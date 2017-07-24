package org.md2k.scheduler.scheduler.when.rule;
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
import org.md2k.scheduler.exception.SchedulerFailedError;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.functions.Func1;

public class Rules {
    private Rule[] rules;

    public Rules(Rule[] rules) {
        this.rules = rules;
    }

    private boolean isValidRange(String path, Logger logger, Long[] blockTime, int index) {
        long curTime = DateTime.getDateTime();
        int curIndex=index;
        if (curIndex >= rules.length)
            curIndex = rules.length - 1;

        if (curTime >= blockTime[2] || (index!=curIndex && rules[curIndex].getRetry()==null)){
            logger.write(path, "invalid_block, block_start=" + DateTime.convertTimeStampToDateTime(blockTime[1]) + ",block_end=" + DateTime.convertTimeStampToDateTime(blockTime[2]));
            return false;
        }else if(index!=curIndex && rules[curIndex].getRetry()!=null && curTime+DateTime.getTimeInMillis(rules[curIndex].getRetry())>=blockTime[2]) {
            logger.write(path, "invalid_block, block_start=" + DateTime.convertTimeStampToDateTime(blockTime[1]) + ",block_end=" + DateTime.convertTimeStampToDateTime(blockTime[2]));
            return false;
        }else{
            return true;
        }
    }

    private long getTriggerTime(String path, Logger logger, int index, Long[] blockTime) {
        int curIndex = index;
        if (curIndex >= rules.length)
            curIndex = rules.length-1;
        if (rules[curIndex].isRandom())
            return getRandom(path,logger, rules[curIndex], blockTime[2]);
        else if (index != 0 || index!=curIndex) {
            logger.write(path + "/immediate", "trigger_at=" + DateTime.convertTimeStampToDateTime(DateTime.getDateTime() + DateTime.getTimeInMillis(rules[curIndex].getRetry())));
            return DateTime.getTimeInMillis(rules[curIndex].getRetry());
        }
        else {
            logger.write(path + "/immediate", "trigger_now=" + DateTime.convertTimeStampToDateTime(DateTime.getDateTime()));
            return 0;
        }
    }

    public Observable<Long[]> getObservable(String path, Logger logger, AtomicBoolean isRunning, DataKitManager dataKitManager, Conditions conditions, Long[] blockTime) {
        path+="/rule";

        String finalPath = path;
        return Observable.just(true)
                .map(aBoolean -> logger.getInt(finalPath) + 1)
                .flatMap(new Func1<Integer, Observable<Rule>>() {
                    @Override
                    public Observable<Rule> call(Integer integer) {
                        int index = integer;
                        int curIndex=index;if(curIndex>=rules.length) curIndex=rules.length-1;
                        long triggerTime;
                        if (!isValidRange(finalPath, logger, blockTime, index))
                            return Observable.just(null);
                        triggerTime = getTriggerTime(finalPath+"/"+String.valueOf(integer), logger, index, blockTime);
                        logger.set(finalPath, logger.getInt(finalPath) + 1);
                        if (triggerTime >0) {
                            return Observable.just(Observable.just(rules[curIndex]).delay(triggerTime, TimeUnit.MILLISECONDS).toBlocking().single());
                        }
                        else
                            return Observable.just(rules[curIndex]);
                    }
                }).flatMap(new Func1<Rule, Observable<Long[]>>() {
                    @Override
                    public Observable<Long[]> call(Rule rule) {
                        try {
                            if(rule==null) return Observable.just(null);
                            if (conditions.isValid(finalPath, logger, dataKitManager, rule.getCondition()) && !isRunning.get()) {
//                                isRunning.set(true);
                                return Observable.just(blockTime);
                            } else return Observable.error(new SchedulerFailedError());
                        } catch (ConfigurationFileFormatError | DataKitAccessError e) {
                            return Observable.error(e);
                        }
                    }
                }).retryWhen(errors -> errors.flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        if (throwable instanceof SchedulerFailedError) {
                            return Observable.just(null);
                        }
                        else {
                            return Observable.error(throwable);
                        }
                    }
                }));
    }

    private long getRandom(String path, Logger logger, Rule rule, long blockEndTime) {
        int divide_by = rule.getDivide_by();
        if (divide_by <= 0) divide_by = 1;
        long currentTime = DateTime.getDateTime();
        long currentTimeSec = currentTime / DateTime.SECOND_IN_MILLIS;
        long endTimeSec = blockEndTime / DateTime.SECOND_IN_MILLIS;

        if (currentTimeSec > endTimeSec) return -1;
        long range = (endTimeSec - currentTimeSec) / divide_by;
        if(range==0) return 0;
        long randomValue = new Random().nextInt((int) range);
        logger.write(path + "/random", "trigger_at=" + DateTime.convertTimeStampToDateTime(currentTime + randomValue * 1000));
        return randomValue*1000;
    }

}
