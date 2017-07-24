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

import com.udojava.evalex.Expression;

import org.md2k.scheduler.Logger;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;

public class Conditions {
    private Condition[] conditions;
    public Conditions(Condition[] conditions){
        this.conditions=conditions;
    }
    public boolean isValid(String path, Logger logger, DataKitManager dataKitManager, String conditionString) throws ConfigurationFileFormatError, DataKitAccessError {
        path+="/condition";
        if(conditionString ==null || conditionString.length()==0) {
            logger.write(path,"condition(empty),output(TRUE)");
            return true;
        }else if(conditions==null){
            throw new ConfigurationFileFormatError();
        }
        Expression expression=new Expression(conditionString.trim().toUpperCase());
        for (Condition condition : conditions) {
            if (conditionString.trim().toUpperCase().contains(condition.getId())) {
                boolean value = condition.isValid(path, logger,dataKitManager);
                if (value)
                    expression = expression.with(condition.getId(), "1");
                else
                    expression = expression.with(condition.getId(), "0");
            }
        }
        boolean result= expression.eval().intValue() != 0;
        logger.write(path,"condition("+conditionString+"),output("+result+")");
        return result;
    }
}
