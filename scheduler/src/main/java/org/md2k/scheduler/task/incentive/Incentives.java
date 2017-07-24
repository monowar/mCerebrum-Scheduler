package org.md2k.scheduler.task.incentive;
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
import android.content.Intent;

import org.md2k.datakitapi.datatype.DataTypeStringArray;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.Logger;
import org.md2k.scheduler.condition.Conditions;
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataSourceNotFound;
import org.md2k.scheduler.task.Response;

import java.util.ArrayList;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;

public class Incentives {
    public Observable<Response> getObservable(Context context, String path, Logger logger, DataKitManager dataKitManager, Conditions conditions, Incentive[] incentives) {
        path+="/incentive";
        if (incentives == null) return null;
        for (Incentive incentive : incentives) {
            try {
                if (conditions.isValid(path, logger, dataKitManager, incentive.getCondition())) {
                    return getObservableIncentive(path, logger, context, dataKitManager, incentive);
                }
            } catch (ConfigurationFileFormatError | DataKitAccessError e) {
                return Observable.error(e);
            }
        }
        return null;
    }

    private Observable<Response> getObservableIncentive(String id, Logger logger, Context context, DataKitManager dataKitManager, Incentive incentive) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    double totalIncentive = getLastTotalIncentive(dataKitManager, incentive)+incentive.getIncentive();
                    save(dataKitManager, id, incentive, totalIncentive);
                    show(context, incentive.getMessages(), totalIncentive);
                    subscriber.onNext(new Response(null,"ok",null));
                    subscriber.onCompleted();
                } catch (DataKitAccessError e) {
                    subscriber.onError(e);
                }

            }
        }).filter(s -> {
            if(incentive.getSkip()==null) return true;
            for (String aSkip : incentive.getSkip())
                if (aSkip.trim().toUpperCase().equals(s.getInput().trim().toUpperCase()))
                    return false;
            return true;
        });
    }
    private void save(DataKitManager dataKitManager, String id, Incentive incentive, double totalIncentive) throws DataKitAccessError {
        String[] result=new String[4];
        result[0]=String.format(Locale.getDefault(), "%.2f",incentive.getIncentive());
        result[1]=String.format(Locale.getDefault(), "%.2f",totalIncentive);
        result[2]=id;
        result[3]=incentive.getCondition();
        DataTypeStringArray dataTypeStringArray=new DataTypeStringArray(DateTime.getDateTime(), result);
        DataSourceClient dataSourceClient = dataKitManager.register(incentive.getData_source());
        dataKitManager.insert(dataSourceClient, dataTypeStringArray);
    }

    private void show(Context context, String[] message, double totalIncentive){
        Intent intent = new Intent(context, ActivityIncentive.class);
        intent.putExtra("messages", message);
        intent.putExtra("total_incentive", totalIncentive);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private double getLastTotalIncentive(DataKitManager dataKitManager, Incentive incentive) throws DataKitAccessError {
        try {
            ArrayList<Data> data = null;
            data = dataKitManager.getSample(incentive.getData_source(), 1);
            if (data.size() == 0) return 0;
            DataTypeStringArray dataTypeStringArray = (DataTypeStringArray) data.get(0).getDataType();
            return Double.parseDouble(dataTypeStringArray.getSample()[1]);
        } catch (DataSourceNotFound dataSourceNotFound) {
            return 0.0;
        }
    }
}
