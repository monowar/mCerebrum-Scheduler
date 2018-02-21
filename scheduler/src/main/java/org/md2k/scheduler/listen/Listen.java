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
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

public class Listen {
    private ArrayList<DataSource> dataSources;
    private ArrayList<String> times;
    private DataKitManager dataKitManager;

    public Listen(DataKitManager dataKitManager) {
        dataSources = new ArrayList<>();times=new ArrayList<>();
        this.dataKitManager = dataKitManager;
    }
    public void add(DataSource dataSource){
        dataSources.add(dataSource);
    }
    public void add(String time){
        for(int i=0;i<times.size();i++)
            if(times.get(i).equals(time)) return;
        times.add(time);
    }
    public Observable<ListenData> getObservable(){
        if(times.size()==0 && dataSources.size()==0) return null;
        if(times.size()==0) return getObservableDataSources();
        if(dataSources.size()==0) return getObservableTimes();
        return Observable.merge(getObservableDataSources(), getObservableTimes());
    }
    private Observable<ListenData> getObservableTimes(){
        ArrayList<Observable<ListenData>> t=new ArrayList<>();
        for(int i=0;i<times.size();i++){
            t.add(getObservableTime(times.get(i)));
        }
        return Observable.merge(t);
    }
    private Observable<ListenData> getObservableDataSources(){
        ArrayList<Observable<ListenData>> d=new ArrayList<>();
        for(int i=0;i<dataSources.size();i++){
            d.add(getObservableDataSource(dataSources.get(i)));
        }
        return Observable.merge(d);
    }

    private Observable<ListenData> getObservableTime(String time){
        return Observable.just(time).flatMap(new Func1<String, Observable<ListenData>>() {
            @Override
            public Observable<ListenData> call(String s) {
                long trigTime = Time.getToday()+Time.getTime(time);
                long curTime = DateTime.getDateTime();
                if(trigTime<curTime)
                    trigTime+=24*60*60*1000L;
                Log.d("abc","trigger at: "+DateTime.convertTimeStampToDateTime(trigTime));
                return Observable.just(new ListenData(ListenData.Type.TIME, null, time)).delay(trigTime-curTime, TimeUnit.MILLISECONDS);
            }
        }).repeat();

    }

    private Observable<ListenData> getObservableDataSource(DataSource dataSource){
        return dataKitManager.subscribe(dataSource).map(new Func1<Data, ListenData>() {
            @Override
            public ListenData call(Data data) {
                return new ListenData(ListenData.Type.DATASOURCE, dataSource, null);
            }
        });
    }
}
