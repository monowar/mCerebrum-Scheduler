package org.md2k.scheduler;
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

import static org.junit.Assert.assertTrue;

public class ConfigFileGenerator {
    @Test
    public void generateContact(){
     print();
        assertTrue(true);
    }
    private void print(){
        for(int i=2;i<=5;i++) {
            for(int j=1;j<=i;j++){
                for(int k=1;k<=2;k++) {
                    for(int l=1;l<=2;l++){
                        String c="contact"+String.valueOf(i)+"."+String.valueOf(j)+"."+String.valueOf(k)+"."+String.valueOf(l);
                        System.out.println("{");
                        System.out.println("\"id\": \""+c+"\",");
                        System.out.println("\"type\": \"ema\",");
                        System.out.println("\"applications\": [");
                        System.out.println("{");
                        System.out.println("\"id\": \"org.md2k.ema\",");
                        System.out.println("\"type\": \"EMA\",");
                        System.out.println("\"parameters\": [");
                        System.out.println("\""+c+".json\"");
                        System.out.println("],");
                        System.out.println("\"timeout\": \"00:30:00\"");
                        System.out.println("}]},");
                        continue;
                    }
                }
            }
        }
    }

}
