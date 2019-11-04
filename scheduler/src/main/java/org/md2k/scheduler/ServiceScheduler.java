package org.md2k.scheduler;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;


import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.configuration.ConfigurationManager;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.listen.Listen;
import org.md2k.scheduler.listen.ListenData;
import org.md2k.scheduler.logger.MyLogger;
import org.md2k.scheduler.resetapp.ResetApp;
import org.md2k.scheduler.resetapp.ResetCallback;
import org.md2k.scheduler.scheduler.Scheduler;
import org.md2k.scheduler.what.WhatManager;
import org.md2k.scheduler.when.WhenManager;

import java.util.concurrent.TimeUnit;
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
    ResetCallback resetCallback=new ResetCallback() {
        @Override
        public void onReset() {
            unsubscribe();
            subscribe();
        }
    };
    ResetApp resetApp;
    private Subscription subscription;
    private Configuration configuration;
    private Scheduler[] schedulers;
    private AtomicBoolean isRunning;
    PowerManager pm;
    PowerManager.WakeLock wl;

    private Listen listen;


    public void onCreate() {
        super.onCreate();

        LocalBroadcastManager.getInstance(MyApplication.getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("DATAKIT_ERROR"));
        try {
            DataKitAPI.getInstance(this).connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    String[] res = MyLogger.getInstance().getDataKitErrorMessage();
                    if(res!=null && res.length==3 && res[0]!=null && res[1]!=null && res[2]!=null){
                        DataKitManager.getInstance().insertSystemLog("DEBUG", "BackgroundService","last error message = "+res[0]+" "+res[1]+" "+res[2]);
                        MyLogger.getInstance().clearDataKitErrorMessage();
                    }
                    DataKitManager.getInstance().insertSystemLog("DEBUG", "BackgroundService","onCreate()");

                    try {
                        init();
                        createObjects();
                        subscribe();
                    } catch (Exception e) {
                        DataKitManager.getInstance().insertSystemLog("DEBUG","BackgroundService","Error: e="+e.getMessage());
                        stopSelf();
                    }
                }
            });
        } catch (DataKitException e) {
//            Toast.makeText(this, "Datakit connection failed: e="+e.getMessage(), Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }
    void init() throws Exception {
//        configuration = ConfigurationManager.readMoffitt();
        configuration = ConfigurationManager.read();

        if (configuration == null) throw new Exception("Invalid/No configuration file");
        try {
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if(pm==null) throw new Exception("Wakelock service not found");
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "scheduler:Wakelock");
            wl.acquire();
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
        resetApp=new ResetApp(resetCallback);
        resetApp.start();

    }

    void subscribe() {
        DataKitManager.getInstance().insertSystemLog("DEBUG","BackgroundService","Scheduler Started");
        subscription = Observable.just(true).flatMap(new Func1<Boolean, Observable<ListenData>>() {
                    @Override
                    public Observable<ListenData> call(Boolean aBoolean) {
                        return Observable.merge(Observable.just(null), listen.getObservable("BackgroundService"));
                    }
                }).doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        DataKitManager.getInstance().insertSystemLog("DEBUG","BackgroundService","Scheduler Stopping");
                        try {
                            for (Scheduler scheduler : schedulers) scheduler.stop("BackgroundService");
                        } catch (Exception ignored) {
                            DataKitManager.getInstance().insertSystemLog("ERROR","BackgroundService","Scheduler Stopping error e="+ignored.getMessage());
                        }
                    }
        }).flatMap(new Func1<ListenData, Observable<ListenData>>() {
            @Override
            public Observable<ListenData> call(ListenData listenData) {
                int[] count = new int[]{0};
                return Observable.interval(0,1, TimeUnit.SECONDS).filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        count[0]++;
                        if(count[0]>60) return true;
                        if(isRunning.get()==true) return false;
                        return true;
                    }
                }).take(1).map(new Func1<Long, ListenData>() {
                    @Override
                    public ListenData call(Long aLong) {
                        return listenData;
                    }
                });
            }
        }).subscribe(new Observer<ListenData>() {
            @Override
            public void onCompleted() {
                DataKitManager.getInstance().insertSystemLog("DEBUG","BackgroundService","Scheduler Completed");
                stopSelf();
            }

            @Override
            public void onError(Throwable e) {
                DataKitManager.getInstance().insertSystemLog("ERROR","BackgroundService","Scheduler Error e="+e.getMessage());
                stopSelf();
            }

            @Override
            public void onNext(ListenData listenData) {
                String type;
                if (listenData == null)
                    type = "init";
                else type = listenData.toString();
                DataKitManager.getInstance().insertSystemLog("DEBUG","BackgroundService", "Listen returns: "+type + " isRunning=" + isRunning.get());
                if (isRunning.get() == false)
                    for (Scheduler scheduler : schedulers) scheduler.restartIfMatch("BackgroundService/Listen("+type+")", listenData);
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
        listen = new Listen();
        isRunning = new AtomicBoolean();
        isRunning.set(false);
        for (int i = 0; i < configuration.getScheduler_list().length; i++) {
            String type = configuration.getScheduler_list()[i].getType();
            String id = configuration.getScheduler_list()[i].getId();
            WhenManager whenManager = new WhenManager(type, id, configuration.getScheduler_list()[i].getWhen(), isRunning);
            WhatManager whatManager = new WhatManager(type, id, configuration, configuration.getScheduler_list()[i].getWhat(), isRunning);
            schedulers[i] = new Scheduler(type, id, configuration.getScheduler_list()[i].getListen(), whenManager, whatManager);
            addListen(configuration.getScheduler_list()[i].getListen());
        }
    }

    void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

    }

    @Override
    public void onDestroy() {
        try {
            wl.release();
        }catch (Exception e){}
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        if(DataKitManager.getInstance().isConnected()){
            DataKitManager.getInstance().insertSystemLog("DEBUG","BackgroundService","OnDestroy");
        }
        DataKitManager.getInstance().disconnect();
        unsubscribe();
        stopForegroundService();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
            unsubscribe();
            stopSelf();
        }
    };
    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Scheduler app running...");


        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService() {

        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

    }
}
