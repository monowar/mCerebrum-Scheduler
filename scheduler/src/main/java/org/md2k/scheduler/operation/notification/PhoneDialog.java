package org.md2k.scheduler.operation.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.MyApplication;
import org.md2k.scheduler.State;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.operation.AbstractOperation;
import org.md2k.scheduler.time.Time;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
public class PhoneDialog extends AbstractOperation {
    private String title, content;
    private String[] buttons;
    private boolean[] confirm;
    private long at;
    private long interval;
    private BroadcastReceiver mMessageReceiver;
    private String base;

    public PhoneDialog(String title, String content, String[] buttons, boolean[] confirm, long at, long interval, String base) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
        this.confirm = confirm;
        this.at = at;
        this.interval = interval;
        this.base = base;
    }

    @Override
    public Observable<State> getObservable(String path, String _type, String _id) {
        return Observable.from(new Long[]{at})
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long delay) {
                        if (base != null) {
                            long dl;
                            long trigTime = delay + Time.getToday() + Time.getTime(base);
                            long curTime = DateTime.getDateTime();
                            if (trigTime > curTime) dl = trigTime - curTime;
                            else if (trigTime + 5000 > curTime) dl = 0L;
                            else dl = -1L;
                            return dl;

                        } else {
                            if (delay <= 0) delay = 0L;
                            DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/dialog", "at: "+DateTime.convertTimeStampToDateTime(DateTime.getDateTime()+delay));
                            return delay;
                        }
                    }
                })
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long value) {
                        if (value < 0) return false;
                        else return true;
                    }
                }).flatMap(new Func1<Long, Observable<State>>() {
                    @Override
                    public Observable<State> call(Long delay) {
                        return Observable.timer(delay, TimeUnit.MILLISECONDS)
                                .flatMap(new Func1<Long, Observable<State>>() {
                                    @Override
                                    public Observable<State> call(Long aLong) {
                                            return Observable.create(new Observable.OnSubscribe<State>() {
                                                @Override
                                                public void call(Subscriber<? super State> subscriber) {
                                                    DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/dialog", "showing...timeout="+String.valueOf((at + interval)/1000)+" seconds");
                                                    showDialog(path, subscriber);
                                                }
                                            });
                                    }
                                }).timeout(at+interval, TimeUnit.MILLISECONDS).onErrorReturn(new Func1<Throwable, State>() {
                                    @Override
                                    public State call(Throwable throwable) {
                                        DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/dialog", "timeout occurs");
                                        stop();
                                        return new State(State.STATE.OUTPUT, "MISSED");
                                    }
                                });
                    }
                }).doOnUnsubscribe(() -> {
                    stop();
                });
    }

    private void stop() {
        try {
            LocalBroadcastManager.getInstance(MyApplication.getContext()).unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {
            Log.e("abc", "PhoneDialog()..stop()..unregister_broadcast failed");
        }
        try {
            ActivityDialog.fa.finish();
        } catch (Exception e) {
        }
    }

    private void showDialog(String path, Subscriber<? super State> subscriber) {

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                String message = intent.getStringExtra(ActivityDialog.RESULT);
                DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/dialog", "response="+message);
                subscriber.onNext(new State(State.STATE.OUTPUT, message));
                subscriber.onCompleted();
            }
        };
        LocalBroadcastManager.getInstance(MyApplication.getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(ActivityDialog.INTENT_RESULT));

        Intent intent = new Intent(MyApplication.getContext(), ActivityDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ActivityDialog.TITLE, title);
        intent.putExtra(ActivityDialog.CONTENT, content);
        intent.putExtra(ActivityDialog.BUTTONS, buttons);
        intent.putExtra(ActivityDialog.CONFIRM, confirm);
        MyApplication.getContext().startActivity(intent);
    }
}