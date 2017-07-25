package org.md2k.scheduler.scheduler.when;
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

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.Logger;
import org.md2k.scheduler.condition.Conditions;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.scheduler.when.rule.Rule;
import org.md2k.scheduler.scheduler.when.rule.Rules;
import org.md2k.scheduler.search.SearchBySample;
import org.md2k.scheduler.search.SearchByValue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class When {
    private SearchByValue search_by_value;
    private SearchBySample search_by_sample;
    private String[] blocks;
    private String repeat;
    private String condition;
    private Rule[] rules;
    private long startTime;

    public When(SearchByValue search_by_value, SearchBySample search_by_sample, String[] blocks, String repeat, String condition, Rule[] rules) {
        this.search_by_value = search_by_value;
        this.search_by_sample = search_by_sample;
        this.blocks = blocks;
        this.repeat = repeat;
        this.condition = condition;
        this.rules = rules;
    }

    public Observable<Long[]> getObservable(String path, Logger logger, AtomicBoolean isRunning, DataKitManager dataKitManager, Conditions conditions) {
        startTime = getStartTime(dataKitManager);
        path+="/when";
        String finalPath = path;
        Observable<Long[]> observable = Observable.just(true)
                .flatMap(new Func1<Boolean, Observable<Long[]>>() {
                    @Override
                    public Observable<Long[]> call(Boolean aBoolean) {
                        return getObservableBlock(finalPath, logger, startTime);
                    }
                })
                .flatMap(new Func1<Long[], Observable<Long[]>>() {
                    @Override
                    public Observable<Long[]> call(Long[] blockTime) {
                        try {
                            if (conditions.isValid(finalPath, logger, dataKitManager, condition)) {
                                return Observable.just(blockTime);
                            } else {
                                return Observable.just(null);
                            }
                        } catch (ConfigurationFileFormatError | DataKitAccessError e) {
                            return Observable.error(e);
                        }
                    }
                })
                .filter(longs -> longs != null)
                .flatMap(new Func1<Long[], Observable<Long[]>>() {
                    @Override
                    public Observable<Long[]> call(Long[] blockTime) {
                        String blockStart = DateTime.convertTimeStampToDateTime(blockTime[0]);
                        return new Rules(rules).getObservable(finalPath+"/"+blockStart, logger, isRunning, dataKitManager, conditions, blockTime);
                    }
                }).onErrorReturn(throwable -> null).filter(longs -> longs != null);
        if (repeat != null && startTime != -1) {
//                while (repeatTime <= curTime)
//                    repeatTime += repeatWhen;
//                long finalRepeatTime = repeatTime;
                observable = observable.repeatWhen(observable1 -> {
//                        System.out.println(finalPath+": delay="+(finalRepeatTime-curTime)/1000);
                    return observable1
                            .flatMap((Func1<Void, Observable<?>>) new Func1<Void, Observable<?>>() {
                                @Override
                                public Observable<?> call(Void aVoid) {
                                    long repeatWhen=DateTime.getTimeInMillis(repeat);
                                    long curTime=DateTime.getDateTime();
                                    startTime+=repeatWhen;
                                    if(startTime<=curTime)  return Observable.just(null);
                                    else{
                                        logger.write(finalPath, "trigger again at=" + DateTime.convertTimeStampToDateTime(startTime));
                                        return Observable.just((Long[]) null).delay(startTime-curTime, TimeUnit.MILLISECONDS);
                                    }
                                }
                            });
                });
        }
        return observable;
    }

    private long getStartTime(DataKitManager dataKitManager) {
        long startTime = -1;
        Object[] objects;
        try {
            if (search_by_value != null) {
                objects = search_by_value.execute(dataKitManager);
                if (objects != null && objects.length != 0) {
                    startTime = DateTime.getTodayAtInMilliSecond("00:00:00") -DateTime.DAY_IN_MILLIS+ (Long) objects[0];
                }
            } else if (search_by_sample != null) {
                objects = search_by_sample.execute(dataKitManager);
                if (objects != null && objects.length != 0)
                    startTime = (Long) objects[0];
            }
        } catch (Exception e) {
            startTime = -1;
        }
        return startTime;
    }

    private Observable<Long[]> getObservableBlock(String path, Logger logger, long startTime) {
        path+= "/block";
        if (blocks == null) {
            blocks = new String[]{"00:00:00 - 00:01:00"};
        }

        String finalPath = path;
        return Observable.from(blocks)
                .map(str -> {
                    String[] parts = str.split("-");
                    Long[] blockTime = new Long[3];
                    long offsetStart = DateTime.getTimeInMillis(parts[0].trim());
                    long offsetEnd = DateTime.getTimeInMillis(parts[1].trim());
                    long currentTime = DateTime.getDateTime();
                    if (startTime + offsetEnd < currentTime) return null;
                    else {
                        blockTime[0] = startTime;
                        blockTime[1] = startTime + offsetStart;
                        blockTime[2] = startTime + offsetEnd;
                        return blockTime;
                    }
                })
                .filter(s -> s != null)
                .flatMap(blockTime -> {
                    long currentTime = DateTime.getDateTime();
                    if (blockTime[1] > currentTime) {

                        logger.write(finalPath, "block_start=" + DateTime.convertTimeStampToDateTime(blockTime[1])+", block_end=" + DateTime.convertTimeStampToDateTime(blockTime[2])+", wait="+(blockTime[1]-currentTime)/(1000)+ " second");
                        return Observable.just(blockTime).delay(blockTime[1] - currentTime, TimeUnit.MILLISECONDS);
                    } else {
                        return Observable.just(blockTime);
                    }
                })
                .flatMap(new Func1<Long[], Observable<Long[]>>() {
                    @Override
                    public Observable<Long[]> call(Long[] blockTime) {
                        return Observable.just(blockTime);
                    }
                })
                .doOnNext(longs -> {
                    String blockStart = DateTime.convertTimeStampToDateTime(longs[1]);
                    String blockEnd = DateTime.convertTimeStampToDateTime(longs[2]);
                    logger.write(finalPath, "block_start=" + blockStart + ", block_end=" + blockEnd+", now");
                });
    }
}
