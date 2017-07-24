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

import java.util.HashMap;

public class Process {
    private String input1;
    private String input2;
    private String output;
    private String operator;

    public Process(String input1, String input2, String output, String operator) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.operator = operator;
    }

    public void execute(HashMap<String, Object[]> hashMap) {
        Object[] obj1=null, obj2=null, obj3;
        if(getInput1()!=null && hashMap.containsKey(getInput1())) obj1=hashMap.get(getInput1());
        if(getInput2()!=null && hashMap.containsKey(getInput2())) obj2=hashMap.get(getInput2());
        obj3=binary(obj1, obj2, getOperator());
        if (obj3 != null && getOutput() != null)
            hashMap.put(getOutput(), obj3);
    }

    private Object[] binary(Object[] in1, Object[] in2, String operation) {
        if (in1 instanceof Long[]) {
            if(in2==null) return new ProcessLong().execute(in1, null, operation);
            else if (in2 instanceof Long[])
                return new ProcessLong().execute(in1, in2, operation);
            else if (in2 instanceof Double[])
                return new ProcessDouble().execute(in1, in2, operation);
            else if (in2 instanceof String[])
                return new ProcessLong().execute(in1, in2, operation);
        } else if (in1 instanceof Double[]) {
            if(in2==null) return new ProcessDouble().execute(in1, null, operation);
            else if (in2 instanceof Long[])
                return new ProcessDouble().execute(in1, in2, operation);
            else if (in2 instanceof Double[]) {
                return new ProcessDouble().execute(in1, in2, operation);
            }
            else if (in2 instanceof String[])
                return new ProcessDouble().execute(in1, in2, operation);
        } else if (in1 instanceof String[]) {
            if(in2==null) return new ProcessString().execute(in1, null, operation);
            if (in2 instanceof Long[])
                return new ProcessLong().execute(in1, in2, operation);
            else if (in2 instanceof Double[])
                return new ProcessDouble().execute(in1, in2, operation);
            else if (in2 instanceof String[])
                return new ProcessString().execute(in1, in2, operation);
        }
        return null;
    }

    private String getInput1() {
        if(input1!=null) return input1.trim().toUpperCase();
        return null;
    }

    private String getInput2() {
        if(input2!=null) return input2.trim().toUpperCase();
        return null;
    }

    private String getOutput() {
        if(output!=null) return output.trim().toUpperCase();
        return null;
    }

    private String getOperator() {
        if(operator!=null) return operator.trim().toUpperCase();
        return null;
    }
}
