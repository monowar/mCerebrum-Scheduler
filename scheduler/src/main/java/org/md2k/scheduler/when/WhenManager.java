package org.md2k.scheduler.when;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.State;
import org.md2k.scheduler.condition.ConditionManager;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.SchedulerFailedError;
import org.md2k.scheduler.logger.MyLogger;
import org.md2k.scheduler.time.Time;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WhenManager {
    private String type, id;
    private Configuration.CWhen[] cWhenList;
    private AtomicBoolean isRunning;

    public WhenManager(String type, String id, Configuration.CWhen[] cWhenList, AtomicBoolean isRunning) {
        this.type = type;
        this.id = id;
        this.cWhenList = cWhenList;
        this.isRunning = isRunning;
    }
    private boolean checkCondition(String path, String condition){
        ArrayList<String> details = new ArrayList<>();
        boolean conditionResult= ConditionManager.getInstance().isTrue(condition, details);
        DataKitManager.getInstance().insertSystemLogCondition(path, details);
        String s = "";
        for(int i=0;i<details.size();i+=3) {
            if(i+2>=details.size()) continue;
            s += details.get(i+1).replace(",",";") + "="+details.get(i+2).replace(",",";")+";";
        }
        DataKitManager.getInstance().insertSystemLog("DEBUG", path,"Condition="+String.valueOf(condition)+" ["+s+"]");
        return conditionResult;
    }

    public Observable<State> getObservable(String path) {
        String[] path1 = new String[1];
        return Observable.range(0, cWhenList.length)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        return DataKitManager.getInstance().connect().map(new Func1<Boolean, Integer>() {
                            @Override
                            public Integer call(Boolean aBoolean) {
                                return integer;
                            }
                        });
                    }
                }).observeOn(Schedulers.computation())
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer index) {
                        path1[0]=path+"/when("+type + "-" + id+"-"+Integer.toString(index)+")";
                        DataKitManager.getInstance().insertSystemLog("DEBUG", path1[0],"start");
                        boolean condition = checkCondition(path1[0]+"/precondition", cWhenList[index].getCondition());
                        DataKitManager.getInstance().insertSystemLog("DEBUG", path1[0],"precondition="+String.valueOf(condition));

                        if (!condition) {
                            DataKitManager.getInstance().insertSystemLog("DEBUG", path1[0],"not scheduled due to precondition failed");
                            return Observable.just(null);
                        }
                        else
                            return getObservable(path1[0],index);
                    }
                }).filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        if (integer == null) return false;
                        return true;
                    }
                })
                .map(new Func1<Integer, State>() {
                    @Override
                    public State call(Integer cTriggerRule) {
                        return new State(State.STATE.OUTPUT, "OK");
                    }
                })
                .onErrorReturn(new Func1<Throwable, State>() {
                    @Override
                    public State call(Throwable throwable) {
                        DataKitManager.getInstance().insertSystemLog("ERROR", path1[0],"not scheduled due to error e="+throwable.getMessage());
                        return null;
                    }
                }).filter((State state) -> {
                    return state != null;
                });
    }

    private Observable<Integer> getObservable(String path, int index) {
        final String[] logId = new String[2];
        return Observable.just(true).flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean integer) {
                return DataKitManager.getInstance().connect();
            }
        }).observeOn(Schedulers.computation()).flatMap(aBoolean -> {
                    long startTime = ConditionManager.getInstance().evaluate(cWhenList[index].getStart_time()).longValue();
                    long endTime = ConditionManager.getInstance().evaluate(cWhenList[index].getEnd_time()).longValue();
                    logId[0]=path+"/block("+DateTime.convertTimeStampToDateTime(startTime)+"-"+DateTime.convertTimeStampToDateTime(endTime)+")";
                    long now = DateTime.getDateTime();
                    if (endTime < now) {
                        DataKitManager.getInstance().insertSystemLog("DEBUG",logId[0], "invalid block [current time > block_end]");
                        return Observable.just(false);
                    } else if (startTime < now) {
                        DataKitManager.getInstance().insertSystemLog("DEBUG",logId[0], "valid block [block_start <current time < block_end]");
                        return Observable.just(true);
                    } else {
                        DataKitManager.getInstance().insertSystemLog("DEBUG",logId[0], "valid block future [block start at: "+DateTime.convertTimeStampToDateTime(startTime)+"]");
                        return Observable.timer(startTime - now, TimeUnit.MILLISECONDS).map(integer -> true);
                    }
                }).filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean;
                    }
                })
                .flatMap(b -> {
                    long curTime = DateTime.getDateTime();
                    long nextTriggerTime = MyLogger.getInstance().getLastScheduleTime(type, id, index);
                    int tries = MyLogger.getInstance().getNumberOfTry(type, id, Time.getToday(), index);
                    int curIndex = tries;
                    if (curIndex >= cWhenList[index].getTrigger_rule().length)
                        curIndex = cWhenList[index].getTrigger_rule().length - 1;
                    while (nextTriggerTime < curTime || tries == -1) {
                        tries++;
                        MyLogger.getInstance().setNumberOfTry(type, id, Time.getToday(), index, tries);
                        curIndex = tries;
                        if (curIndex >= cWhenList[index].getTrigger_rule().length) {
                            curIndex = cWhenList[index].getTrigger_rule().length - 1;
                            if (cWhenList[index].getTrigger_rule()[curIndex].getRetry_after() == null) {
                                DataKitManager.getInstance().insertSystemLog("DEBUG", logId[0] + "/try[" + Integer.toString(tries + 1) + "]", "not scheduled: retry option not available");
                                return Observable.just(null);
                            }
                        }
                        nextTriggerTime = ConditionManager.getInstance().evaluate(cWhenList[index].getTrigger_rule()[curIndex].getTrigger_time()).longValue();
                        if (tries != curIndex) {
                            nextTriggerTime += ConditionManager.getInstance().evaluate(cWhenList[index].getTrigger_rule()[curIndex].getRetry_after()).longValue();
                        }
//                        Log.d("aaa", "[" + type + "_" + id + "_" + index + "] block new try trigger time=" + DateTime.convertTimeStampToDateTime(nextTriggerTime));
                        MyLogger.getInstance().setLastScheduleTime(type, id, index, nextTriggerTime);
                    }
                    long endTime = ConditionManager.getInstance().evaluate(cWhenList[index].getEnd_time()).longValue();
                    if(nextTriggerTime>=endTime){
                        DataKitManager.getInstance().insertSystemLog("DEBUG",logId[0] + "/try[" + Integer.toString(tries + 1) + "]", "not scheduled: block ended when next scheduled..next schedule="+DateTime.convertTimeStampToDateTime(nextTriggerTime));
                        return Observable.just(null);
                    }
                    logId[1]=logId[0]+ "/try[" + Integer.toString(tries + 1) + "]";
                    DataKitManager.getInstance().insertSystemLog("DEBUG", logId[1], "scheduled at: " + DateTime.convertTimeStampToDateTime(nextTriggerTime));
                    int finalCurIndex = curIndex;
                    long timeDiff = nextTriggerTime - DateTime.getDateTime();
                    if(timeDiff < 0) timeDiff = 0;
                    return Observable.timer(timeDiff, TimeUnit.MILLISECONDS).map(new Func1<Long, Configuration.CTriggerRule>() {
                        @Override
                        public Configuration.CTriggerRule call(Long aLong) {
                            return cWhenList[index].getTrigger_rule()[finalCurIndex];
                        }
                    });
//                    return Observable.just(cWhenList[index].getTrigger_rule()[curIndex]).delay(nextTriggerTime - DateTime.getDateTime() , TimeUnit.MILLISECONDS);
                }).flatMap(new Func1<Configuration.CTriggerRule, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Configuration.CTriggerRule rule) {
                        if (rule == null) {
                            DataKitManager.getInstance().insertSystemLog("DEBUG", logId[1], "not scheduled: trigger rule not found");
                            return Observable.just(null);
                        }
                        boolean condition = checkCondition(logId[1]+"/condition", rule.getCondition());

                        if (condition && !isRunning.get()) {
                            isRunning.set(true);
                            DataKitManager.getInstance().insertSystemLog("DEBUG", logId[1]+"/trigger", "success [condition=true is_running=false]");
                            return Observable.just(index);
                        } else {
                            DataKitManager.getInstance().insertSystemLog("DEBUG", logId[1]+"/trigger", "failed [condition="+String.valueOf(condition)+" is_running="+String.valueOf(isRunning.get())+"]");
                            return Observable.error(new SchedulerFailedError());
                        }
                    }
                }).retryWhen(errors -> errors.flatMap(new Func1<Throwable, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Throwable throwable) {
                        if (throwable instanceof SchedulerFailedError) {
                            DataKitManager.getInstance().insertSystemLog("DEBUG", logId[1]+"/trigger", "retry");
                            return Observable.just(true);
                        } else {
                            DataKitManager.getInstance().insertSystemLog("DEBUG", logId[1]+"/trigger", "failed and stopped [e="+throwable.getMessage()+"]");
                            return Observable.error(throwable);
                        }
                    }
                }));
    }

}
