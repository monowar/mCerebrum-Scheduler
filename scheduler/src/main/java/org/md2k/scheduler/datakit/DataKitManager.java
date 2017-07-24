package org.md2k.scheduler.datakit;
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

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeStringArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataSourceNotFound;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class DataKitManager {
    private DataKitAPI dataKitAPI;

    public ArrayList<Data> getSample(DataSource dataSource, int sampleNo) throws DataKitAccessError, DataSourceNotFound {
        ArrayList<Data> data = new ArrayList<>();
        try {
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(dataSourceBuilder);
            if (dataSourceClients.size() == 0) {
                throw new DataSourceNotFound();
            }else {
                for (int i = 0; i < dataSourceClients.size(); i++) {
                    ArrayList<DataType> dataTypes1 = dataKitAPI.query(dataSourceClients.get(i), sampleNo);
                    for (int j = 0; j < dataTypes1.size(); j++)
                        data.add(new Data(dataSourceClients.get(i), dataTypes1.get(j)));
                }
                return data;
            }
        } catch (DataKitException e) {
            throw new DataKitAccessError();
        } catch (DataSourceNotFound dataSourceNotFound) {
            throw new DataSourceNotFound();
        }
    }
    public ArrayList<Data> getSample(DataSource dataSource, long startTime, long endTime) throws DataKitAccessError, DataSourceNotFound {
        ArrayList<Data> data = new ArrayList<>();
        try {
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(dataSourceBuilder);
            if (dataSourceClients.size() == 0) {
                throw new DataSourceNotFound();
            }else {
                for (int i = 0; i < dataSourceClients.size(); i++) {
                    ArrayList<DataType> dataTypes1 = dataKitAPI.query(dataSourceClients.get(i), startTime, endTime);
                    for (int j = 0; j < dataTypes1.size(); j++)
                        data.add(new Data(dataSourceClients.get(i), dataTypes1.get(j)));
                }
                return data;
            }
        } catch (DataKitException e) {
            throw new DataKitAccessError();
        } catch (DataSourceNotFound dataSourceNotFound) {
            throw new DataSourceNotFound();
        }
    }

    private ArrayList<DataSourceClient> find(final DataSource dataSource) throws DataKitException {
        return dataKitAPI.find(new DataSourceBuilder(dataSource));
    }

    public Observable<Data> subscribe(DataSource dataSource) {
        return Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                try {
                    ArrayList<DataSourceClient> dataSourceClients = find(dataSource);
                    if (dataSourceClients.size() == 0) throw new DataSourceNotFound();
                    for (int i = 0; i < dataSourceClients.size(); i++) {
                        int finalI = i;
                        dataKitAPI.subscribe(dataSourceClients.get(i), dataType -> {
                            Data data =new Data(dataSourceClients.get(finalI), dataType);
                            subscriber.onNext(data);
                        });
                    }
                } catch (DataKitException e) {
                    subscriber.onError(new DataKitAccessError());
                } catch (DataSourceNotFound dataSourceNotFound) {
                    subscriber.onError(dataSourceNotFound);
                }
            }
        }).retryWhen(observable -> observable.flatMap(new Func1<Throwable, Observable<?>>() {
            @Override
            public Observable<?> call(Throwable throwable) {
                if (throwable instanceof DataSourceNotFound)
                    return Observable.timer(1000, TimeUnit.MICROSECONDS);
                else return Observable.error(throwable);
            }
        })).doOnUnsubscribe(() -> unsubscribe(dataSource))
                .doOnCompleted(() -> unsubscribe(dataSource))
                .doOnError(throwable -> unsubscribe(dataSource));
    }


    public void unsubscribe(DataSource dataSource){
        try {
            ArrayList<DataSourceClient> dataSourceClients = find(dataSource);
            for (int i = 0; i < dataSourceClients.size(); i++)
                dataKitAPI.unsubscribe(dataSourceClients.get(i));
        } catch (DataKitException ignored) {

        }
    }
    public Observable<Boolean> connect(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                try {
                    dataKitAPI = DataKitAPI.getInstance(context);
                    dataKitAPI.connect(() -> subscriber.onNext(true));
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).doOnUnsubscribe(() -> dataKitAPI.disconnect()).doOnError(throwable -> dataKitAPI.disconnect());
    }

    public DataSourceClient register(DataSource dataSource) throws DataKitAccessError {
        try {
            return dataKitAPI.register(new DataSourceBuilder(dataSource));
        } catch (DataKitException e) {
            throw new DataKitAccessError();
        }
    }
    public void unregister(DataSource dataSource){
        try {
            ArrayList<DataSourceClient> dataSourceClients = find(dataSource);
            for (int i = 0; i < dataSourceClients.size(); i++)
                dataKitAPI.unregister(dataSourceClients.get(i));
        } catch (DataKitException ignored) {

        }
    }

    public void insert(DataSourceClient dataSourceClient, DataType dataType) throws DataKitAccessError {
        try {
            dataKitAPI.insert(dataSourceClient, dataType);
        } catch (DataKitException e) {
            throw new DataKitAccessError();
        }
    }
}
