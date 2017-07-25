package org.md2k.scheduler.condition;
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

import org.junit.Test;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;

public class testinterval {
    private int count=0;
    @Test
    public void myTest(){
        Subscription s= getObservableFrom().subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError");
            }

            @Override
            public void onNext(Boolean aBoolean) {
                System.out.println("onNext");
            }
        });
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private Observable<Boolean> getObservable(){
        return Observable.just(true)
                .map(aBoolean -> count)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        if(integer%2==0) {
                            System.out.println(DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+" "+String.valueOf(integer)+" delay=2 before");
                            return Observable.just(integer).delay(2000, TimeUnit.MILLISECONDS);
                        }
                        else{
                            System.out.println(DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+" "+String.valueOf(integer)+" delay=3 before");
                            return Observable.just(integer).delay(3000, TimeUnit.MILLISECONDS);
                        }
                    }
                }).flatMap(new Func1<Integer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Integer integer) {
                        System.out.println(DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+" "+String.valueOf(integer)+" after");
                        return Observable.error(new ConfigurationFileFormatError());
                    }
                }).retryWhen(errors->errors.flatMap((Func1<Throwable, Observable<?>>) throwable -> {
                    count++;
                     return Observable.just(null);
                }));
    }
    private Observable<Boolean> getObservableRange(){
        return Observable.range(1, 100)
                .map(integer -> count)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        if(integer%2==0) {
                            System.out.println(DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+" "+String.valueOf(integer)+" delay=2 before");
                            return Observable.just(integer).delay(2000, TimeUnit.MILLISECONDS);
                        }
                        else{
                            System.out.println(DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+" "+String.valueOf(integer)+" delay=3 before");
                            return Observable.just(integer).delay(3000, TimeUnit.MILLISECONDS);
                        }
                    }
                }).flatMap(new Func1<Integer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Integer integer) {
                        System.out.println(DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+" "+String.valueOf(integer)+" after");
                        return Observable.error(new ConfigurationFileFormatError());
                    }
                }).retryWhen(errors->errors.flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        count++;
                        return Observable.just(null);
                    }
                }));
    }
    private Observable<Boolean> getObservableFrom(){
        Integer[] arr=new Integer[]{1,2,3,4};
        return Observable.from(arr)
                .map(integer -> count)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        if(integer%2==0) {
                            System.out.println(DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+" "+String.valueOf(integer)+" delay=2 before");
                            return Observable.just(integer).delay(2000, TimeUnit.MILLISECONDS);
                        }
                        else{
                            System.out.println(DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+" "+String.valueOf(integer)+" delay=3 before");
                            return Observable.just(integer).delay(3000, TimeUnit.MILLISECONDS);
                        }
                    }
                }).flatMap(new Func1<Integer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Integer integer) {
                        System.out.println(DateTime.convertTimeStampToDateTime(DateTime.getDateTime())+" "+String.valueOf(integer)+" after");
                        return Observable.error(new ConfigurationFileFormatError());
                    }
                }).retryWhen(errors->errors.flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        count++;
                        return Observable.just(null);
                    }
                }));
    }

}
