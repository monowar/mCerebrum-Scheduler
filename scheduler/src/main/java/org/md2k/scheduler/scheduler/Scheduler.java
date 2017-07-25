package org.md2k.scheduler.scheduler;
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

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.Logger;
import org.md2k.scheduler.action.Actions;
import org.md2k.scheduler.condition.Conditions;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.scheduler.what.What;
import org.md2k.scheduler.scheduler.what.WhatManager;
import org.md2k.scheduler.scheduler.when.When;
import org.md2k.scheduler.task.Tasks;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.*;

public class Scheduler {
    private String id;
    private String type;
    private String title;
    private String summary;
    private String description;
    private String condition;
    private When when;
    private What[][] what;
    private Subscription subscription;
    private boolean isActive;


    private void stop() {
        if(subscription!=null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void start(Context context, String path, Logger logger, AtomicBoolean isRunning, DataKitManager dataKitManager, Conditions conditions, Tasks tasks, Actions actions, Subscriber subscriber) {
        isActive=true;
        subscription = when.getObservable(path, logger, isRunning, dataKitManager, conditions)
                .flatMap(new Func1<Long[], Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Long[] blockTime) {
                        try {
                            logger.write(path, "valid block, block_start=" + DateTime.convertTimeStampToDateTime(blockTime[1])+", block_end=" + DateTime.convertTimeStampToDateTime(blockTime[2]));
                            isRunning.set(true);
                            return new WhatManager().getObservable(context, path, logger, dataKitManager, conditions, what, actions, tasks);
                        } catch (ConfigurationFileFormatError | DataKitAccessError e) {
                            return Observable.error(e);
                        }
                    }
                }).doOnUnsubscribe(() -> isRunning.set(false))
                .subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                isActive=false; isRunning.set(false);
            }

            @Override
            public void onError(Throwable e) {
                isRunning.set(false);subscriber.onError(e);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                isRunning.set(false);subscriber.onNext(aBoolean);
            }
        });
    }

    Observable<Boolean> execute(Context context, String path, Logger logger, AtomicBoolean isRunning, DataKitManager dataKitManager, Conditions conditions, Tasks tasks, Actions actions) throws ConfigurationFileFormatError, DataKitAccessError {
        path=path+"/"+id;
        String finalPath = path;
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    logger.write(finalPath,"running="+isActive);
                    if(conditions.isValid(finalPath, logger, dataKitManager, condition)){
                        if(isActive) {
                            logger.write(finalPath,"status=running, condition=true, operation=do_nothing");
                        }else{
                            logger.write(finalPath,"status=not_running, condition=true, operation=start");
                            start(context, finalPath, logger, isRunning, dataKitManager, conditions, tasks, actions, subscriber);
                        }
                    }else{
                        if(isActive){
                            logger.write(finalPath,"status=running, condition=false, operation=stop");
                            stop();
                        }else{
                            logger.write(finalPath,"status=not_running, condition=false, operation=do_nothing");
                        }
                    }
                } catch (ConfigurationFileFormatError | DataKitAccessError e) {
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(rx.schedulers.Schedulers.computation());
    }

    public When getWhen() {
        return when;
    }

    public What[][] getWhat() {
        return what;
    }
}
