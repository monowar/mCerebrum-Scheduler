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

import android.content.Context;
import android.util.Log;

import org.md2k.scheduler.State;
import org.md2k.scheduler.condition.ConditionManager;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.configuration2object.Config2Operation;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.logger.Logger;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class WhatManager {
    private String type;
    private String id;
    private Context context;
    private DataKitManager dataKitManager;
    private ConditionManager conditionManager;
    private Configuration configuration;
    private Configuration.CWhat[][] cWhats;
    private AtomicBoolean isRunning;
    private Logger logger;
    private HashMap<String, Observable<State>> observables;

    public WhatManager(String type, String id, Context context, Configuration configuration, Configuration.CWhat[][] cWhats, DataKitManager dataKitManager, ConditionManager conditionManager, Logger logger, AtomicBoolean isRunning) {
        this.type = type;
        this.id = id;
        this.context = context;
        this.configuration = configuration;
        this.cWhats = cWhats;
        this.dataKitManager = dataKitManager;
        this.conditionManager = conditionManager;
        this.isRunning = isRunning;
        this.logger = logger;
    }

    public Observable<State> getObservable(long time) throws ConfigurationFileFormatError {
        if (cWhats.length == 0) return null;

        Configuration.CWhat cWhat = getWhat(cWhats);
        if (cWhat == null) return null;
//        ActionOperation actionOperation = new ActionOperation(type, id, cWhat.getAction().getTransition(), getHashmap(cWhat.getAction().getTransition(), time), logger);
        return Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                isRunning.set(true);
                return true;
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                isRunning.set(false);
            }
        }).flatMap(new Func1<Boolean, Observable<State>>() {
            @Override
            public Observable<State> call(Boolean aBoolean) {
                try {
                    observables = getHashmap(cWhat.getAction().getTransition());
                    return getObservable(cWhat.getAction().getTransition());
                } catch (ConfigurationFileFormatError configurationFileFormatError) {
                    return Observable.error(new ConfigurationFileFormatError());
                }
            }
        });
    }

    private HashMap<String, Observable<State>> getHashmap(String[][] transition) throws ConfigurationFileFormatError {
        HashMap<String, Observable<State>> hashMap = new HashMap<>();
        for (String[] aTransition : transition) {
            String a = aTransition[0];
            if (!hashMap.containsKey(a))
                hashMap.put(a, Config2Operation.getObservable(context,type,id, dataKitManager, configuration, conditionManager, a));
            a = aTransition[2];
            if (!hashMap.containsKey(a))
                hashMap.put(a, Config2Operation.getObservable(context, type,id, dataKitManager, configuration, conditionManager, a));
        }
        return hashMap;
    }

    private Configuration.CWhat getWhat(Configuration.CWhat[][] cWhats) {
        int r = new Random().nextInt(cWhats.length);
        for (int i = 0; i < cWhats[r].length; i++)
            if (conditionManager.isTrue(cWhats[r][i].getCondition())) {
                logger.write(type + "/" + id + "/what", "condition=[" + cWhats[r][i] + "]=true, what [" + r + "," + i + "]");
                return cWhats[r][i];
            }
        return null;
    }

    private String curState;

    public Observable<State> getObservable(String[][] transition) {
        this.curState = transition[0][0];
        return Observable.just(null)
                .flatMap(integer -> {
                    logger.write(type + "/" + id + "/what/state[" + curState + "]", "delivered");
                    dataKitManager.insert(type,id,"DELIVERED");
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
                        logger.write(type + "/" + id + "/what/state[" + curState + "]/response", state.getMessage());
                        String nextState = null;
                        for (String[] aTransition : transition) {
                            if (aTransition[0].equalsIgnoreCase(curState) && aTransition[1].equalsIgnoreCase(state.getMessage())) {
                                nextState = aTransition[2];
                                break;
                            }
                        }
                        dataKitManager.insert(type+"_"+id,"STATUS",curState+","+state.getMessage()+","+nextState);
                        Log.d("abc",type+"_"+id+" ...STATUS..."+curState+","+state.getMessage()+","+nextState);

                        curState = nextState;
                        if (curState == null) return Observable.error(new Throwable("done"));
                        else return Observable.error(new Throwable("continue"));
                    }
                }).retryWhen(errors -> errors.flatMap(error -> {

                    if (error.getMessage().equals("done")) {
                        return Observable.error(error);
                    } else
                        return Observable.just(null);
                })).onErrorReturn(new Func1<Throwable, State>() {
                    @Override
                    public State call(Throwable throwable) {
                        Log.d("abc", "onerrorreturn=" + throwable.toString());
                        return new State(State.STATE.OUTPUT, curState);
                    }
                });
    }

}
