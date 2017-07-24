/*
package org.md2k.scheduler.condition;
*/
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
 *//*


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Test;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.configuration.ConfigurationTest;
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataSourceNotFound;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlockStressTest {
    @Test
    public void block1_0() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            DataKitManager dataKitManager=getDataKitManager(-1, 0);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "block1_4hour_stress_ema==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void block1_1H_0() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            DataKitManager dataKitManager=getDataKitManager(DateTime.getDateTime()-60*60*1000, 0);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "block1_4hour_stress_ema==TRUE");
            assertTrue(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void block1_5H_0() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            DataKitManager dataKitManager=getDataKitManager(DateTime.getDateTime()-5*60*60*1000, 0);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "block1_4hour_stress_ema==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void block1_1H_1() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            DataKitManager dataKitManager=getDataKitManager(DateTime.getDateTime()-60*60*1000, 1);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "block1_4hour_stress_ema==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void block1_2H_3() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            DataKitManager dataKitManager=getDataKitManager(DateTime.getDateTime()-2*60*60*1000, 3);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "block1_4hour_stress_ema==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void block1_P2H_0() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            DataKitManager dataKitManager=getDataKitManager(DateTime.getDateTime()+2*60*60*1000, 0);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "block1_4hour_stress_ema==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void block1_P2H_2() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            DataKitManager dataKitManager=getDataKitManager(DateTime.getDateTime()+2*60*60*1000, 0);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "block1_4hour_stress_ema==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }

    private DataKitManager getDataKitManager(long dayStart, int ema) throws DataKitAccessError, DataSourceNotFound {
        DataKitManager dataKitManager= mock(DataKitManager.class);
        when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
            @Override
            public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                ArrayList<Data> dataTypes= new ArrayList<>();
                if(((DataSource) args[0]).getType().equals("DAY_START") && dayStart!=-1) {
                    dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                }else{
//                    dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd));
                }
                return dataTypes;
            }
        });
        when(dataKitManager.getSample(any(DataSource.class), anyLong(),anyLong())).thenAnswer(new Answer<ArrayList<Data>>() {
            @Override
            public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                ArrayList<Data> dataTypes= new ArrayList<>();
                if(((DataSource) args[0]).getType().equals("EMA")) {
                    for(int i=0;i<ema;i++)
                        dataTypes.add(new Data(null, new DataTypeJSONObject(DateTime.getDateTime(), (JsonObject) new JsonParser().parse("{abc:10}"))));
                }else{
//                    dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd));
                }
                return dataTypes;
            }
        });

        return dataKitManager;

    }
}
*/
