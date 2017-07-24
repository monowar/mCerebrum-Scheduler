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


import org.junit.Test;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class DayTest {
    @Test
    public void F_CT() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()+ 1*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()+ 2*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
//                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart));
                    }else{
//                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void F_CT_DS1() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()+ 1*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()+ 2*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
//                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void T_DS8_CT() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()- 8*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
//                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertTrue(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void F_DS25_CT() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()- 25*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
//                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }

    @Test
    public void T_DS1_CT_DE1() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()- 60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()+ 60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertTrue(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void T_DS1_CT_DE30() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()- 60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()+ 30*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertTrue(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void F_DS25_CT_DE30() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()- 25*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()+ 30*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }

    @Test
    public void T_DE2_DS1_CT() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()- 60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()- 2*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertTrue(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void F_DE27_DS25_CT() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()- 25*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()- 27*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }


    @Test
    public void F_DS2_DE1_CT() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()- 2*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()- 1*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void F_DS27_DE25_CT() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()- 27*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()- 25*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }

    @Test
    public void F_CT_DS1_DE2() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()+ 1*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()+ 2*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void F_CT_DS25_DE27() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()+ 25*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()+ 27*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }

    @Test
    public void F_CT_DE2_DS3() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()+ 3*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()+ 2*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }

    @Test
    public void F_DE1_CT_DS3() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            long dayStart=DateTime.getDateTime()+ 3*60 * 60 * 1000L;
            long dayEnd=DateTime.getDateTime()- 1*60 * 60 * 1000L;
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes= new ArrayList<>();
                    if(((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayStart)));
                    }else{
                        dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(), dayEnd)));
                    }
                    return dataTypes;
                }
            });
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "valid_day==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }


}
*/
