package org.md2k.scheduler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.scheduler.condition.ConditionManager;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.configuration.ConfigurationManager;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.PermissionError;
import org.md2k.scheduler.listen.Listen;
import org.md2k.scheduler.listen.ListenData;
import org.md2k.scheduler.logger.Logger;
import org.md2k.scheduler.scheduler.Scheduler;
import org.md2k.scheduler.what.WhatManager;
import org.md2k.scheduler.when.WhenManager;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
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

public class ServiceScheduler extends Service {
    private static final String TAG = ServiceScheduler.class.getSimpleName();

    private DataKitManager dataKitManager;
    private Subscription subscription;
    private ConditionManager conditionManager;
    private Logger logger;
    private String id;
    private Configuration configuration;
    private Scheduler[] schedulers;
    private AtomicBoolean isRunning;

    private Listen listen;

    public void onCreate() {
        super.onCreate();
        subscription = Observable.just(true).flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean aBoolean) {
                if (!Permission.hasPermission(ServiceScheduler.this))
                    return Observable.error(new PermissionError());
                configuration = ConfigurationManager.readROBAS(ServiceScheduler.this);
                if (configuration == null)
                    return Observable.error(new ConfigurationFileFormatError());
                dataKitManager = new DataKitManager();
                return dataKitManager.connect(ServiceScheduler.this);
            }
        }).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                createObjects();
                return null;
            }
        }).flatMap(new Func1<Boolean, Observable<ListenData>>() {
            @Override
            public Observable<ListenData> call(Boolean aBoolean) {
                return Observable.merge(Observable.just(null), listen.getObservable());
            }
        }).map(new Func1<ListenData, Boolean>() {
            @Override
            public Boolean call(ListenData listenData) {
                for (Scheduler scheduler : schedulers) scheduler.restartIfMatch(listenData);
                return true;
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                try {
                    for (Scheduler scheduler : schedulers) scheduler.stop();
                }catch (Exception ignored){}
            }
        }).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d("abc","onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("abc","onError()..e="+e.toString());
            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.d("abc","onNext()");
            }
        });
    }

    void addListen(Configuration.CListen cListen) {
        if (cListen == null) return;
        for (int i = 0; cListen.getDatasource() != null && i < cListen.getDatasource().length; i++)
            listen.add(cListen.getDatasource()[i]);
        for (int i = 0; cListen.getTime() != null && i < cListen.getTime().length; i++)
            listen.add(cListen.getTime()[i]);
    }

    void createObjects() {
        schedulers = new Scheduler[configuration.getScheduler_list().length];
        listen = new Listen(dataKitManager);
        conditionManager=new ConditionManager(dataKitManager);
        logger = new Logger(this, dataKitManager);
        isRunning=new AtomicBoolean();
        isRunning.set(false);
        for (int i = 0; i < configuration.getScheduler_list().length; i++) {
            String type = configuration.getScheduler_list()[i].getType();
            String id = configuration.getScheduler_list()[i].getId();
            WhenManager whenManager = new WhenManager(type, id, configuration.getScheduler_list()[i].getWhen(), conditionManager, logger, isRunning);
            WhatManager whatManager = new WhatManager(type, id, this, configuration,configuration.getScheduler_list()[i].getWhat(), dataKitManager, conditionManager, logger, isRunning);
            schedulers[i] = new Scheduler(type, id, configuration.getScheduler_list()[i].getListen(), whenManager, whatManager, logger);
            addListen(configuration.getScheduler_list()[i].getListen());
        }
    }

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
