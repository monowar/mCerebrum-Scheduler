package org.md2k.scheduler.operation.application;
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.MyApplication;
import org.md2k.scheduler.State;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.operation.AbstractOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class ApplicationOperation extends AbstractOperation {
    private String packageName;
    private long timeout;
    private HashMap<String, String> parameters;
    private BroadcastReceiver mMessageReceiver;

    public ApplicationOperation(String packageName, long timeout, HashMap<String, String> parameters) {
        this.packageName = packageName;
        this.timeout = timeout;
        this.parameters = parameters;
    }

    @Override
    public Observable<State> getObservable(String path, String _type, String _id) {
        return Observable.just(0L)
                .map(delay -> {
                    if (delay <= 0) delay = 1L;
                    return delay;
                })
                .flatMap(new Func1<Long, Observable<? extends Long>>() {
                    @Override
                    public Observable<? extends Long> call(Long delay) {
                        return Observable.timer(delay, TimeUnit.MILLISECONDS);
                    }
                })
                .flatMap(new Func1<Long, Observable<State>>() {
                    @Override
                    public Observable<State> call(Long aLong) {
                        return Observable.create(new Observable.OnSubscribe<State>() {
                            @Override
                            public void call(Subscriber<? super State> subscriber) {
                                triggerApp(path, _type, _id, subscriber);
                            }
                        }).timeout(timeout, TimeUnit.MILLISECONDS).onErrorReturn(new Func1<Throwable, State>() {
                            @Override
                            public State call(Throwable throwable) {
                                DataKitManager.getInstance().insertSystemLog("DEBUG",path, " timeout");
                                stop();
                                if (throwable instanceof TimeoutException) {
                                    Intent intent=new Intent();
                                    intent.setAction("org.md2k.scheduler.request");
                                    intent.putExtra("TYPE","TIMEOUT");
                                    MyApplication.getContext().sendBroadcast(intent);
                                    return new State(State.STATE.OUTPUT, "TIMEOUT");
                                }
                                else return null;
                            }
                        });

                    }
                }).doOnUnsubscribe(() -> stop());
    }
    private void stop() {
        try {
            MyApplication.getContext().unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {
            Log.e("abc", "AppOp()..stop()..unregister_broadcast failed");
        }
    }

    private void triggerApp(String path, String _type, String _id, Subscriber<? super State> subscriber) {
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                String resType=intent.getStringExtra("TYPE");
                if("RESULT".equals(resType)) {
                    String message = intent.getStringExtra("OUTPUT");
//                String message = intent.getStringExtra(ActivityDialog.RESULT);
                    DataKitManager.getInstance().insertSystemLog("DEBUG",path,"stop application result="+message);
                    subscriber.onNext(new State(State.STATE.OUTPUT, message));
                    subscriber.onCompleted();
                }
            }
        };
        MyApplication.getContext().registerReceiver(mMessageReceiver,
                new IntentFilter("org.md2k.scheduler.response"));
        Intent intent = MyApplication.getContext().getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(parameters!=null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                intent.putExtra(key, value);
            }
        }
        if(_id!=null)
        intent.putExtra("type",_type+"_"+_id);
        else         intent.putExtra("type",_type);
        intent.putExtra("id","DATA");
        DataKitManager.getInstance().insertSystemLog("DEBUG",path,"start application");

        MyApplication.getContext().startActivity(intent);
    }
    
/*
    public Observable<State> getObservable(Context context, ApplicationData applicationData) {
        MyBroadcastReceiver myReceiver = new MyBroadcastReceiver();
        long time[]=new long[2];
        String status;
        return Observable.just(true).flatMap(new Func1<Boolean, Observable<State>>() {
            @Override
            public Observable<State> call(Boolean aBoolean) {
                return null;
            }
        }).
        return Observable.just(true).map(new Func1<Boolean, State>() {
            @Override
            public State call(Boolean aBoolean) {
                return null;
            }
        });
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                myReceiver[0] = new MyBroadcastReceiver(subscriber, logger, finalPath, time);
                context.registerReceiver(myReceiver[0], new IntentFilter("org.md2k.ema_scheduler.response"));
                String p="";
                if(parameters!=null){
                    for(int i=0;i<parameters.length;i++) {
                        if(i!=0) p+=",";
                        p+=parameters[i];
                    }
                }
                logger.write(finalPath, "start (timeout="+timeout+"), parameters=("+p+")");
                start(context, time);
            }
        }).timeout(DateTime.getTimeInMillis(timeout), TimeUnit.MILLISECONDS)
                .onErrorResumeNext(throwable -> {
                    stop(context, myReceiver[0]);
                    if (throwable instanceof TimeoutException) {
                        logger.write(finalPath, "stop (status=timeout)");
                        sendStopNotification(context);
                        if (type.trim().toUpperCase().equals("EMA")) {
                            DataType dataType = createEMAData("timeout", time, null);
                            return Observable.just(new Response(null, "timeout", dataType));
                        } else {
                            return Observable.just(new Response(null, "timeout", null));
                        }
                    } else return Observable.error(throwable);
                }).filter(response -> {
                    if (skip == null) return true;
                    for (String aSkip : skip)
                        if (aSkip.trim().toUpperCase().equals(response.getInput().trim().toUpperCase()))
                            return false;
                    return true;
                }).doOnUnsubscribe(() -> {
                    stop(context, myReceiver[0]);
                });

    }
    private void sendStopNotification(Context context) {
        Intent intent = new Intent();
        intent.setAction("org.md2k.ema.operation");
        intent.putExtra("TYPE", "TIMEOUT");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(intent);
    }

    private DataType createEMAData(String status, long[] time, JsonArray jsonArray) {
        Gson gson = new Gson();
        EMA ema = new EMA(time[0], time[1], status, jsonArray);
        JsonObject sample = new JsonParser().parse(gson.toJson(ema)).getAsJsonObject();
        return new DataTypeJSONObject(DateTime.getDateTime(), sample);
    }

    private boolean start(Context context, long[] time) {
        time[0] = DateTime.getDateTime();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(id);
        intent.setAction(id);
        intent.putExtra("parameters", parameters);
        if (type.trim().toUpperCase().equals("EMA")) {
            intent.putExtra("file_name", parameters[0]);
            intent.putExtra("id", id);
            intent.putExtra("name", parameters[0]);
            intent.putExtra("timeout", DateTime.getTimeInMillis(timeout));
        }
        context.startActivity(intent);
        return true;
    }

    private void stop(Context context, MyBroadcastReceiver myReceiver) {
        context.unregisterReceiver(myReceiver);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        Subscriber<? super Response> subscriber;
        Logger logger;
        long[] time;

        MyBroadcastReceiver(Subscriber<? super Response> subscriber, Logger logger, long[] time) {
            this.subscriber = subscriber;
            this.time=time;
            this.logger=logger;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("TYPE");
            if (type.equals("RESULT")) {
                Response response;
                String answer = intent.getStringExtra("ANSWER");
                String status = intent.getStringExtra("STATUS");
                logger.write(path, "stop (status="+status+")");

                if (ApplicationOperation.this.type.trim().toUpperCase().equals("EMA")) {
                    JsonParser parser = new JsonParser();
                    JsonElement tradeElement = parser.parse(answer);
                    JsonArray question_answer = tradeElement.getAsJsonArray();
                    DataType dataType = createEMAData(status, time, question_answer);
                    response = new Response(null, status, dataType);
                } else response = new Response(null, status, null);
                subscriber.onNext(response);
                subscriber.onCompleted();
//                    saveData(question_answer, status);
            } else if (type.equals("STATUS_MESSAGE")) {
//                  lastResponseTime = intent.getLongExtra("TIMESTAMP", -1);
//                 message = intent.getStringExtra("MESSAGE");
//                Log.d(TAG, "data received... lastResponseTime=" + lastResponseTime + " message=" + message);
            }
        }
    }

*/
}
