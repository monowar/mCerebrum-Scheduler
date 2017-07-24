package org.md2k.scheduler.search;

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

import com.google.gson.JsonObject;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeBoolean;
import org.md2k.datakitapi.datatype.DataTypeBooleanArray;
import org.md2k.datakitapi.datatype.DataTypeByte;
import org.md2k.datakitapi.datatype.DataTypeByteArray;
import org.md2k.datakitapi.datatype.DataTypeDouble;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeFloat;
import org.md2k.datakitapi.datatype.DataTypeFloatArray;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.datatype.DataTypeLongArray;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.datatype.DataTypeStringArray;
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataNotFoundError;
import org.md2k.scheduler.exception.DataNotFoundErrorFalse;
import org.md2k.scheduler.exception.DataNotFoundErrorTrue;
import org.md2k.scheduler.exception.DataSourceNotFound;

import java.util.ArrayList;

public abstract class SearchDataKit extends Search {
    private String index;
    private String not_found_default;
    private static final String NOT_FOUND_TRUE="TRUE";
    private static final String NOT_FOUND_FALSE="FALSE";
    private static final String NOT_FOUND_NULL="NULL";

    public SearchDataKit(String type, String data_type, String index) {
        super(type, data_type);
        this.index = index;
    }

    public abstract ArrayList<Data> query(DataKitManager dataKitManager) throws DataKitAccessError, DataSourceNotFound, ConfigurationFileFormatError, DataNotFoundError;

    @Override
    Double[] getSamplesAsDouble(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorFalse, DataNotFoundErrorTrue {
        try {
            ArrayList<Data> dataTypes = query(dataKitManager);
            Double[] data = new Double[dataTypes.size()];
            for (int i = 0; i < dataTypes.size(); i++)
                data[i] = getValueAsDouble(dataTypes.get(i).getDataType());
            return data;
        }catch(DataNotFoundError | DataSourceNotFound e){
            if(not_found_default==null ||NOT_FOUND_FALSE.equals(not_found_default.trim().toUpperCase())) throw new DataNotFoundErrorFalse();
            else if(NOT_FOUND_TRUE.equals(not_found_default.trim().toUpperCase())) throw new DataNotFoundErrorTrue();
            else if(NOT_FOUND_NULL.equals(not_found_default.trim().toUpperCase())) {
                return new Double[0];
            }else{
                Double[] data = new Double[1];
                data[0]=Double.parseDouble(not_found_default);
                return data;
            }
        }
    }
    @Override
    Long[] getTimes(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorFalse, DataNotFoundErrorTrue {
        try{
        ArrayList<Data> dataTypes= query(dataKitManager);
        Long[] times = new Long[dataTypes.size()];
        for (int i = 0; i < dataTypes.size(); i++)
            times[i] = dataTypes.get(i).getDataType().getDateTime();
        return times;
        }catch(DataNotFoundError | DataSourceNotFound e){
            if(not_found_default==null ||NOT_FOUND_FALSE.equals(not_found_default.trim().toUpperCase())) throw new DataNotFoundErrorFalse();
            else if(NOT_FOUND_TRUE.equals(not_found_default.trim().toUpperCase())) throw new DataNotFoundErrorTrue();
            else if(NOT_FOUND_NULL.equals(not_found_default.trim().toUpperCase())) {
                return new Long[0];
            }else{
                Long[] data = new Long[1];
                data[0]=Long.parseLong(not_found_default);
                return data;
            }
        }
    }
    @Override
    Long[] getSamplesAsLong(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorFalse, DataNotFoundErrorTrue {
        try{
        ArrayList<Data> dataTypes= query(dataKitManager);
        Long[] data=new Long[dataTypes.size()];
        for(int i=0;i<dataTypes.size();i++)
            data[i]=getValueAsLong(dataTypes.get(i).getDataType());
        return data;
        }catch(DataNotFoundError | DataSourceNotFound e){
            if(not_found_default==null ||NOT_FOUND_FALSE.equals(not_found_default.trim().toUpperCase())) throw new DataNotFoundErrorFalse();
            else if(NOT_FOUND_TRUE.equals(not_found_default.trim().toUpperCase())) throw new DataNotFoundErrorTrue();
            else if(NOT_FOUND_NULL.equals(not_found_default.trim().toUpperCase())) {
                return new Long[0];
            }else{
                Long[] data = new Long[1];
                data[0]=Long.parseLong(not_found_default);
                return data;
            }
        }
    }
    @Override
    String[] getSamplesAsString(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorFalse, DataNotFoundErrorTrue {
        try {
            ArrayList<Data> dataTypes = query(dataKitManager);
            String[] data = new String[dataTypes.size()];
            for (int i = 0; i < dataTypes.size(); i++)
                data[i] = getValueAsString(dataTypes.get(i).getDataType());
            return data;
        }catch(DataNotFoundError | DataSourceNotFound e){
            if(not_found_default==null ||NOT_FOUND_FALSE.equals(not_found_default.trim().toUpperCase())) throw new DataNotFoundErrorFalse();
            else if(NOT_FOUND_TRUE.equals(not_found_default.trim().toUpperCase())) throw new DataNotFoundErrorTrue();
            else if(NOT_FOUND_NULL.equals(not_found_default.trim().toUpperCase())) {
                return new String[0];
            }else{
                String[] data = new String[1];
                data[0]=not_found_default;
                return data;
            }
        }
    }

    private Double getValueAsDouble(DataType dataType) {
        if(dataType instanceof DataTypeLong)
            return (double) ((DataTypeLong) dataType).getSample();
        else if(dataType instanceof DataTypeLongArray)
            return (double) ((DataTypeLongArray) dataType).getSample()[Integer.valueOf(index)];
        else if(dataType instanceof DataTypeDouble)
            return ((DataTypeDouble) dataType).getSample();
        else if(dataType instanceof DataTypeDoubleArray)
            return ((DataTypeDoubleArray) dataType).getSample()[Integer.valueOf(index)];
        else if(dataType instanceof DataTypeInt)
            return (double)( ((DataTypeInt) dataType).getSample());
        else if(dataType instanceof DataTypeIntArray)
            return (double)( ((DataTypeIntArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeFloat)
            return (double)( ((DataTypeFloat) dataType).getSample());
        else if(dataType instanceof DataTypeFloatArray)
            return (double)( ((DataTypeFloatArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeBoolean) {
            if (((DataTypeBoolean) dataType).getSample())
                return 0.0;
            else return 1.0;
        }
        else if(dataType instanceof DataTypeBooleanArray) {
            if (((DataTypeBooleanArray) dataType).getSample()[Integer.valueOf(index)])
                return 0.0;
            else return 1.0;
        }
        else if(dataType instanceof DataTypeByte)
            return (double)(((DataTypeByte) dataType).getSample());
        else if(dataType instanceof DataTypeByteArray)
            return (double)(((DataTypeByteArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeString)
            return Double.parseDouble(((DataTypeString) dataType).getSample());
        else if(dataType instanceof DataTypeStringArray)
            return Double.parseDouble(((DataTypeStringArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeJSONObject){
            JsonObject json=((DataTypeJSONObject)dataType).getSample();
            return json.get(index).getAsDouble();
        }
        return null;
    }
    private Long getValueAsLong(DataType dataType) {
        if(dataType instanceof DataTypeLong)
            return ((DataTypeLong) dataType).getSample();
        else if(dataType instanceof DataTypeLongArray)
            return ((DataTypeLongArray) dataType).getSample()[Integer.valueOf(index)];
        else if(dataType instanceof DataTypeDouble)
            return (long)(((DataTypeDouble) dataType).getSample());
        else if(dataType instanceof DataTypeDoubleArray)
            return (long)(((DataTypeDoubleArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeInt)
            return (long)( ((DataTypeInt) dataType).getSample());
        else if(dataType instanceof DataTypeIntArray)
            return (long)( ((DataTypeIntArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeFloat)
            return (long)( ((DataTypeFloat) dataType).getSample());
        else if(dataType instanceof DataTypeFloatArray)
            return (long)( ((DataTypeFloatArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeBoolean) {
            if (((DataTypeBoolean) dataType).getSample())
                return 0L;
            else return 1L;
        }
        else if(dataType instanceof DataTypeBooleanArray) {
            if (((DataTypeBooleanArray) dataType).getSample()[Integer.valueOf(index)])
                return 0L;
            else return 1L;
        }
        else if(dataType instanceof DataTypeByte)
            return (long)(((DataTypeByte) dataType).getSample());
        else if(dataType instanceof DataTypeByteArray)
            return (long)(((DataTypeByteArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeString)
            return Long.parseLong(((DataTypeString) dataType).getSample());
        else if(dataType instanceof DataTypeStringArray)
            return Long.parseLong(((DataTypeStringArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeJSONObject){
            JsonObject json=((DataTypeJSONObject)dataType).getSample();
            return json.get(index).getAsLong();
        }
        return null;

    }
    private String getValueAsString(DataType dataType) {
        if(dataType instanceof DataTypeLong)
            return String.valueOf(((DataTypeLong) dataType).getSample());
        else if(dataType instanceof DataTypeLongArray)
            return String.valueOf(((DataTypeLongArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeDouble)
            return String.valueOf(((DataTypeDouble) dataType).getSample());
        else if(dataType instanceof DataTypeDoubleArray)
            return String.valueOf(((DataTypeDoubleArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeInt)
            return String.valueOf( ((DataTypeInt) dataType).getSample());
        else if(dataType instanceof DataTypeIntArray)
            return String.valueOf( ((DataTypeIntArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeFloat)
            return String.valueOf( ((DataTypeFloat) dataType).getSample());
        else if(dataType instanceof DataTypeFloatArray)
            return String.valueOf( ((DataTypeFloatArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeBoolean)
            return String.valueOf( ((DataTypeBoolean) dataType).getSample());
        else if(dataType instanceof DataTypeBooleanArray)
            return String.valueOf(((DataTypeBooleanArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeByte)
            return String.valueOf(((DataTypeByte) dataType).getSample());
        else if(dataType instanceof DataTypeByteArray)
            return String.valueOf(((DataTypeByteArray) dataType).getSample()[Integer.valueOf(index)]);
        else if(dataType instanceof DataTypeString)
            return ((DataTypeString) dataType).getSample();
        else if(dataType instanceof DataTypeStringArray)
            return ((DataTypeStringArray) dataType).getSample()[Integer.valueOf(index)];
        else if(dataType instanceof DataTypeJSONObject){
            JsonObject json=((DataTypeJSONObject)dataType).getSample();
            return json.get(index).getAsString();
        }
        return null;
    }
}
