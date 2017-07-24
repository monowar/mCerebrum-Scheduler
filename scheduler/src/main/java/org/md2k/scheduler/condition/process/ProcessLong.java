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

import org.md2k.datakitapi.time.DateTime;

import java.util.ArrayList;

class ProcessLong extends ProcessData {

    @Override
    Long[] greater(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        ArrayList<Long> output=new ArrayList<>();
        Long d1, d2;
        for (Long anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Long anIn2 : in2) {
                d2 = anIn2;
                if (d1 > d2) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        return output.toArray(new Long[output.size()]);
    }
    @Override
    Long[] greaterOrEqual(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Long[] out;
        ArrayList<Long> output=new ArrayList<>();
        Long d1, d2;
        for (Long anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Long anIn2 : in2) {
                d2 = anIn2;
                if (d1 >= d2) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        out= output.toArray(new Long[output.size()]);
        return out;
    }
    @Override
    Long[] less(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Long[] out;
        ArrayList<Long> output=new ArrayList<>();
        Long d1, d2;
        for (Long anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Long anIn2 : in2) {
                d2 = anIn2;
                if (d1 < d2) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        out= output.toArray(new Long[output.size()]);
        return out;
    }
    @Override
    Long[] lessOrEqual(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Long[] out;
        ArrayList<Long> output=new ArrayList<>();
        Long d1, d2;
        for (Long anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Long anIn2 : in2) {
                d2 = anIn2;
                if (d1 <= d2) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }

        out= output.toArray(new Long[output.size()]);
        return out;
    }
    @Override
    Long[] equal(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Long[] out;
        ArrayList<Long> output=new ArrayList<>();
        Long d1, d2;
        for (Long anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Long anIn2 : in2) {
                d2 = anIn2;
                if (d1.longValue() == d2.longValue()) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        out= output.toArray(new Long[output.size()]);
        return out;
    }
    @Override
    Long[] notEqual(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Long[] out;
        ArrayList<Long> output=new ArrayList<>();
        Long d1, d2;
        for (Long anIn1 : in1) {
            d1 = anIn1;
            boolean flag=true;
            for (Long anIn2 : in2) {
                d2 = anIn2;
                if (d1.longValue() == d2.longValue()) {
                    flag=false;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        out= output.toArray(new Long[output.size()]);
        return out;
    }
    @Override
    Long[] add(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Long[] output=new Long[Math.max(in1.length, in2.length)];
        for(int s=0;s<Math.max(in1.length,in2.length);s++) {
            if(s>=in1.length) output[s]=in1[in1.length-1];
            else output[s]=in1[s];
            if(s>=in2.length) output[s]+=in2[in1.length-1];
            else output[s]+=in2[s];
        }
        return output;
    }
    @Override
    Long[] subtract(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Long[] output=new Long[Math.max(in1.length, in2.length)];
        for(int s=0;s<Math.max(in1.length,in2.length);s++) {
            if(s>=in1.length) output[s]=in1[in1.length-1];
            else output[s]=in1[s];
            if(s>=in2.length) output[s]-=in2[in1.length-1];
            else output[s]-=in2[s];
        }
        return output;
    }
    @Override
    Long[] multiply(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Long[] output=new Long[Math.max(in1.length, in2.length)];
        for(int s=0;s<Math.max(in1.length,in2.length);s++) {
            if(s>=in1.length) output[s]=in1[in1.length-1];
            else output[s]=in1[s];
            if(s>=in2.length) output[s]*=in2[in1.length-1];
            else output[s]*=in2[s];
        }
        return output;
    }
    @Override
    Double[] divide(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Double[] output=new Double[Math.max(in1.length, in2.length)];
        for(int s=0;s<Math.max(in1.length,in2.length);s++) {
            if(s>=in1.length) output[s]= Double.valueOf(in1[in1.length-1]);
            else output[s]= Double.valueOf(in1[s]);
            if(s>=in2.length) output[s]/=in2[in1.length-1];
            else output[s]/=in2[s];
        }
        return output;
    }
    @Override
    Long[] modulus(Object[] inn1, Object[] inn2){
        Long[] in1=prepare(inn1);
        Long[] in2=prepare(inn2);
        Long[] output=new Long[Math.max(in1.length, in2.length)];
        for(int s=0;s<Math.max(in1.length,in2.length);s++) {
            if(s>=in1.length) output[s]=in1[in1.length-1];
            else output[s]=in1[s];
            if(s>=in2.length) output[s]%=in2[in1.length-1];
            else output[s]%=in2[s];
        }
        return output;
    }
    @Override
    Double[] average(Object[] inn1){
        Long[] in1=prepare(inn1);
        double sum=0;
        for (Long anIn : in1) sum += anIn;
        return new Double[]{sum};
    }

    @Override
    Double[] percentage(Object[] inn1, Object[] inn2) {
        Double[] out=divide(inn1, inn2);
        for(int i=0;i<out.length;i++){
            out[i]*=100;
        }
        return out;
    }
    @Override
    Long[] max(Object[] inn) {
        Long[] in=prepare(inn);
        Long[] max = new Long[1];
        max[0] = Long.MIN_VALUE;
        boolean isFirst = true;
        for (Long anIn : in) {
            if (isFirst) {
                max[0] = anIn;
                isFirst = false;
            } else if (anIn > max[0]) max[0] = anIn;
        }
        return max;
    }

    @Override
    Long[] min(Object[] inn) {
        Long[] in=prepare(inn);
        Long[] min = new Long[1];
        min[0] = Long.MAX_VALUE;
        boolean isFirst = true;
        for (Long anIn : in) {
            if (isFirst) {
                min[0] = anIn;
                isFirst = false;
            } else if (anIn < min[0]) min[0] = anIn;
        }
        return min;
    }
    @Override
    Long[] dayOfWeek(Object[] inn1){
        Long[] in1 = prepare(inn1);
        Long[] out = new Long[in1.length];
        for(int i=0;i<in1.length;i++){
            long value= in1[i];
            out[i] = (long) DateTime.getDayOfWeek(value);
        }
        return out;
    }

    private Long[] prepare(Object[] in){
        if(in instanceof String[]) return convertStringToLong((String[]) in);
        if(in instanceof Double[]) return convertDoubleToLong((Double[]) in);
        else return (Long[]) in;
    }

    private Long[] convertStringToLong(String[] stringData) {
        Long[] longData = new Long[stringData.length];
        for (int i = 0; i < stringData.length; i++)
            longData[i] = Long.parseLong(stringData[i]);
        return longData;
    }
    private Long[] convertDoubleToLong(Double[] doubleData) {
        Long[] longData = new Long[doubleData.length];
        for (int i = 0; i < doubleData.length; i++) {
            longData[i] = (long)((double)doubleData[i]);
        }
        return longData;
    }

}
