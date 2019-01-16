package org.md2k.scheduler.operation.notification;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import org.md2k.scheduler.MyApplication;
import org.md2k.scheduler.State;
import org.md2k.scheduler.operation.AbstractOperation;

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
public class PhoneScreen extends AbstractOperation {
    private long repeat;
    private long interval;
    private Long[] at;
    private PowerManager.WakeLock wl = null;
    private String base;
    public PhoneScreen(long repeat, long interval, Long[] at, String base) {
        this.repeat = repeat;
        this.interval = interval;
        this.at = at;
        this.base = base;
    }

    @Override
    public Observable<State> getObservable(String path, String _type, String _id) {
        Log.d("abc", "phoneScreen Observable...interval=" + interval);
        return Observable.from(at)
                .map(delay -> {
                    if (delay <= 0) delay = 0L;
                    return delay;
                }).flatMap(delay -> Observable.interval(delay, interval, TimeUnit.MILLISECONDS)
                        .takeWhile(aLong -> aLong < repeat)).map(new Func1<Long, State>() {
                    @Override
                    public State call(Long aLong) {
                        if (aLong % 2 == 0) screenOn();
                        else screenOff();
                        return new State(State.STATE.PROCESS, "Phone screen...");
                    }
                }).doOnUnsubscribe(this::screenOff).doOnError(throwable -> screenOff());
    }

    private void screenOn() {
        try {
            PowerManager pm = (PowerManager) MyApplication.getContext().getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Scheduler:PhoneScreen");
            wl.acquire();
        }catch (Exception e){}
    }

    private void screenOff() {
        try {
            if (wl != null)
                wl.release();
            wl = null;
        }catch (Exception e){}
    }
}