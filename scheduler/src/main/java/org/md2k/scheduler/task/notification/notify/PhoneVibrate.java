package org.md2k.scheduler.task.notification.notify;

import android.content.Context;
import android.os.Vibrator;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.Logger;
import org.md2k.scheduler.task.notification.Notification;

import java.util.concurrent.TimeUnit;

import rx.Observable;
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
class PhoneVibrate{
    private static final String TAG = PhoneVibrate.class.getSimpleName();

    Observable<String> getObservable(Context context, String path, Logger logger, Notification notification){
        long startTime=DateTime.getDateTime();
        long interval= DateTime.getTimeInMillis(notification.getInterval());
        int repeat= notification.getRepeat();
        return Observable.from(notification.getWhen())
                .map(new Func1<String, Long>() {
                    @Override
                    public Long call(String s) {
                        long delayOffset = DateTime.getTimeInMillis(s);
                        return (startTime + delayOffset) - DateTime.getDateTime();
                    }
                }).flatMap(new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long delay) {
                        if(delay<=0L) delay=1L;
                        return Observable.interval(delay,interval, TimeUnit.MILLISECONDS).takeWhile(new Func1<Long, Boolean>() {
                            @Override
                            public Boolean call(Long aLong) {
                                if(aLong<repeat) return true;
                                else return false;
                            }
                        });
                    }
                }).map(new Func1<Long, String>() {
                    @Override
                    public String call(Long integer) {
                        logger.write(path, "vibrating...");
                        PhoneVibrate.this.vibrate(context);
                        return "";
                    }
                }).onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        return null;
                    }
                }).filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return false;
                    }
                });

    }

    private void vibrate(Context context) {
        Vibrator vibrator;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }
}
