/*
package org.md2k.scheduler.scheduler.listen;

import org.junit.Test;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataSourceNotFound;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

*/
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
 *//*

public class ListenTest {


    @Test
    public void empty(){
        DataSource[] d=null;
        Listen listen=new Listen(d);
        DataKitManager dataKitManager= CDKM(null,null,-1,null,null,-1);
        Subscription s=listen.getObservable(dataKitManager).subscribe(new Observer<Data>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted()...");
                assertTrue(true);
            }

            @Override
            public void onError(Throwable e) {
                assertTrue(false);

            }

            @Override
            public void onNext(Data data) {
                System.out.println("onNext()...");
                assertTrue(true);
            }
        });
    }

    @Test
    public void SS_QM5() {
        ArrayList<String> results = new ArrayList<>();
        Listen listen = new Listen(createDataSource());
        DataTypeLong s_ss = null;
        DataTypeLong q_ss = new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime() - 2000);
        DataTypeLong s_se = null;
        DataTypeLong q_se = null;
        DataKitManager dataKitManager = CDKM(s_ss, q_ss, -1, s_se, q_se, -1);
        Subscription s = listen.getObservable(dataKitManager).subscribe(new Observer<Data>() {
            @Override
            public void onCompleted() {
                results.add("C");
                System.out.println("onCompleted()...");
                assertTrue(true);
            }

            @Override
            public void onError(Throwable e) {
                results.add("E");
                assertTrue(false);

            }

            @Override
            public void onNext(Data data) {
                results.add("N");
                if (data != null)
                    System.out.println("onNext()...");
                assertTrue(true);
            }
        });
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertArrayEquals(results.toArray(), new String[]{"N"});

    }

    @Test
    public void SS_QP5() {
        ArrayList<String> results = new ArrayList<>();
        DataSource[] d = createDataSource();
        Listen listen = new Listen(d);
        DataTypeLong s_ss = null;
        DataTypeLong q_ss = new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime() + 2000);
        DataTypeLong s_se = null;
        DataTypeLong q_se = null;
        DataKitManager dataKitManager = CDKM(s_ss, q_ss, -1, s_se, q_se, -1);
        Subscription s = listen.getObservable(dataKitManager).subscribe(new Observer<Data>() {
            @Override
            public void onCompleted() {
                results.add("C");
                System.out.println("onCompleted()...");
                assertTrue(true);
            }

            @Override
            public void onError(Throwable e) {
                results.add("E");
                assertTrue(false);

            }

            @Override
            public void onNext(Data data) {
                results.add("N");
                System.out.println("onNext()...time=" + DateTime.getDateTime() + " data=" + data);
                assertTrue(true);
            }
        });
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertArrayEquals(results.toArray(), new String[]{"N", "N"});
    }
    @Test
    public void SS_SNOW(){
        ArrayList<String> results=new ArrayList<>();
        DataSource[] d=createDataSource();
        Listen listen=new Listen(d);
        DataTypeLong s_ss=new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime()-2000);
        DataTypeLong q_ss=null;//new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime()-2000);
        DataTypeLong s_se=null;
        DataTypeLong q_se=null;
        DataKitManager dataKitManager= CDKM(s_ss,q_ss,0,s_se,q_se,-1);
        Subscription s=listen.getObservable(dataKitManager).subscribe(new Observer<Data>() {
            @Override
            public void onCompleted() {
                results.add("C");
                System.out.println("onCompleted()...");
                assertTrue(false);
            }

            @Override
            public void onError(Throwable e) {
                results.add("E");
                assertTrue(false);

            }

            @Override
            public void onNext(Data data) {
                results.add("N");
                System.out.println("onNext()...time="+DateTime.getDateTime()+" data="+data);
                assertTrue(true);
            }
        });
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertArrayEquals( results.toArray(), new String[]{"N","N"} );

    }
    @Test
    public void SS_SNOW_QP2(){
        ArrayList<String> results=new ArrayList<>();
        DataSource[] d=createDataSource();
        Listen listen=new Listen(d);
        DataTypeLong s_ss=new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime());
        DataTypeLong q_ss=new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime()+1000);
        DataTypeLong s_se=null;
        DataTypeLong q_se=null;
        DataKitManager dataKitManager= CDKM(s_ss,q_ss,4000,s_se,q_se,-1);
        Subscription s=listen.getObservable(dataKitManager).subscribe(new Observer<Data>() {
            @Override
            public void onCompleted() {
                results.add("C");
                System.out.println("onCompleted()...");
                assertTrue(false);
            }

            @Override
            public void onError(Throwable e) {
                results.add("E");
                assertTrue(false);

            }

            @Override
            public void onNext(Data data) {
                results.add("N");
                System.out.println("onNext()...time="+DateTime.getDateTime()+" data="+data);
                assertTrue(true);
            }
        });
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertArrayEquals( results.toArray(), new String[]{"N","N", "N"} );

    }

    private DataKitManager CDKM(DataTypeLong s_ss, DataTypeLong q_ss, long d_ss, DataTypeLong s_se, DataTypeLong q_se, long d_se) {
        try {
            DataKitManager dataKitManager = mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("STUDY_START") && q_ss!=null) {
                        dataTypes.add(new Data(null, q_ss));
                    }
                    if(((DataSource) args[0]).getType().equals("STUDY_END") && q_se!=null) {
                        dataTypes.add(new Data(null, q_se));
                    }
                    return dataTypes;
                }
            });
            when(dataKitManager.subscribe(any(DataSource.class))).thenAnswer(new Answer<Observable<Data>>() {
                @Override
                public Observable<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    if (((DataSource) args[0]).getType().equals("STUDY_START")) {
                        return Observable.create(new Observable.OnSubscribe<Data>() {
                            @Override
                            public void call(Subscriber<? super Data> subscriber) {
                                try {
                                    if (d_ss >=0) {
                                        Thread.sleep(d_ss);
                                        subscriber.onNext(new Data(null, s_ss));
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).subscribeOn(Schedulers.computation()).observeOn(Schedulers.io());
                    }else if (((DataSource) args[0]).getType().equals("STUDY_END")) {
                        return Observable.create(new Observable.OnSubscribe<Data>() {
                            @Override
                            public void call(Subscriber<? super Data> subscriber) {
                                try {
                                    if (d_se >=0) {
                                        Thread.sleep(d_se);
                                        subscriber.onNext(new Data(null, s_se));
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
*/
/*
                    Object[] args = invocation.getArguments();
                    if (((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), 10)));
                    } else {
//                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd));
                    }
*//*

                    return Observable.just(new Data(null,new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime()+10000)));
                }
            });
            return dataKitManager;
        } catch (DataKitAccessError dataKitAccessError) {
            dataKitAccessError.printStackTrace();
        } catch (DataSourceNotFound dataSourceNotFound) {
            dataSourceNotFound.printStackTrace();
        }
        return null;
    }
    private DataSource[] createDataSource(){
        return new DataSource[]{new DataSourceBuilder().setType("STUDY_START").build(),new DataSourceBuilder().setType("STUDY_END").build()};
    }
}*/
