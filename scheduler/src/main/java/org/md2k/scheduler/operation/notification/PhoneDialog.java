package org.md2k.scheduler.operation.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.State;
import org.md2k.scheduler.operation.AbstractOperation;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

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

    public PhoneDialog(String title, String content, String[] buttons, boolean[] confirm, long at, long interval) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
        this.confirm = confirm;
        this.at = at;
        this.interval = interval;
    }

    @Override
    public Observable<State> getObservable(Context context, String _type, String _id) {
        long curTime = DateTime.getDateTime();
        return Observable.from(new Long[]{at})
                .map(delay -> {
                    if (delay <= 0) delay = 1L;
                    return delay;
                })
                .flatMap(new Func1<Long, Observable<? extends Long>>() {
                    @Override
                    public Observable<? extends Long> call(Long delay) {
                        return Observable.just(0L).delay(delay, TimeUnit.MILLISECONDS);
                    }
                })
                .flatMap(new Func1<Long, Observable<State>>() {
                    @Override
                    public Observable<State> call(Long aLong) {
                        Log.d("abc", "here");
                        return Observable.create(new Observable.OnSubscribe<State>() {
                            @Override
                            public void call(Subscriber<? super State> subscriber) {
                                showDialog(context, subscriber);
                            }
                        }).timeout( at + interval, TimeUnit.MILLISECONDS).onErrorReturn(new Func1<Throwable, State>() {
                            @Override
                            public State call(Throwable throwable) {
                                Log.d("abc","phonedialog ... onerrorreturn="+throwable.toString());
                                stop(context);
                                if (throwable instanceof TimeoutException)
                                    return new State(State.STATE.OUTPUT, "MISSED");
                                else return null;
                            }
                        });

                    }
                }).doOnUnsubscribe(() -> {
                    stop(context);
                });
    }

    private void stop(Context context) {
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {
            Log.e("abc", "PhoneDialog()..stop()..unregister_broadcast failed");
        }
        try {
            ActivityDialog.fa.finish();
        }catch (Exception e){}
    }

    private void showDialog(Context context, Subscriber<? super State> subscriber) {
        Log.d("abc","phone dialog...show()");
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                Log.d("abc","phone dialog onreceive()");
                String message = intent.getStringExtra(ActivityDialog.RESULT);
                subscriber.onNext(new State(State.STATE.OUTPUT, message));
                subscriber.onCompleted();
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter(ActivityDialog.INTENT_RESULT));

        Intent intent = new Intent(context, ActivityDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ActivityDialog.TITLE, title);
        intent.putExtra(ActivityDialog.CONTENT, content);
        intent.putExtra(ActivityDialog.BUTTONS, buttons);
        intent.putExtra(ActivityDialog.CONFIRM, confirm);
        context.startActivity(intent);
    }
}