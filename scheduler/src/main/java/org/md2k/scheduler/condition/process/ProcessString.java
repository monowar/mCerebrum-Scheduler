package org.md2k.scheduler.condition.process;
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

import java.util.ArrayList;

class ProcessString extends ProcessData{

    @Override
    String[] equal(Object[] inn1, Object[] inn2){
        String[] in1=prepare(inn1);
        String[] in2=prepare(inn2);
        String[] out;
        ArrayList<String> output=new ArrayList<>();
        String d1, d2;
        for (String anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (String anIn2 : in2) {
                d2 = anIn2;
                if (d1.trim().toUpperCase().equals(d2.trim().toUpperCase())) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1.trim().toUpperCase());
        }

        out= output.toArray(new String[output.size()]);
        return out;
    }
    @Override
    String[] notEqual(Object[] inn1, Object[] inn2){
        String[] in1=prepare(inn1);
        String[] in2=prepare(inn2);
        String[] out;
        ArrayList<String> output=new ArrayList<>();
        String d1, d2;
        for (String anIn1 : in1) {
            d1 = anIn1;
            boolean flag=true;
            for (String anIn2 : in2) {
                d2 = anIn2;
                if (d1.trim().toUpperCase().equals(d2.trim().toUpperCase())) {
                    flag=false;
                }
            }
            if(flag)
                output.add(d1.trim().toUpperCase());
        }
        out= output.toArray(new String[output.size()]);
        return out;
    }

    private String[] prepare(Object[] in){
        if(in instanceof String[]) return (String[]) in;
        else if(in instanceof Long[]) return convertLongToString((Long[]) in);
        else if(in instanceof Double[]) return convertDoubleToString((Double[]) in);
        else return null;
    }

    private String[] convertDoubleToString(Double[] doubleData) {
        String[] stringData = new String[doubleData.length];
        for (int i = 0; i < doubleData.length; i++)
            stringData[i] = String.valueOf(doubleData[i]);
        return stringData;
    }
    private String[] convertLongToString(Long[] longData) {
        String[] stringData = new String[longData.length];
        for (int i = 0; i < longData.length; i++)
            stringData[i] = String.valueOf(longData[i]);
        return stringData;
    }
}
