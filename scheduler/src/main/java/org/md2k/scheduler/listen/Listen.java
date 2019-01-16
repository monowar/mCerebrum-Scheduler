package org.md2k.scheduler.listen;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following _condition are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of _condition and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of _condition and the following disclaimer in the documentation
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

import android.util.Log;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.time.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Listen {
    private static final long DAYS_IN_MILLIS = 24L * 60L * 60L * 1000L;
    private static final long DAYS_IN_SECOND = 24L * 60L * 60L;
    private ArrayList<DataSource> dataSources;
    private ArrayList<String> times;
    private HashMap<String, Long> lastTriggerDay;
//    PendingIntent pendingIntent;

    public Listen() {
        dataSources = new ArrayList<>();
        times = new ArrayList<>();
        lastTriggerDay  = new HashMap<>();
    }

    public void add(DataSource dataSource) {
        for (int i = 0; i < dataSources.size(); i++)
            if (isEqual(dataSources.get(i), dataSource)) return;
        dataSources.add(dataSource);
    }

    private boolean isEqual(DataSource d1, DataSource d2) {
        if (!d1.getType().equals(d2.getType())) return false;
        if (d1.getId() != null && d2.getId() == null) return false;
        if (d1.getId() == null && d2.getId() != null) return false;
        if (d1.getId() != null && !d1.getId().equals(d2.getId())) return false;
        return true;
    }

    public void add(String time) {
        for (int i = 0; i < times.size(); i++)
            if (times.get(i).equals(time)) return;
        times.add(time);
    }

    public Observable<ListenData> getObservable(String path) {
        return Observable.just(true).flatMap(new Func1<Boolean, Observable<ListenData>>() {
            @Override
            public Observable<ListenData> call(Boolean aBoolean) {
                if (times.size() == 0 && dataSources.size() == 0) return null;
                if (times.size() == 0) return getObservableDataSources(path);
                if (dataSources.size() == 0) return getObservableTimes(path);
                return Observable.merge(getObservableDataSources(path), getObservableTimes(path));
            }
        });
    }

    private Observable<ListenData> getObservableTimes(String path) {
        return Observable.just(true).flatMap(new Func1<Boolean, Observable<ListenData>>() {
            @Override
            public Observable<ListenData> call(Boolean aBoolean) {
                ArrayList<Observable<ListenData>> t = new ArrayList<>();
                for (int i = 0; i < times.size(); i++) {
                    t.add(getObservableTime(times.get(i)));
                }
                return Observable.merge(t);
            }
        });
    }

    private Observable<ListenData> getObservableDataSources(String path) {
        return Observable.just(true).flatMap(new Func1<Boolean, Observable<ListenData>>() {
            @Override
            public Observable<ListenData> call(Boolean aBoolean) {
                ArrayList<Observable<ListenData>> d = new ArrayList<>();
                for (int i = 0; i < dataSources.size(); i++) {
                    d.add(getObservableDataSource(path, dataSources.get(i)));
                }
                return Observable.merge(d);
            }
        });
    }

/*
    private void setAlarm(long timestamp) {
        AlarmManager manager = (AlarmManager) MyApplication.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(MyApplication.getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 0, alarmIntent, 0);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, timestamp,
                DAYS_IN_MILLIS, pendingIntent);
    }

    public void cancelAlarm() {
        AlarmManager manager = (AlarmManager) MyApplication.getContext().getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }
*/

    private Observable<ListenData> getObservableTime(String time) {
        return Observable.interval(15, TimeUnit.SECONDS, Schedulers.io()).onBackpressureLatest()
                .observeOn(Schedulers.computation()).map(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                long today = Time.getToday();
                long trigTime = Time.getToday() + Time.getTime(time);
                long curTime = DateTime.getDateTime();

                if(trigTime>curTime) {
                    Log.d("abc","try future = "+time);
                    return false;
                }
                if(!lastTriggerDay.containsKey(time) || lastTriggerDay.get(time)!=today){
                    Log.d("abc","try valid= "+time);
                    lastTriggerDay.put(time, today);
                    return true;
                }else return false;
            }
        }).filter(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                return aBoolean;
            }
        }).map(new Func1<Boolean, ListenData>() {
            @Override
            public ListenData call(Boolean aBoolean) {
                return new ListenData(ListenData.Type.TIME, null, time);
            }
        });
/*
        return Observable.just(true).map(new Func1<Boolean, Long>() {
            @Override
            public Long call(Boolean aBoolean) {
                long trigTime = Time.getToday() + Time.getTime(time);
                long curTime = DateTime.getDateTime();
                while (trigTime < curTime)
                    trigTime += DAYS_IN_MILLIS;
                Logger.d("next trigger time = " + DateTime.convertTimeStampToDateTime(trigTime));
//                setAlarm(trigTime);
*/
/*
                long sec = (trigTime - curTime) / 1000;
                if ((trigTime - curTime) % 1000 != 0)
                    sec++;
                return sec;
*//*

                return trigTime-DateTime.getDateTime();
            }
        })*/
/*.flatMap(new Func1<Long, Observable<Long>>() {
            @Override
            public Observable<Long> call(Long aLong) {
                Logger.d("next trigger...initial delay = " + String.valueOf(aLong/ (60*1000)) + " Minute");
                return Observable.timer(aLong, TimeUnit.MILLISECONDS, Schedulers.io());
            }
        })*//*
*/
/*.filter(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                return false;
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                cancelAlarm();
            }
        })*//*
.flatMap(new Func1<Long, Observable<Long>>() {
            @Override
            public Observable<Long> call(Long aLong) {
                Logger.d("next trigger...initial delay = " + String.valueOf(aLong / (60*1000)) + " Minute");
                return  Observable.interval(aLong/(1000*60), DAYS_IN_SECOND/60, TimeUnit.MINUTES, Schedulers.io()).observeOn(Schedulers.computation());
//                return Observable.interval(aLong, DAYS_IN_MILLIS, TimeUnit.MILLISECONDS, Schedulers.newThread()).observeOn(Schedulers.newThread());
            }
        }).map(new Func1<Long, ListenData>() {
            @Override
            public ListenData call(Long aLong) {
                Logger.d("trigger now=" + time);
                return new ListenData(ListenData.Type.TIME, null, time);
            }
        });
*/

/*
        return Observable.just(time).flatMap(new Func1<String, Observable<ListenData>>() {
            @Override
            public Observable<ListenData> call(String s) {
                long trigTime = Time.getToday()+Time.getTime(time);
                long curTime = DateTime.getDateTime();
                while(trigTime<curTime)
                    trigTime+=DAYS_IN_MILLIS;
                Log.d("abc","trigger at: "+DateTime.convertTimeStampToDateTime(trigTime));
                return Observable.just(new ListenData(ListenData.Type.TIME, null, time)).delay(trigTime-curTime, TimeUnit.MILLISECONDS);
            }
        }).repeat();

*/
    }

    private Observable<ListenData> getObservableDataSource(String path, DataSource dataSource) {
        return DataKitManager.getInstance().subscribe(dataSource).map(new Func1<Data, ListenData>() {
            @Override
            public ListenData call(Data data) {
                ListenData d = new ListenData(ListenData.Type.DATASOURCE, dataSource, null);
                DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/Listen","event received: "+d.toString());
                return d;
            }
        });
    }
}
