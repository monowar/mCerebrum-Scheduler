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

import android.content.Intent;
import android.util.Log;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.datatype.DataTypeStringArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.MyApplication;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataSourceNotFound;
import org.md2k.scheduler.logger.MyLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class DataKitManager {
    private static DataKitManager instance;
    private HashMap<String, DataSourceClient> dataSourceClients;

    public static DataKitManager getInstance() {
        if (instance == null) instance = new DataKitManager();
        return instance;
    }

    private DataKitManager() {
        dataSourceClients = new HashMap<>();
    }

/*
    public ArrayList<Data> getSample(DataSource dataSource, int sampleNo) throws DataKitAccessError, DataSourceNotFound {
        ArrayList<Data> data = new ArrayList<>();
        try {
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(dataSourceBuilder);
            if (dataSourceClients.size() == 0) {
                throw new DataSourceNotFound();
            } else {
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
            } else {
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
*/

    public ArrayList<DataSourceClient> find(final DataSource dataSource) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            return dataKitAPI.find(new DataSourceBuilder(dataSource));
        } catch (DataKitException e) {
            MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitFind", "datasource=[" + dataSource.getType() + " " + dataSource.getId() + "] DataKitException e=" + e.getMessage());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
        return new ArrayList<>();
    }

    public Observable<Data> subscribe(DataSource dataSource) {
        Log.d("aaa", "subscribe datasource=" + dataSource.getType() + " " + dataSource.getId());
        return Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                try {
                    DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
                    ArrayList<DataSourceClient> dataSourceClients = find(dataSource);
                    if (dataSourceClients.size() == 0) throw new DataSourceNotFound();
                    for (int i = 0; i < dataSourceClients.size(); i++) {
                        int finalI = i;
                        dataKitAPI.subscribe(dataSourceClients.get(i), dataType -> {
                            Data data = new Data(dataSourceClients.get(finalI), dataType);
                            subscriber.onNext(data);
                        });
                        ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(i), 1);
                        if (dataTypes.size() != 0 && DateTime.getDateTime() - dataTypes.get(0).getDateTime() < 5000) {
                            Data data = new Data(dataSourceClients.get(i), dataTypes.get(0));
                            subscriber.onNext(data);
                        }

                        Log.d("abc", "subscribed=" + dataSourceClients.get(i).getDataSource().getType());
                    }
                } catch (DataKitException e) {
                    MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitSubscribe", "datasource=[" + dataSource.getType() + " " + dataSource.getId() + "] DataKitException e=" + e.getMessage());
                    subscriber.onError(new DataKitAccessError());
                } catch (DataSourceNotFound dataSourceNotFound) {
                    subscriber.onError(dataSourceNotFound);
                }
            }
        }).retryWhen(observable -> observable.flatMap(new Func1<Throwable, Observable<?>>() {
            @Override
            public Observable<?> call(Throwable throwable) {
                if (throwable instanceof DataSourceNotFound)
                    return Observable.timer(1000, TimeUnit.MILLISECONDS);
                else {
                    MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitSubscribe", "datasource=[" + dataSource.getType() + " " + dataSource.getId() + "] DataKitException e=" + throwable.getMessage());
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
                    return Observable.error(throwable);
                }
            }
        })).doOnUnsubscribe(() -> {
            unsubscribe(dataSource);
        })
                .doOnCompleted(() -> unsubscribe(dataSource))
                .doOnError(throwable -> unsubscribe(dataSource));
    }

    public void disconnect() {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
        if (dataKitAPI != null) {
            try {
                dataKitAPI.disconnect();
            } catch (Exception e) {
            }
        }
    }

    private void unsubscribe(DataSource dataSource) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            ArrayList<DataSourceClient> dataSourceClients = find(dataSource);
            for (int i = 0; i < dataSourceClients.size(); i++) {
                dataKitAPI.unsubscribe(dataSourceClients.get(i));

            }
        } catch (DataKitException e) {
            MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitUnSubscribe", "datasource=[" + dataSource.getType() + " " + dataSource.getId() + "] DataKitException e=" + e.getMessage());
        }
    }

    public Observable<Boolean> connect() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                try {
                    DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
                    dataKitAPI.connect(() -> {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    });

                } catch (Exception e) {
                    MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitConnect", "DataKitException e=" + e.getMessage());
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
                    subscriber.onError(e);
                }
            }
        });
    }

    public DataSourceClient register(DataSource dataSource) throws DataKitAccessError {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            return dataKitAPI.register(new DataSourceBuilder(dataSource));
        } catch (DataKitException e) {
            MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitRegister", "datasource=[" + dataSource.getType() + " " + dataSource.getId() + "] DataKitException e=" + e.getMessage());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
            throw new DataKitAccessError();
        }
    }

    public void unregister(DataSource dataSource) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            ArrayList<DataSourceClient> dataSourceClients = find(dataSource);
            for (int i = 0; i < dataSourceClients.size(); i++)
                dataKitAPI.unregister(dataSourceClients.get(i));
        } catch (DataKitException ignored) {

        }
    }

    public void insert(DataSourceClient dataSourceClient, DataType dataType) throws DataKitAccessError {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            dataKitAPI.insert(dataSourceClient, dataType);
        } catch (DataKitException e) {
            MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitInsert", "datasource=[" + dataSourceClient.getDataSource().getType() + " " + dataSourceClient.getDataSource().getId() + "] DataKitException e=" + e.getMessage());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
            throw new DataKitAccessError();
        }
    }

    public double insertIncentive(double amount) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            DataSourceBuilder db = new DataSourceBuilder().setType(DataSourceType.INCENTIVE);
            double total = queryTotalIncentive();
            double[] data = new double[]{amount, total + amount};
            DataSourceClient dsc = dataKitAPI.register(db);
            dataKitAPI.insert(dsc, new DataTypeDoubleArray(DateTime.getDateTime(), data));
            return total + amount;
        } catch (Exception e) {
            MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitInsertIncentive", "datasource=[INCENTIVE] DataKitException e=" + e.getMessage());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
        return amount;
    }

    public double queryTotalIncentive() {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            DataSourceBuilder db = new DataSourceBuilder().setType(DataSourceType.INCENTIVE);
            ArrayList<DataSourceClient> dsc = dataKitAPI.find(db);
            if (dsc.size() == 0) return 0;
            ArrayList<DataType> dt = dataKitAPI.query(dsc.get(0), 1);
            if (dt.size() == 0) return 0;
            DataTypeDoubleArray d = (DataTypeDoubleArray) dt.get(0);
            return d.getSample()[1];
        } catch (DataKitException e) {
            MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitIQueryIncentive", "datasource=[INCENTIVE] DataKitException e=" + e.getMessage());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
        return 0;
    }

    public ArrayList<DataType> query(DataSourceClient dataSourceClient, int i) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            return dataKitAPI.query(dataSourceClient, i);
        } catch (DataKitException e) {
            MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitQueryN", "datasource=[" + dataSourceClient.getDataSource().getType() + " " + dataSourceClient.getDataSource().getId() + "] DataKitException e=" + e.getMessage());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
            return new ArrayList<DataType>();
        }
    }

    public ArrayList<DataType> query(DataSourceClient dataSourceClient, long sTime, long eTime) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            return dataKitAPI.query(dataSourceClient, sTime, eTime);
        } catch (DataKitException e) {
            MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitQueryTime", "datasource=[" + dataSourceClient.getDataSource().getType() + " " + dataSourceClient.getDataSource().getId() + "] DataKitException e=" + e.getMessage());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
            return new ArrayList<DataType>();
        }
    }

    public void insert(String type, String id, String message) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(type).setId(id);
            DataSourceClient d = dataKitAPI.register(dataSourceBuilder);
            dataKitAPI.insert(d, new DataTypeString(DateTime.getDateTime(), message));
        } catch (Exception e) {
            MyLogger.getInstance().setDataKitErrorMessage("ERROR", "DataKitInsert", "datasource=[" + type + " " + id + "] DataKitException e=" + e.getMessage());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
    }

    public void insertSystemLog(String type, String path, String message) {
        Logger.d(type+","+path.replace(",",";")+","+message.replace(",",";"));
        Log.d("system_log", type + " -> " + path + " -> " + message);
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            if (dataSourceClients.get("SYSTEM_LOG") == null) {
                DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType("SYSTEM_LOG");
                DataSourceClient d = dataKitAPI.register(dataSourceBuilder);
                dataSourceClients.put("SYSTEM_LOG", d);
            }
            dataKitAPI.insert(dataSourceClients.get("SYSTEM_LOG"), new DataTypeStringArray(DateTime.getDateTime(), new String[]{DateTime.convertTimeStampToDateTime(DateTime.getDateTime()), type, path.replace(",", ";"), message.replace(",", ";")}));
        } catch (Exception e) {
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
    }

    public void insertSystemLogCondition(String path, ArrayList<String> details) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            if (dataSourceClients.get("CONDITION") == null) {
                DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType("CONDITION");
                DataSourceClient d = dataKitAPI.register(dataSourceBuilder);
                dataSourceClients.put("CONDITION", d);
            }
            for(int i=0;i<details.size();i+=3) {
                if(i+2>=details.size()) continue;

                String[] str=new String[5];
                str[0]=DateTime.convertTimeStampToDateTime(DateTime.getDateTime());
                str[1]=path;
                str[2]=details.get(i).replace(",",";");
                str[3]=details.get(i+1).replace(",",";");
                str[4]=details.get(i+2).replace(",",";");
                dataKitAPI.insert(dataSourceClients.get("CONDITION"), new DataTypeStringArray(DateTime.getDateTime(), str));
            }
        } catch (Exception e) {
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
    }

    public boolean isConnected() {
        return DataKitAPI.getInstance(MyApplication.getContext()).isConnected();
    }
}
