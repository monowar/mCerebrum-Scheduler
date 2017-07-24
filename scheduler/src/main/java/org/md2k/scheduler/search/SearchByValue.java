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

import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.time.Time;

public class SearchByValue extends Search {
    private String[] values;

    public SearchByValue(String type, String data_type, String[] values) {
        super(type, data_type);
        this.values = values;
    }

    @Override
    Object[] getSamplesAsLong(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError {
        Long[] data=new Long[values.length];
        for(int i=0;i<values.length;i++)
            data[i]=Long.parseLong(values[i]);
        return data;
    }

    @Override
    Object[] getSamplesAsDouble(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError {
        Double[] data=new Double[values.length];
        for(int i=0;i<values.length;i++)
            data[i]=Double.parseDouble(values[i]);
        return data;
    }

    @Override
    Object[] getSamplesAsString(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError {
        return values;
    }

    @Override
    Object[] getTimes(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError {
        Long[] data=new Long[values.length];
        for(int i=0;i<values.length;i++)
            data[i]=Time.getTime(values[i]);
        return data;
    }
}
