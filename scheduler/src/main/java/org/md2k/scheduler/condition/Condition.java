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

import org.md2k.scheduler.Logger;
import org.md2k.scheduler.condition.input.Input;
import org.md2k.scheduler.condition.process.Process;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.exception.DataNotFoundError;
import org.md2k.scheduler.exception.DataNotFoundErrorFalse;
import org.md2k.scheduler.exception.DataNotFoundErrorTrue;
import org.md2k.scheduler.exception.DataSourceNotFound;

import java.util.HashMap;

public class Condition {
    private String id;
    private String type;
    private String title;
    private String summary;
    private String description;
    private Input[] input;
    private Process[] process;
    private String output;

    private String getOutput() {
        if(output!=null) return output.trim().toUpperCase();
        return null;
    }


    public Condition(String id, String type, String title, String summary, String description, Input[] input, Process[] process, String output) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.input = input;
        this.process = process;
        this.output = output;
    }


    public boolean isValid(String path, Logger logger, DataKitManager dataKitManager) throws DataKitAccessError, ConfigurationFileFormatError {
        try{
            HashMap<String, Object[]> hashMap = new HashMap<>();
            String msg = executeInput(dataKitManager, hashMap);
            executeProcess(hashMap);
            boolean result= executeOutput(hashMap);
            logger.write(path+"/"+this.id, "input=("+msg+"),"+"output("+result+")");
            return result;
        } catch (DataNotFoundErrorFalse dataNotFoundErrorFalse) {
            return false;
        } catch (DataNotFoundErrorTrue dataNotFoundErrorTrue) {
            return true;
        }
    }
    private String executeInput(DataKitManager dataKitManager, HashMap<String, Object[]> hashMap) throws ConfigurationFileFormatError, DataKitAccessError, DataNotFoundErrorFalse, DataNotFoundErrorTrue {
        String msg="";
        if(input!=null)
            for (Input anInput : input) msg+=anInput.execute(dataKitManager, hashMap);
        return msg;
    }
    private void executeProcess(HashMap<String, Object[]> hashMap) {
        if(process!=null)
            for (Process anProcess : process) anProcess.execute(hashMap);
    }
    private boolean executeOutput(HashMap<String, Object[]> hashMap) throws ConfigurationFileFormatError {
        if(!hashMap.containsKey(getOutput())) throw new ConfigurationFileFormatError();
        Object[] obj=hashMap.get(getOutput());
        return obj.length != 0;
    }

    public String getId() {
        return id.trim().toUpperCase();
    }
}
