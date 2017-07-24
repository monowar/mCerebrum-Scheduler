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

abstract class ProcessData {
    private static final String MAX="MAX";
    private static final String MIN="MIN";
    private static final String AVERAGE="AVERAGE";
    private static final String COUNT="COUNT";
    private static final String PERCENTAGE="PERCENTAGE";
    private static final String DAY_OF_WEEK="DAY_OF_WEEK";

    private static final String PLUS="+";
    private static final String MINUS="-";
    private static final String MULTIPLY="*";
    private static final String DIVIDE="/";
    private static final String MODULUS="%";

    private static final String GREATER =">";
    private static final String GREATER_OR_EQUAL =">=";
    private static final String LESS ="<";
    private static final String LESS_OR_EQUAL ="<=";
    private static final String EQUAL ="==";
    private static final String NOT_EQUAL ="!=";

    private static final String AND="&&";
    private static final String OR="||";

    Object[] max(Object[] in1){return null;}
    Object[] min(Object[] in1){return null;}
    Object[] average(Object[] in1){return null;}
    private Object[] count(Object[] in){
        Long[] count=new Long[1];
        count[0]= (long) in.length;
        return count;
    }
    Object[] percentage(Object[] in1, Object[] in2){return null;}
    Object[] dayOfWeek(Object[] in1){return null;}
    Object[] add(Object[] in1, Object[] in2){return null;}
    Object[] subtract(Object[] in1, Object[] in2){return null;}
    Object[] multiply(Object[] in1, Object[] in2){return null;}
    Object[] divide(Object[] in1, Object[] in2){return null;}
    Object[] modulus(Object[] in1, Object[] in2){return null;}
    Object[] greater(Object[] in1, Object[] in2){return null;}
    Object[] greaterOrEqual(Object[] in1, Object[] in2){return null;}
    Object[] less(Object[] in1, Object[] in2){return null;}
    Object[] lessOrEqual(Object[] in1, Object[] in2){return null;}
    Object[] equal(Object[] in1, Object[] in2){return null;}
    Object[] notEqual(Object[] in1, Object[] in2){return null;}
    private Object[] and(Object[] in1, Object[] in2){
        if(in1.length !=0 && in2.length!=0){
            Long[] count=new Long[1];
            count[0]= (long) in1.length;
            return count;
        }
        else return new Long[0];
    }
    private Object[] or(Object[] in1, Object[] in2){
        if(in1.length !=0 || in2.length!=0){
            Long[] count=new Long[1];
            count[0]= (long) in1.length;
            return count;
        }
        else return new Long[0];
    }


    Object[] execute(Object[] in1, Object[] in2, String operation) {
        try {
            switch (operation.trim().toUpperCase()) {
                case MAX:
                    return max(in1);
                case MIN:
                    return min(in1);
                case COUNT:
                    return count(in1);
                case AVERAGE:
                    return average(in1);
                case PERCENTAGE:
                    return percentage(in1, in2);
                case DAY_OF_WEEK:
                    return dayOfWeek(in1);
                case PLUS:
                    return add(in1, in2);
                case MINUS:
                    return subtract(in1, in2);
                case MULTIPLY:
                    return multiply(in1, in2);
                case DIVIDE:
                    return divide(in1, in2);
                case MODULUS:
                    return modulus(in1,in2);
                case GREATER:
                    return greater(in1, in2);
                case GREATER_OR_EQUAL:
                    return greaterOrEqual(in1, in2);
                case LESS:
                    return less(in1, in2);
                case LESS_OR_EQUAL:
                    return lessOrEqual(in1, in2);
                case EQUAL:
                    return equal(in1, in2);
                case NOT_EQUAL:
                    return notEqual(in1, in2);
                case AND:
                    return and(in1, in2);
                case OR:
                    return or(in1, in2);
                default:
                    return null;
            }
        }catch (Exception e){
            return null;
        }
    }

}
