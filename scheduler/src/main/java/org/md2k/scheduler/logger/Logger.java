package org.md2k.scheduler.logger;
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
import android.util.Log;

import org.md2k.datakitapi.datatype.DataTypeStringArray;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.Constants;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.DataKitAccessError;

public class Logger {
    private DataKitManager dataKitManager;
    private DataSourceClient dataSourceClient=null;
    private SharedPreferences sharedPreferences;

    public Logger(Context context, DataKitManager dataKitManager) {
        this.dataKitManager = dataKitManager;
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    public void write(String id, String message) {
        Log.d("Logger","[id = "+id+"][message = "+message+"]");
/*
        try {
            if(dataSourceClient==null){
                DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.LOG);
                dataSourceClient = dataKitManager.register(dataSourceBuilder.build());
            }
            long time = DateTime.getDateTime();
            String timeStr = DateTime.convertTimeStampToDateTime(time);
            String[] s = new String[]{timeStr, id, message};
            DataTypeStringArray dataTypeStringArray = new DataTypeStringArray(DateTime.getDateTime(), s);
            dataKitManager.insert(dataSourceClient, dataTypeStringArray);
            System.out.println(timeStr+": "+id+": "+message);
        } catch (DataKitAccessError dataKitAccessError) {
            dataKitAccessError.printStackTrace();
        }
*/
    }
    public int getNumberOfTry(String type, String id, long today, int index){
        if(sharedPreferences.getLong(getKeyTime(type, id, index),0)!=today){
            setNewTime(getKeyTime(type, id,index), today);
            setNewTry(getKeyTry(type, id,index),0);
        }
        return sharedPreferences.getInt(getKeyTry(type, id, index),0);
    }

    public void setNumberOfTry(String type, String id, long today, int index, int tries){
        if(sharedPreferences.getLong(getKeyTime(type, id, index),0)!=today){
            setNewTime(getKeyTime(type, id,index), today);
        }
        setNewTry(getKeyTry(type, id,index),tries);
    }
    private void setNewTime(String key, long today){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key,today);
        editor.apply();
    }
    private void setNewTry(String key, int tries){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,tries);
        editor.apply();
    }
    private String getKeyTime(String type, String id, int index){
        if(id==null) id="";
        if(type==null) type="";
        return type+"_"+id+"_"+Integer.toString(index)+"_time";
    }
    private String getKeyTry(String type, String id, int index){
        if(id==null) id="";
        if(type==null) type="";
        return type+"_"+id+"_"+Integer.toString(index)+"_try";
    }

}