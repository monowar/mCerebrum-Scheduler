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

class ProcessDouble extends ProcessData{

    @Override
    Double[] greater(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        ArrayList<Double> output=new ArrayList<>();
        Double d1, d2;
        for (Double anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Double anIn2 : in2) {
                d2 = anIn2;
                if (d1 > d2) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        return output.toArray(new Double[output.size()]);
    }
    @Override
    Double[] greaterOrEqual(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] out;
        ArrayList<Double> output=new ArrayList<>();
        Double d1, d2;
        for (Double anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Double anIn2 : in2) {
                d2 = anIn2;
                if (d1 >= d2) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        out= output.toArray(new Double[output.size()]);
        return out;
    }
    @Override
    Double[] less(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] out;
        ArrayList<Double> output=new ArrayList<>();
        Double d1, d2;
        for (Double anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Double anIn2 : in2) {
                d2 = anIn2;
                if (d1 < d2) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        out= output.toArray(new Double[output.size()]);
        return out;
    }
    @Override
    Double[] lessOrEqual(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] out;
        ArrayList<Double> output=new ArrayList<>();
        Double d1, d2;
        for (Double anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Double anIn2 : in2) {
                d2 = anIn2;
                if (d1 <= d2) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }

        out= output.toArray(new Double[output.size()]);
        return out;
    }
    @Override
    Double[] equal(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] out;
        ArrayList<Double> output=new ArrayList<>();
        Double d1, d2;
        for (Double anIn1 : in1) {
            d1 = anIn1;
            boolean flag=false;
            for (Double anIn2 : in2) {
                d2 = anIn2;
                if (d1.doubleValue() == d2.doubleValue()) {
                    flag=true;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        out= output.toArray(new Double[output.size()]);
        return out;
    }
    @Override
    Double[] notEqual(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] out;
        ArrayList<Double> output=new ArrayList<>();
        Double d1, d2;
        for (Double anIn1 : in1) {
            d1 = anIn1;
            boolean flag=true;
            for (Double anIn2 : in2) {
                d2 = anIn2;
                if (d1.doubleValue() == d2.doubleValue()) {
                    flag=false;
                    break;
                }
            }
            if(flag)
                output.add(d1);
        }
        out= output.toArray(new Double[output.size()]);
        return out;
    }
    @Override
    Double[] add(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] output=new Double[Math.max(in1.length, in2.length)];
        for(int s=0;s<Math.max(in1.length,in2.length);s++) {
            if(s>=in1.length) output[s]=in1[in1.length-1];
            else output[s]=in1[s];
            if(s>=in2.length) output[s]+=in2[in1.length-1];
            else output[s]+=in2[s];
        }
        return output;
    }
    @Override
    Double[] subtract(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] output=new Double[Math.max(in1.length, in2.length)];
        for(int s=0;s<Math.max(in1.length,in2.length);s++) {
            if(s>=in1.length) output[s]=in1[in1.length-1];
            else output[s]=in1[s];
            if(s>=in2.length) output[s]-=in2[in1.length-1];
            else output[s]-=in2[s];
        }
        return output;
    }
    @Override
    Double[] multiply(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] output=new Double[Math.max(in1.length, in2.length)];
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
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] output=new Double[Math.max(in1.length, in2.length)];
        for(int s=0;s<Math.max(in1.length,in2.length);s++) {
            if(s>=in1.length) output[s]= in1[in1.length-1];
            else output[s]= in1[s];
            if(s>=in2.length) output[s]/=in2[in1.length-1];
            else output[s]/=in2[s];
        }
        return output;
    }
    @Override
    Double[] modulus(Object[] inn1, Object[] inn2){
        Double[] in1=prepare(inn1);
        Double[] in2=prepare(inn2);
        Double[] output=new Double[Math.max(in1.length, in2.length)];
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
        Double[] in1=prepare(inn1);
        double sum=0;
        for (Double anIn : in1) sum += anIn;
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
    Long[] dayOfWeek(Object[] inn1){
        Double[] in1 = prepare(inn1);
        Long[] out = new Long[in1.length];
        for(int i=0;i<in1.length;i++){
            long value=(long) ((double)in1[i]);
            out[i] = (long) DateTime.getDayOfWeek(value);
        }
        return out;
    }
    @Override
    Double[] max(Object[] inn) {
        Double[] in=prepare(inn);
        Double[] max = new Double[1];
        max[0] = Double.MIN_VALUE;
        boolean isFirst = true;
        for (Double anIn : in) {
            if (isFirst) {
                max[0] = anIn;
                isFirst = false;
            } else if (anIn > max[0]) max[0] = anIn;
        }
        return max;
    }

    @Override
    Double[] min(Object[] inn) {
        Double[] in=prepare(inn);
        Double[] min = new Double[1];
        min[0] = Double.MAX_VALUE;
        boolean isFirst = true;
        for (Double anIn : in) {
            if (isFirst) {
                min[0] = anIn;
                isFirst = false;
            } else if (anIn < min[0]) min[0] = anIn;
        }
        return min;
    }

    private Double[] prepare(Object[] in){
        if(in instanceof String[]) return convertStringToDouble((String[]) in);
        if(in instanceof Long[]) return convertLongToDouble((Long[]) in);
        else return (Double[]) in;
    }

    private Double[] convertStringToDouble(String[] stringData) {
        Double[] doubleData = new Double[stringData.length];
        for (int i = 0; i < stringData.length; i++)
            doubleData[i] = Double.parseDouble(stringData[i]);
        return doubleData;
    }
    private Double[] convertLongToDouble(Long[] longData) {
        Double[] doubleData = new Double[longData.length];
        for (int i = 0; i < longData.length; i++)
            doubleData[i] = (double) longData[i];
        return doubleData;
    }

}
