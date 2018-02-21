package org.md2k.scheduler.configuration2object;
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

import android.content.Context;

import org.md2k.scheduler.State;
import org.md2k.scheduler.condition.ConditionManager;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.operation.application.ApplicationOperation;
import org.md2k.scheduler.operation.incentive.IncentiveOperation;
import org.md2k.scheduler.time.Time;

import java.math.BigDecimal;

import rx.Observable;

class Config2Application {
    public static Observable<State> getObservable(Context context, String _type, String _id, DataKitManager dataKitManager, Configuration.CApplicationList[] cApplicationList, ConditionManager conditionManager, String id) throws ConfigurationFileFormatError {
        Configuration.CApplication[] cApplications=get(cApplicationList, id);
        if(cApplications==null) return null;
        Configuration.CApplication cApplication = get(cApplications, conditionManager);
        if(cApplication==null) return null;
        return new ApplicationOperation(cApplication.getPackage_name(), Time.getTime(cApplication.getTimeout()), cApplication.getParameter()).getObservable(context,_type,_id);
    }
    private static Configuration.CApplication get(Configuration.CApplication[] cApplications, ConditionManager conditionManager){
        for (Configuration.CApplication cApplication : cApplications) {
            if(conditionManager.isTrue(cApplication.getCondition()))
                return cApplication;
        }
        return null;
    }
    private static Configuration.CApplication[] get(Configuration.CApplicationList[] cApplicationLists, String id) {
        for (Configuration.CApplicationList cApplicationList : cApplicationLists) {
            if (cApplicationList.getId().equalsIgnoreCase(id))
                return cApplicationList.getApplication();
        }
        return null;
    }

/*
    static AbstractOperation[] get(Configuration.CApplicationList[] cApps, String id) throws ConfigurationFileFormatError {
        try {
            for (Configuration.CApplicationList cApp : cApps) {
                if (cApp.getId().equals(id)) {
                    try {
                        return new ApplicationOperation[]{new ApplicationOperation(cAppgetPackage_name(), Time.getTime(cApp.getTimeout()), cApp.getParameter())};
                    } catch (ConfigurationFileFormatError ignored) {

                    }
                }
            }
        }catch (Exception ignored){}
        throw new ConfigurationFileFormatError();
    }
*/

}
