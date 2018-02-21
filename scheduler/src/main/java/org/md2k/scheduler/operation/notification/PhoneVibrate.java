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
import org.md2k.scheduler.State;
import org.md2k.scheduler.operation.AbstractOperation;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

public class PhoneVibrate extends AbstractOperation {
    private long repeat;
    private long interval;
    private Long[] at;

    public PhoneVibrate(long repeat, long interval, Long[] at) {
        this.repeat = repeat;
        this.interval = interval;
        this.at = at;
    }

    public Observable<State> getObservable(Context context, String _type, String _id) {
        return Observable.from(at)
                .map(delay -> {
                    if (delay <= 0) delay = 1L;
                    return delay;
                }).flatMap(delay -> Observable.interval(delay, interval, TimeUnit.MILLISECONDS)
                        .takeWhile(aLong -> aLong < repeat)).map(integer -> {
                    vibrate(context);
                    return new State(State.STATE.PROCESS, "Phone vibrate...");
                }).filter(new Func1<State, Boolean>() {
                    @Override
                    public Boolean call(State state) {
                        return false;
                    }
                });
    }

    private void vibrate(Context context) {
        Vibrator vibrator;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.vibrate(300);
    }
}
