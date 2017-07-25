package org.md2k.scheduler.task.save;
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

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.scheduler.Logger;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.task.Response;

import rx.Observable;
import rx.functions.Func1;

public class Save {
    private DataSource data_source;
    private Data data;
    private String[] skip;
    private Response save(Context context, String path, Logger logger, DataKitManager dataKitManager, Response response) throws DataKitAccessError {
        DataSourceClient dataSourceClient = dataKitManager.register(data_source);
        DataType dataType=data.get(response);
        dataKitManager.insert(dataSourceClient, dataType);
        return new Response(null, "ok",null);
    }
    Observable<Response> getObservable(Context context, String path, Logger logger, DataKitManager dataKitManager, Response response){
        return Observable.just(true)
                .flatMap(new Func1<Boolean, Observable<Response>>() {
                    @Override
                    public Observable<Response> call(Boolean aBoolean) {
                        try {
                            return Observable.just(save(context, path, logger, dataKitManager, response));
                        } catch (DataKitAccessError dataKitAccessError) {
                            return Observable.error(dataKitAccessError);
                        }
                    }
                })
                .filter(s -> {
                    if(skip==null) return true;
                    for (String aSkip : skip)
                        if (aSkip.trim().toUpperCase().equals(s.getInput().trim().toUpperCase()))
                            return false;
                    return true;
                });
    }
}
