package org.md2k.scheduler.condition.function;
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

import com.udojava.evalex.Expression;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.source.application.ApplicationBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platformapp.PlatformAppBuilder;
import org.md2k.mcerebrum.core.data_format.DATA_QUALITY;
import org.md2k.scheduler.datakit.DataKitManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class get_data_quality extends Function {
    public get_data_quality(DataKitManager dataKitManager) {
        super(dataKitManager);
    }

    public Expression add(Expression e) {
        e.addLazyFunction(e.new LazyFunction("get_data_quality", -1) {
            @Override
            public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
                //todo: check data quality code
                double good =0;
                DataSourceBuilder d = createDataSource(lazyParams,2);
                long sTime = lazyParams.get(0).eval().longValue();
                long eTime = lazyParams.get(1).eval().longValue();
                sTime=sTime-sTime%(1000*60);
                eTime = eTime-eTime%(1000*60);
                ArrayList<DataSourceClient> dd = dataKitManager.find(d.build());
                for (int i = 0; i < dd.size(); i++) {
                    ArrayList<DataType> dataTypes = dataKitManager.query(dd.get(i), sTime, eTime);
                    double g=getDataQuality(dataTypes, sTime, eTime);
                    if(g>good) good = g;
                }
                return create(good);
            }
        });
        return e;
    }
    private double getDataQuality(ArrayList<DataType> dataTypes, long sTime, long eTime){
        long minute = (eTime - sTime)/(1000*60);
        long goodMinute = 0;
        for(int i=0;i<dataTypes.size();i++){
            int[] res=((DataTypeIntArray)(dataTypes.get(i))).getSample();
            if(res[DATA_QUALITY.GOOD]>30) goodMinute++;
        }
        return ((goodMinute*100.0)/minute);
    }

    public String prepare(String s) {
        return s;
    }
}
