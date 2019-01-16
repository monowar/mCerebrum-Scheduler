package org.md2k.scheduler.what;
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

import org.md2k.scheduler.State;
import org.md2k.scheduler.condition.ConditionManager;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.configuration2object.Config2Operation;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.logger.MyLogger;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WhatManager {
    private String type;
    private String id;
    private Configuration configuration;
    private Configuration.CWhat[][] cWhats;
    private AtomicBoolean isRunning;
    private HashMap<String, Observable<State>> observables;
    private Random random;
    private String curState;

    public WhatManager(String type, String id, Configuration configuration, Configuration.CWhat[][] cWhats, AtomicBoolean isRunning) {
        this.type = type;
        this.id = id;
        this.configuration = configuration;
        this.cWhats = cWhats;
        this.isRunning = isRunning;
        random=new Random();
    }

    public Observable<State> getObservable(String path, long time) throws ConfigurationFileFormatError {
        String[] path1=new String[1];
        return Observable.just(true).flatMap(new Func1<Boolean, Observable<State>>() {
            @Override
            public Observable<State> call(Boolean aBoolean) {
                path1[0]=path + "/what(" + type + "-" + id+")";
                if (cWhats.length == 0) {
                    DataKitManager.getInstance().insertSystemLog("ERROR", path1[0], "failed: nothing to trigger");
                    return null;
                }
                Configuration.CWhat cWhat = getWhat(path1, cWhats);
                if (cWhat == null) {
                    DataKitManager.getInstance().insertSystemLog("DEBUG", path1[0], "trigger failed: condition=false");
                    return null;
                }
                DataKitManager.getInstance().insertSystemLog("DEBUG", path1[0], "trigger successful: condition=true");
                DataKitManager.getInstance().insert(type, id, "DELIVERED");

//        ActionOperation actionOperation = new ActionOperation(type, id, cWhat.getAction().getTransition(), getHashmap(cWhat.getAction().getTransition(), time), logger);
                return Observable.just(true)
                        .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(Boolean integer) {
                                return DataKitManager.getInstance().connect();
                            }
                        }).observeOn(Schedulers.computation()).flatMap(new Func1<Boolean, Observable<State>>() {
                            @Override
                            public Observable<State> call(Boolean aBoolean) {
                                try {
                                    if (cWhat.getAction().getTransition().length == 0) {
                                        DataKitManager.getInstance().insertSystemLog("ERROR", path1[0]+"/state", "Transision not found");
                                        return Observable.just(new State(State.STATE.OUTPUT, "OK"));
                                    } else
                                        curState = cWhat.getAction().getTransition()[0][0];

                                    observables = getHashmap(path1[0]+"/state("+curState+")", cWhat.getAction().getTransition());
                                    return getObservable(path1, cWhat.getAction().getTransition());
                                } catch (ConfigurationFileFormatError configurationFileFormatError) {
                                    DataKitManager.getInstance().insertSystemLog("ERROR", path1[0], "Error: Configuration file formate error");
                                    return Observable.error(new ConfigurationFileFormatError());
                                }
                            }
                        }).doOnUnsubscribe(new Action0() {
                            @Override
                            public void call() {
                                isRunning.set(false);
                            }
                        }).doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                DataKitManager.getInstance().insertSystemLog("ERROR", path1[0], "exception e="+throwable.getMessage());
                                isRunning.set(false);
                            }
                        }).doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                DataKitManager.getInstance().insertSystemLog("DEBUG", path1[0], "completed");
                                isRunning.set(false);
                            }
                        });
            }
        });

    }

    private HashMap<String, Observable<State>> getHashmap(String path, String[][] transition) throws ConfigurationFileFormatError {
        HashMap<String, Observable<State>> hashMap = new HashMap<>();
        for (String[] aTransition : transition) {
            String a = aTransition[0];
            if (a != null && a.length() != 0 && !hashMap.containsKey(a))
                hashMap.put(a, Config2Operation.getObservable(path, type, id, configuration, a));
            a = aTransition[2];
            if (a != null && a.length() != 0 && !hashMap.containsKey(a))
                hashMap.put(a, Config2Operation.getObservable(path, type, id, configuration, a));
        }
        return hashMap;
    }

    private Configuration.CWhat getWhat(String[] path, Configuration.CWhat[][] cWhats) {
        int r=0;

        if(cWhats.length>1){
            r = random.nextInt(cWhats.length*10)%cWhats.length;
            if(cWhats.length==2) {
                int x0 = MyLogger.getInstance().getRandomTry(type + "_" + id + "_0");
                int x1 = MyLogger.getInstance().getRandomTry(type + "_" + id + "_1");
                if (x0 - x1 >= 2)
                    r = 1;
                if (x1 - x0 >= 2) r = 0;
                if(r==0)
                    MyLogger.getInstance().setRandomTry(type+"_"+id+"_0", x0+1);
                else
                    MyLogger.getInstance().setRandomTry(type+"_"+id+"_1", x1+1);
            }
            DataKitManager.getInstance().insertSystemLog("DEBUG", path[0], "random selection=" + String.valueOf(r));
        }
        for (int i = 0; i < cWhats[r].length; i++)
            if (ConditionManager.getInstance().isTrue(cWhats[r][i].getCondition())) {
                DataKitManager.getInstance().insertSystemLog("DEBUG", path[0]+"/condition", "true: index["+String.valueOf(r)+" "+String.valueOf(i)+"] condition="+cWhats[r][i].getCondition());
                path[0]+="["+String.valueOf(r)+" "+String.valueOf(i)+"]";
                return cWhats[r][i];
            }
        return null;
    }


    public Observable<State> getObservable(String[] path, String[][] transition) {
        return Observable.just(null)
                .flatMap(integer -> {
                    DataKitManager.getInstance().insertSystemLog("DEBUG", path[0]+"/state(" + curState + ")", "delivered");
                    if (curState == null) {
                        return Observable.just(new State(State.STATE.OUTPUT, "OK"));
                    } else
                        return observables.get(curState);
                })
                .filter(new Func1<State, Boolean>() {
                    @Override
                    public Boolean call(State state) {
                        if (state.getState() == State.STATE.OUTPUT) return true;
                        return false;
                    }
                }).flatMap(new Func1<State, Observable<State>>() {
                    @Override
                    public Observable<State> call(State state) {
                        String nextState = null;
                        for (String[] aTransition : transition) {
                            if (aTransition[0].equalsIgnoreCase(curState) && aTransition[1].equalsIgnoreCase(state.getMessage())) {
                                nextState = aTransition[2];
                                break;
                            }
                        }
                        DataKitManager.getInstance().insertSystemLog("DEBUG", path[0]+"/state(" + curState + ")", "response="+state.getMessage()+" nextstate="+nextState);

                        DataKitManager.getInstance().insert(type + "_" + id, "STATUS", curState + "," + state.getMessage() + "," + nextState);

                        curState = nextState;
                        if (curState == null) return Observable.error(new Throwable("done"));
                        else return Observable.error(new Throwable("continue"));
                    }
                }).retryWhen(errors -> errors.flatMap(error -> {
                    if (error.getMessage().equals("done")) {
                        DataKitManager.getInstance().insertSystemLog("DEBUG", path[0]+"/state(" + curState + ")", "completed");
                        return Observable.error(error);
                    } else {
                        DataKitManager.getInstance().insertSystemLog("DEBUG", path[0]+"/state(" + curState + ")", "continue to next state");
                        return Observable.just(null);
                    }
                })).onErrorReturn(new Func1<Throwable, State>() {
                    @Override
                    public State call(Throwable throwable) {
                        return new State(State.STATE.OUTPUT, curState);
                    }
                });
    }

}
