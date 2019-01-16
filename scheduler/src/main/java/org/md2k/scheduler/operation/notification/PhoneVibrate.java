package org.md2k.scheduler.operation.notification;
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
import android.os.Vibrator;


import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.MyApplication;
import org.md2k.scheduler.State;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.logger.MyLogger;
import org.md2k.scheduler.operation.AbstractOperation;
import org.md2k.scheduler.time.Time;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PhoneVibrate extends AbstractOperation {
    private long repeat;
    private long interval;
    private Long[] at;
    private String base;

    public PhoneVibrate(long repeat, long interval, Long[] at, String base) {
        this.repeat = repeat;
        this.interval = interval;
        this.at = at;
        this.base = base;
    }

    public Observable<State> getObservable(String path, String _type, String _id) {
        return Observable.from(at)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long delay) {
                        if(base!=null){
                            long dl;
                            long trigTime = delay+ Time.getToday()+Time.getTime(base);
                            long curTime = DateTime.getDateTime();
                            if(trigTime>curTime) dl= trigTime-curTime;
                            else if(trigTime+5000>curTime) dl=0;
                            else dl= -1L;
                            return dl;

                        }else {
                            if (delay <= 0) delay = 0L;
                            DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/vibrate", "at: "+DateTime.convertTimeStampToDateTime(DateTime.getDateTime()+delay));
                            return delay;
                        }
                    }
                }).filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long value) {
                        if(value<0) return false;
                        else return true;
                    }
                })
                .flatMap(delay -> {
                    return Observable.interval(delay, interval, TimeUnit.MILLISECONDS)
                            .takeWhile(aLong -> {
                                return aLong < repeat;
                            });
                }).map(integer -> {
                    DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/vibrate", "vibrating...");
                    vibrate();
                    return new State(State.STATE.PROCESS, "Phone vibrate...");
                }).filter(new Func1<State, Boolean>() {
                    @Override
                    public Boolean call(State state) {
                        return false;
                    }
                });
    }

    private void vibrate() {
        Vibrator vibrator;
        vibrator = (Vibrator) MyApplication.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.vibrate(300);
    }
}
