
package org.md2k.scheduler.condition;
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
import org.md2k.scheduler.configuration.ConfigurationManager;

public class BlockStressTest {
    @Test
    public void a(){
        String[] f={
                "contact2.1.1.1.json",
                "contact2.1.1.2.json",
                "contact2.1.2.1.json",
                "contact2.1.2.2.json",
                "contact2.2.1.1.json",
                "contact2.2.1.2.json",
                "contact2.2.2.1.json",
                "contact2.2.2.2.json",
                "contact3.1.1.1.json",
                "contact3.1.1.2.json",
                "contact3.1.2.1.json",
                "contact3.1.2.2.json",
                "contact3.2.1.1.json",
                "contact3.2.1.2.json",
                "contact3.2.2.1.json",
                "contact3.2.2.2.json",
                "contact3.3.1.1.json",
                "contact3.3.1.2.json",
                "contact3.3.2.1.json",
                "contact3.3.2.2.json",
                "contact4.1.1.1.json",
                "contact4.1.1.2.json",
                "contact4.1.2.1.json",
                "contact4.1.2.2.json",
                "contact4.2.1.1.json",
                "contact4.2.1.2.json",
                "contact4.2.2.1.json",
                "contact4.2.2.2.json",
                "contact4.3.1.1.json",
                "contact4.3.1.2.json",
                "contact4.3.2.1.json",
                "contact4.3.2.2.json",
                "contact4.4.1.1.json",
                "contact4.4.1.2.json",
                "contact4.4.2.1.json",
                "contact4.4.2.2.json",
                "contact5.1.1.1.json",
                "contact5.1.1.2.json",
                "contact5.1.2.1.json",
                "contact5.1.2.2.json",
                "contact5.2.1.1.json",
                "contact5.2.1.2.json",
                "contact5.2.2.1.json",
                "contact5.2.2.2.json",
                "contact5.3.1.1.json",
                "contact5.3.1.2.json",
                "contact5.3.2.1.json",
                "contact5.3.2.2.json",
                "contact5.4.1.1.json",
                "contact5.4.1.2.json",
                "contact5.4.2.1.json",
                "contact5.4.2.2.json",
                "contact5.5.1.1.json",
                "contact5.5.1.2.json",
                "contact5.5.2.1.json",
                "contact5.5.2.2.json",                
        };
        for(int i=0;i<f.length;i++){
            String s="    {\n" +
                    "      \"id\": \""+f[i].substring(0, f[i].length()-5)+"\",\n" +
                    "      \"application\": [\n" +
                    "        {\n" +
                    "          \"package_name\": \"org.md2k.ema\",\n" +
                    "          \"timeout\": \"00:10:00\",\n" +
                    "          \"parameter\": {\n" +
                    "            \"filename\": \""+f[0]+"\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n";
            System.out.println(s);
        }
    }
}

