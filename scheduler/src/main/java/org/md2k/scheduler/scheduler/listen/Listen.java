package org.md2k.scheduler.scheduler.listen;
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

import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.Logger;
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataSourceNotFound;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Listen {
    private DataSource[] sources;

    Listen(DataSource[] sources) {
        this.sources = sources;
    }

    public Observable<Data> getObservable(String id, Logger logger, DataKitManager dataKitManager) {
        ArrayList<Observable<Data>> observables = new ArrayList<>();
        observables.add(Observable.just(null));
        if(sources !=null){
            for (DataSource aDatasource : sources) {
                observables.add(dataKitManager.subscribe(aDatasource));
                try {
                    ArrayList<Data> data = dataKitManager.getSample(aDatasource, 1);
                    if (data.size() != 0) {
                        if (data.get(0).getDataType() instanceof DataTypeLong) {
                            DataTypeLong dataTypeLong = (DataTypeLong) data.get(0).getDataType();
                            long timeDiff = dataTypeLong.getSample() - DateTime.getDateTime();
                            if (timeDiff > 0) {
                                observables.add(Observable.just(data.get(0)));
                            }
                        }
                    }
                } catch (DataKitAccessError e) {
                    return Observable.error(e);
                } catch (DataSourceNotFound dataSourceNotFound) {

                }
            }
        }
        return Observable.merge(observables).subscribeOn(Schedulers.computation())
                .flatMap(new Func1<Data, Observable<Data>>() {
                    @Override
                    public Observable<Data> call(Data data) {
                        if(data==null) return Observable.just(null);
                        if(data.getDataType() instanceof DataTypeLong){
                            long timeDiff = ((DataTypeLong)data.getDataType()).getSample()-DateTime.getDateTime();
                            if(timeDiff>0){
                                return Observable.just(data).delay(timeDiff, TimeUnit.MILLISECONDS);
                            }
                            else return Observable.just(data);
                        }
                        else return Observable.just(data);
                    }
                }).doOnNext(data -> {
                    String msg=null;
                    if(data!=null) {
                        msg = "type=" + data.getDataSourceClient().getDataSource().getType();
                        msg = "id=" + data.getDataSourceClient().getDataSource().getId();
                        msg = "application=" + data.getDataSourceClient().getDataSource().getApplication().getId();
                        logger.write(id + ".listen", "receivedDataFrom=(" + msg + ")");
                    }
                });
    }
}
