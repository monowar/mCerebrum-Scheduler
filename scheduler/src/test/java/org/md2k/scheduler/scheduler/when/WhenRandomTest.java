package org.md2k.scheduler.scheduler.when;
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

import org.junit.Test;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.Logger;
import org.md2k.scheduler.condition.Conditions;
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
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observer;
import rx.Subscription;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenRandomTest {


    @Test
    public void testRandom() throws ConfigurationFileFormatError, DataKitAccessError {
        Logger logger = mock(Logger.class);
        final int[] retry = {-1};
        when(logger.getInt(anyString())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return retry[0];
            }
        });
        when(logger.set(anyString(),anyInt())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                retry[0]++;
                return retry[0];
            }
        });
        DataTypeLong dataTypeLong=new DataTypeLong(DateTime.getDateTime(), DateTime.getDateTime()-10000);
        DataKitManager dataKitManager=getDataKit(dataTypeLong);
        ArrayList<String> results = new ArrayList<>();
        AtomicBoolean a = new AtomicBoolean();
        Conditions conditions = getConditions();
        When when = create(0);
        Subscription s = when.getObservable(anyString(), logger, a, dataKitManager, conditions).subscribe(new Observer<Long[]>() {
            @Override
            public void onCompleted() {
                results.add("C");
                System.out.println("onCompleted()...");
                assertTrue(true);
            }

            @Override
            public void onError(Throwable e) {
                results.add("E");
                assertTrue(false);

            }

            @Override
            public void onNext(Long[] longs) {
                System.out.println("N");
//                results.add("N");
//                System.out.println("onNext()...time="+DateTime.getDateTime()+" data="+data);
                assertTrue(true);
            }
        });
        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertArrayEquals(results.toArray(), new String[]{"N", "N", "N"});

    }

    private When create(int which) {
        ConfigurationTest configurationTest = new ConfigurationTest();
        Configuration configuration = configurationTest.read("config_when_random.json");
        return configuration.getSchedulers()[which].getWhen();
    }

    private Conditions getConditions() throws ConfigurationFileFormatError, DataKitAccessError {
        Conditions conditions = mock(Conditions.class);
        when(conditions.isValid(anyString(), any(Logger.class), any(DataKitManager.class), anyString())).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();

                switch ((String) args[3]) {
                    case "a":
                        return true;
                    case "b":
                        return true;
                    case "c":
                        return false;
                    case "d":
                        return false;
                    case "e":
                        return false;
                    default:
                        return true;
                }
            }
        });
        return conditions;
    }

    private DataKitManager getDataKit(DataType dayStart) {
        DataKitManager dataKitManager = mock(DataKitManager.class);
        try {
            when(dataKitManager.getSample(any(DataSource.class), eq(1))).thenAnswer(new Answer<ArrayList<Data>>() {
                @Override
                public ArrayList<Data> answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    ArrayList<Data> dataTypes = new ArrayList<>();
                    if (((DataSource) args[0]).getType().equals("DAY_START")) {
                        dataTypes.add(new Data(null, dayStart));
                    }
                    return dataTypes;
                }
            });

        } catch (DataKitAccessError dataKitAccessError) {
            dataKitAccessError.printStackTrace();
        } catch (DataSourceNotFound dataSourceNotFound) {
            dataSourceNotFound.printStackTrace();
        }
        return dataKitManager;
    }
}