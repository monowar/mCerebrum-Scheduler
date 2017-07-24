package org.md2k.scheduler.createdata;
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

import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataSourceNotFound;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import rx.Observable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateDataKitManager {
    public static DataKitManager create() {
        try {
            DataKitManager dataKitManager = mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime()+10000)));
                    }else{
//                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd));
                    }
                    return dataTypes;
                }
            });
            when(dataKitManager.subscribe(any(DataSource.class))).thenAnswer(new Answer<Observable<Data>>() {
                @Override
                public Observable<Data> answer(InvocationOnMock invocation) throws Throwable {
/*
                    Object[] args = invocation.getArguments();
                    if (((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), 10)));
                    } else {
//                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd));
                    }
*/
                    return Observable.just(new Data(null,new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime()+10000)));
                }
            });
            return dataKitManager;
        } catch (DataKitAccessError dataKitAccessError) {
            dataKitAccessError.printStackTrace();
        } catch (DataSourceNotFound dataSourceNotFound) {
            dataSourceNotFound.printStackTrace();
        }
        return null;
    }
}
