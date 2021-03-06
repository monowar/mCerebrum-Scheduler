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
import org.md2k.datakitapi.datatype.DataTypeDouble;
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

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StudyStartTest {
    @Test
    public void sample_5_min_later() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            ArrayList<Data> dataTypes = new ArrayList<>();
            dataTypes.add(new Data(null, new DataTypeLong(DateTime.getDateTime(),DateTime.getDateTime()+5*60*1000)));
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class),anyInt())).thenReturn(dataTypes);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "study_start==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void sample_5_min_before() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            ArrayList<Data> dataTypes = new ArrayList<>();
            dataTypes.add(new Data(null,new DataTypeLong(DateTime.getDateTime(),DateTime.getDateTime()-5*60*1000)));
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class),anyInt())).thenReturn(dataTypes);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "study_start==TRUE");
            assertTrue(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }
    @Test
    public void sample_empty() {
        try {
            Configuration configuration = new ConfigurationTest().read();
            ArrayList<Data> dataTypes = new ArrayList<>();
            DataKitManager dataKitManager= mock(DataKitManager.class);
            when(dataKitManager.getSample(any(DataSource.class),anyInt())).thenReturn(dataTypes);
            Conditions conditions = new Conditions(configuration.getConditions());
            boolean check1 = conditions.isValid(dataKitManager, "study_start==TRUE");
            assertFalse(check1);
        } catch (DataSourceNotFound | DataKitAccessError | ConfigurationFileFormatError dataSourceNotFound) {
            assertTrue(false);
        }
    }

}
*/
