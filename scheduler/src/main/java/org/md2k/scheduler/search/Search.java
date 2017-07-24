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
import org.md2k.scheduler.exception.DataNotFoundErrorFalse;
import org.md2k.scheduler.exception.DataNotFoundErrorTrue;

public abstract class Search {
    private static final String TYPE_TIME = "TIME";
    private static final String TYPE_SAMPLE = "SAMPLE";
    private static final String DATATYPE_LONG = "LONG";
    private static final String DATATYPE_DOUBLE = "DOUBLE";
    private static final String DATATYPE_STRING = "STRING";

    private String type;
    private String data_type;

    public Search(String type, String data_type) {
        this.type = type;
        this.data_type = data_type;
    }

    abstract Object[] getSamplesAsLong(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorFalse, DataNotFoundErrorTrue;
    abstract Object[] getSamplesAsDouble(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorFalse, DataNotFoundErrorTrue;
    abstract Object[] getSamplesAsString(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorFalse, DataNotFoundErrorTrue;
    abstract Object[] getTimes(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorFalse, DataNotFoundErrorTrue;

    public Object[] execute(DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError, DataNotFoundErrorFalse, DataNotFoundErrorTrue {
        if (TYPE_SAMPLE.equals(type.toUpperCase()))
            return getSamples(dataKitManager);
        else if (TYPE_TIME.equals(type.toUpperCase()))
            return getTimes(dataKitManager);
        else return null;
    }
    private Object[] getSamples(DataKitManager dataKitManager) throws ConfigurationFileFormatError, DataKitAccessError, DataNotFoundErrorFalse, DataNotFoundErrorTrue {
        if(DATATYPE_LONG.equals(data_type.toUpperCase()))
            return getSamplesAsLong(dataKitManager);
        else if(DATATYPE_DOUBLE.equals(data_type.toUpperCase()))
            return getSamplesAsDouble(dataKitManager);
        else if(DATATYPE_STRING.equals(data_type.toUpperCase()))
            return getSamplesAsString(dataKitManager);
        else throw new ConfigurationFileFormatError();
    }
}
