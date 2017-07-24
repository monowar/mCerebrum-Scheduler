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

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataNotFoundError;
import org.md2k.scheduler.exception.DataSourceNotFound;
import org.md2k.scheduler.search.SearchDataKit;

import java.util.ArrayList;

public class SearchBySample extends SearchDataKit {
    private DataSource source;
    private int sample_no;

    public SearchBySample(String type, String data_type, String index, DataSource source, int sample_no) {
        super(type, data_type, index);
        this.source = source;
        this.sample_no = sample_no;
    }

    @Override
    public ArrayList<Data> query(DataKitManager dataKitManager) throws DataKitAccessError, DataSourceNotFound, DataNotFoundError {
        ArrayList<Data> data = dataKitManager.getSample(source, sample_no);
        if(data.size()==0) throw new DataNotFoundError();
        return data;
    }

}
