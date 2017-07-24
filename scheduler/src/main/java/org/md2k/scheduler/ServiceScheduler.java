package org.md2k.scheduler;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.scheduler.action.Actions;
import org.md2k.scheduler.condition.Conditions;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.configuration.ConfigurationManager;
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.scheduler.Schedulers;
import org.md2k.scheduler.scheduler.listen.Listen;
import org.md2k.scheduler.scheduler.what.Parameter;
import org.md2k.scheduler.task.Tasks;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.permission.PermissionInfo;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
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
    private Listen listen;
    private Schedulers schedulers;
    private Actions actions;
    private Tasks tasks;
    private Conditions conditions;
    private Subscription subscription;
    private AtomicBoolean isRunning;
    Logger logger;
    String id;

    public void onCreate() {
        super.onCreate();
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.getPermissions(this, result -> {
            if (!result) {
                Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                stop();
            } else {
                start();
            }
        });
    }
    private void stop(){
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        stopSelf();
    }
    private void start() {
        Log.d(TAG,"start()...");
        subscription = Observable.just(true)
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        try {
                            Log.d(TAG,"read_config...");
                            Configuration configuration= new ConfigurationManager().read(ServiceScheduler.this, "SDCARD_INTERNAL", Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.scheduler/"+Constants.CONFIG_FILENAME);
                            id=configuration.getType()+"/"+configuration.getId();
                            listen = configuration.getListen();

                            schedulers=new Schedulers(configuration.getSchedulers());
                            actions=new Actions(configuration.getActions());
                            conditions=new Conditions(configuration.getConditions());
                            tasks=new Tasks(configuration.getTasks());
                            dataKitManager = new DataKitManager();
                            isRunning=new AtomicBoolean(false);
                            Log.d(TAG,"connect_datakit...");
                            return dataKitManager.connect(ServiceScheduler.this).doOnNext(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    try {
                                        create_datasource(dataKitManager, configuration.getCreate_datasource());
                                        logger=new Logger(ServiceScheduler.this, dataKitManager);
                                    } catch (DataKitAccessError ignored) {
                                    }
                                }
                            });
                        } catch (Exception e) {
                            return Observable.error(e);
                        }
                    }
                })
                .flatMap(new Func1<Boolean, Observable<Data>>() {
                    @Override
                    public Observable<Data> call(Boolean aBoolean) {
                        if(listen==null) return Observable.just(null);
                        else
                            return Observable.just(null);
//                            return listen.getObservable(id, logger, dataKitManager);
                    }
                })
                .flatMap(new Func1<Data, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Data data) {
                        //TODO:abc
//                        Parameter[] parameters=new Parameter[]{new Parameter("application","contact2.1.1.1")};
//                        return actions.getObservable(ServiceScheduler.this, ".",logger, "action_notification_ema",parameters,dataKitManager,conditions,tasks);
/*
                            return tasks.get("ema_week1_tuesday").getObservable("incentive",logger, ServiceScheduler.this,dataKitManager, conditions)
                                    .map(s -> {
                                        System.out.println("output:" +s);
                                        return true;
                                    });
*/
                        try {
                            return schedulers.getObservableSchedule(ServiceScheduler.this, id, logger, data, isRunning, dataKitManager, conditions, tasks, actions);
                        } catch (ConfigurationFileFormatError | DataKitAccessError e) {
                            return Observable.error(e);
                        }
                    }
                })
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                        System.out.println("on completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ServiceScheduler.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        stopSelf();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        System.out.println("on next");
                    }
                });
    }
    void create_datasource(DataKitManager dataKitManager, DataSource[] dataSources) throws DataKitAccessError {
        if(dataSources==null) return;
        for (DataSource dataSource : dataSources) {
            dataKitManager.register(dataSource);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "onStartCommand()...");
        startForeground(98763, getCompatNotification());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Notification getCompatNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getResources().getString(R.string.app_name));
        return builder.build();
    }

}
