package org.md2k.scheduler.condition.input;
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

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataNotFoundErrorFalse;
import org.md2k.scheduler.exception.DataNotFoundErrorTrue;
import org.md2k.scheduler.search.SearchBySample;
import org.md2k.scheduler.search.SearchByTime;
import org.md2k.scheduler.search.SearchByValue;

import java.util.HashMap;

public class Input {
    private String input;
    private SearchBySample search_by_sample;
    private SearchByTime search_by_time;
    private SearchByValue search_by_value;

    public Input(String input, SearchBySample search_by_sample, SearchByTime search_by_time, SearchByValue search_by_value) {
        this.input = input;
        this.search_by_sample = search_by_sample;
        this.search_by_time = search_by_time;
        this.search_by_value = search_by_value;
    }

    public String execute(DataKitManager dataKitManager, HashMap<String, Object[]> variable) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorTrue, DataNotFoundErrorFalse {
        Object[] result=null;
        String msg=getInput()+"=";
        if (search_by_sample != null)
            result=search_by_sample.execute(dataKitManager);
        else if (search_by_time != null)
            result=search_by_time.execute(dataKitManager);
        else if (search_by_value != null)
            result=search_by_value.execute(dataKitManager);
        variable.put(getInput(), result);
        if(result==null){
            msg+="[null]";
        }
        else{
            msg+="[";
            for (int i = 0; i < result.length; i++) {
                if (i != 0) msg += ",";
                if (result[i] instanceof String)
                    msg += (String) result[i];
                else if (result[i] instanceof Double)
                    msg += String.valueOf(result[i]);
                else if (result[i] instanceof Long) {
                    long v = (Long) result[i];
                    if (v > 1000000000000L)
                        msg += DateTime.convertTimeStampToDateTime(v);
                    else msg += String.valueOf(result[i]);
                }
            }
            msg+="]";
        }
        return msg;
    }

    public String getInput() {
        return input.trim().toUpperCase();
    }
}
