package org.md2k.scheduler;
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
import android.content.SharedPreferences;

import org.md2k.datakitapi.datatype.DataTypeStringArray;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.DataKitAccessError;

public class Logger {
    private Context context;
    private DataKitManager dataKitManager;
    private SharedPreferences sharedPreferences;
    private DataSourceClient dataSourceClient;

    Logger(Context context, DataKitManager dataKitManager) throws DataKitAccessError {
        this.context = context;
        this.dataKitManager = dataKitManager;
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.LOG);
        dataSourceClient = dataKitManager.register(dataSourceBuilder.build());
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    public int getInt(String id) {
        return sharedPreferences.getInt(id, -1);
    }

    public int set(String id, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(id, value);
        editor.apply();
        return value;
    }

    public void write(String id, String message) {
        try {
            long time = DateTime.getDateTime();
            String timeStr = DateTime.convertTimeStampToDateTime(time);
            String[] s = new String[]{timeStr, id, message};
            DataTypeStringArray dataTypeStringArray = new DataTypeStringArray(DateTime.getDateTime(), s);
            dataKitManager.insert(dataSourceClient, dataTypeStringArray);
            System.out.println(timeStr+": "+id+": "+message);
        } catch (DataKitAccessError dataKitAccessError) {
            dataKitAccessError.printStackTrace();
        }
    }
}
