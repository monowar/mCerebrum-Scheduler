package org.md2k.scheduler.task.notification.notify;
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

import org.md2k.scheduler.Logger;
import org.md2k.scheduler.condition.Conditions;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.task.Response;
import org.md2k.scheduler.task.notification.Notification;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Notifications {
    private static final String PHONE_MESSAGE="PHONE_MESSAGE";
    private static final String PHONE_SCREEN="PHONE_SCREEN";
    private static final String PHONE_TONE="PHONE_TONE";
    private static final String PHONE_VIBRATE="PHONE_VIBRATION";
    private Subscription subscription;
    public Observable<Response> getObservable(Context context, String path, Logger logger, DataKitManager dataKitManager, Conditions conditions, Notification[] notifications) {
        ArrayList<Observable<Response>> observables=new ArrayList<>();
        if(notifications==null) return null;
        for (Notification notification : notifications) {
            switch (notification.getType().trim().toUpperCase()) {
                case PHONE_MESSAGE:
                    observables.add(new PhoneMessage().getObservable(context, path+"/phone_message", logger, notification)
                            .map(s -> new Response(null, s, null)));
                    break;
                case PHONE_SCREEN:
//                    observables.add(new PhoneScreen().getObservable(context, notification).subscribeOn(Schedulers.io()));
                    break;
                case PHONE_TONE:
                    observables.add(new PhoneTone().getObservable(context, path+"/phone_tone", logger,notification).map(new Func1<String, Response>() {
                        @Override
                        public Response call(String s) {
                            return new Response(null, s, null);
                        }
                    }));
                    break;
                case PHONE_VIBRATE:
                    observables.add(new PhoneVibrate().getObservable(context, path+"/phone_vibrate", logger,notification).map(new Func1<String, Response>() {
                        @Override
                        public Response call(String s) {
                            return new Response(null, s, null);
                        }
                    }));
                    break;
            }
        }
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                subscription=Observable.merge(observables).subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(Response response) {
                        subscriber.onNext(response);
                        subscriber.onCompleted();
                    }
                });
            }
        }).doOnNext(new Action1<Response>() {
            @Override
            public void call(Response response) {
                if(!subscription.isUnsubscribed())
                    subscription.unsubscribe();
            }
        })
                .doOnCompleted(new Action0() {
            @Override
            public void call() {
                if(!subscription.isUnsubscribed())
                    subscription.unsubscribe();

            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if(!subscription.isUnsubscribed())
                    subscription.unsubscribe();

            }
        });
    }
}
