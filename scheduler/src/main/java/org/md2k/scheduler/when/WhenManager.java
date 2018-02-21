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
import org.md2k.scheduler.exception.InvalidBlock;
import org.md2k.scheduler.exception.SchedulerFailedError;
import org.md2k.scheduler.exception.SchedulerNotFound;
import org.md2k.scheduler.logger.Logger;
import org.md2k.scheduler.time.Time;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.functions.Func1;

public class WhenManager{
    private String type, id;
    private Configuration.CWhen[] cWhenList;
    private ConditionManager conditionManager;
//    private int[] tries;
    private AtomicBoolean isRunning;
    private Logger logger;

    public WhenManager(String type, String id, Configuration.CWhen[] cWhenList, ConditionManager conditionManager, Logger logger, AtomicBoolean isRunning) {
        this.type = type;
        this.id = id;
        this.cWhenList = cWhenList;
        this.conditionManager = conditionManager;
        this.logger = logger;
/*
        tries = new int[cWhenList.length];
        for (int i = 0; i < cWhenList.length; i++)
            tries[i] = -1;
*/
        this.isRunning = isRunning;
    }

    public Observable<State> getObservable() {
        return Observable.range(0, cWhenList.length)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer index) {
                        if (!conditionManager.isTrue(cWhenList[index].getCondition())) {
                            logger.write(type+"/"+id+"/when["+Integer.toString(index)+"]/condition["+cWhenList[index].getCondition()+"]", "FAILED: block_condition=false");
                            return Observable.error(new InvalidBlock());
                        }
                        logger.write(type+"/"+id+"/when["+Integer.toString(index)+"]/condition["+cWhenList[index].getCondition()+"]", "TRY: block_condition=true");
                        return getObservable(index);
                    }
                })
                .map(cTriggerRule -> new State(State.STATE.OUTPUT, "OK")).onErrorReturn(throwable -> null).filter(longs -> longs != null);
    }

    private Observable<Integer> getObservable(int index) {
        return Observable.just(true)
                .flatMap(aBoolean -> {
                    long startTime = conditionManager.evaluate(cWhenList[index].getStart_time()).longValue();
                    long endTime = conditionManager.evaluate(cWhenList[index].getEnd_time()).longValue();
                    long now = DateTime.getDateTime();
                    if (endTime < now) {
                        logger.write(type + "/" + id + "/when[" + Integer.toString(index) + "]/block[start:" + DateTime.convertTimeStampToDateTime(startTime) + ", end:" + DateTime.convertTimeStampToDateTime(endTime) + "]", "FAILED: current time > block_end");
                        return Observable.error(new Throwable(new InvalidBlock()));
                    } else if(startTime<now) {
                        logger.write(type + "/" + id + "/when[" + Integer.toString(index) + "]/block[start:" + DateTime.convertTimeStampToDateTime(startTime) + ", end:" + DateTime.convertTimeStampToDateTime(endTime) + "]", "TRY: block_start < current time < block_end");
                        return Observable.just(true);
                    }
                    else {
                        logger.write(type + "/" + id + "/when[" + Integer.toString(index) + "]/block[start:" + DateTime.convertTimeStampToDateTime(startTime) + ", end:" + DateTime.convertTimeStampToDateTime(endTime) + "]", "WAIT: current time< block start: trigger time="+DateTime.convertTimeStampToDateTime(startTime));
                        return Observable.just(true).delay(now-startTime, TimeUnit.MILLISECONDS).map(integer -> true);
                    }
                })
                .flatMap(b -> {
                    int tries = logger.getNumberOfTry(type, id, Time.getToday(), index);
                    logger.setNumberOfTry(type, id, Time.getToday(), index, tries+1);
                    int curIndex = tries;
                    if (curIndex >= cWhenList[index].getTrigger_rule().length) {
                        curIndex = cWhenList[index].getTrigger_rule().length - 1;
                        if (cWhenList[index].getTrigger_rule()[curIndex].getRetry_after() == null) {
                            logger.write(type+"/"+id+"/when["+Integer.toString(index)+"]/try["+Integer.toString(tries+1)+"]", "FAILED: retry option not available");
                            return Observable.error(new SchedulerNotFound());
                        }
                    }
                    long time = conditionManager.evaluate(cWhenList[index].getTrigger_rule()[curIndex].getTrigger_time()).longValue();
                    long curTime = DateTime.getDateTime();
                    if (curTime < time) {
                        logger.write(type+"/"+id+"/when["+Integer.toString(index)+"]/try["+Integer.toString(tries+1)+"]/at", "Trigger at: "+DateTime.convertTimeStampToDateTime(time));
                        return Observable.just(cWhenList[index].getTrigger_rule()[curIndex]).delay(time - curTime, TimeUnit.MILLISECONDS);
                    }
                    else {
                        logger.write(type+"/"+id+"/when["+Integer.toString(index)+"]/try["+Integer.toString(tries+1)+"]/at", "Trigger now");
                        return Observable.just(cWhenList[index].getTrigger_rule()[curIndex]);
                    }
                }).flatMap(new Func1<Configuration.CTriggerRule, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Configuration.CTriggerRule rule) {
                        if (rule == null) return Observable.error(new SchedulerNotFound());
                        boolean condition = conditionManager.isTrue(rule.getCondition());
                        if (condition && !isRunning.get()) {
                            logger.write(type+"/"+id+"/when["+Integer.toString(index)+"]/condition["+rule.getCondition()+"=true]/running[false]", "deliver");
                            return Observable.just(index);
                        } else {
                            logger.write(type+"/"+id+"/when["+Integer.toString(index)+"]/condition["+rule.getCondition()+"="+Boolean.toString(condition)+"]/running["+Boolean.toString(isRunning.get())+"]", "deliver failed, reschedule");
                            return Observable.error(new SchedulerFailedError());
                        }
                    }
                }).retryWhen(errors -> errors.flatMap(new Func1<Throwable, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Throwable throwable) {
                        if (throwable instanceof SchedulerFailedError) {
                            int curIndex = logger.getNumberOfTry(type, id, Time.getToday(), index);
                            if (curIndex >= cWhenList[index].getTrigger_rule().length) {
                                curIndex = cWhenList[index].getTrigger_rule().length - 1;
                                if (cWhenList[index].getTrigger_rule()[curIndex].getRetry_after() == null) {
                                    return Observable.error(new SchedulerNotFound());
                                }
                            }
                            long time = conditionManager.evaluate(cWhenList[index].getTrigger_rule()[curIndex].getTrigger_time()).longValue();
                            return Observable.just(true).delay(time, TimeUnit.MILLISECONDS);
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                }));
    }

}
