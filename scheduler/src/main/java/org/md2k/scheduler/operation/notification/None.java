package org.md2k.scheduler.operation.notification;

import org.md2k.scheduler.State;
import org.md2k.scheduler.operation.AbstractOperation;

import java.util.concurrent.TimeUnit;

import rx.Observable;
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
public class None extends AbstractOperation {
    private long at;
    private long interval;
    String base;

    public None(long interval, long at, String base) {
        this.at = at;
        this.interval = interval;
        this.base = base;
    }

    @Override
    public Observable<State> getObservable(String path, String _type, String _id) {
        return Observable.from(new Long[]{at})
                .map(delay -> {
                    if (delay <= 0) delay = 1L;
                    return delay;
                })
                .flatMap(new Func1<Long, Observable<? extends Long>>() {
                    @Override
                    public Observable<? extends Long> call(Long delay) {
                        return Observable.timer(delay, TimeUnit.MILLISECONDS);
//                        return Observable.just(0L).delay(delay, TimeUnit.MILLISECONDS);
                    }
                })
                .flatMap(new Func1<Long, Observable<State>>() {
                    @Override
                    public Observable<State> call(Long aLong) {
                        return Observable.timer(at + interval, TimeUnit.MILLISECONDS).map(new Func1<Long, State>() {
                            @Override
                            public State call(Long aLong) {
                                return new State(State.STATE.OUTPUT, "MISSED");
                            }
                        });
//                        return Observable.just(new State(State.STATE.OUTPUT, "MISSED"))
//                                .delay(at + interval, TimeUnit.MILLISECONDS);
                    }
                });
    }
}