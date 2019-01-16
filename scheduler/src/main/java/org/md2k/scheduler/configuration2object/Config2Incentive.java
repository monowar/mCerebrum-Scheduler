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



import org.md2k.scheduler.State;
import org.md2k.scheduler.condition.ConditionManager;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.operation.incentive.IncentiveOperation;

import java.util.ArrayList;

import rx.Observable;

class Config2Incentive {
    public static Observable<State> getObservable(String path, String _type, String _id, Configuration.CIncentiveList[] incentive_list, String id) {
        Configuration.CIncentive[] cIncentives=get(incentive_list, id);
        if(cIncentives==null) return null;
        Configuration.CIncentive cIncentive = get(path, cIncentives);
        if(cIncentive==null) return null;
        return new IncentiveOperation(cIncentive.getAmount(), cIncentive.getMessage(), 60000).getObservable(path, _type, _id);
    }
    private static Configuration.CIncentive get(String path, Configuration.CIncentive[] cIncentives){
        for (Configuration.CIncentive cIncentive : cIncentives) {
            ArrayList<String> details=new ArrayList<>();
            boolean condition= ConditionManager.getInstance().isTrue(cIncentive.getCondition(), details);
            String s = "";
            for(int i=0;i<details.size();i+=3) {
                if(i+2>=details.size()) continue;
                s += details.get(i + 1).replace(",", ";") + "=" + details.get(i + 2).replace(",", ";") + ";";
            }
            DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/incetive/condition",String.valueOf(condition)+" ["+s+"]");
            if(condition)
                return cIncentive;
        }
        return null;
    }
    private static Configuration.CIncentive[] get(Configuration.CIncentiveList[] cIncentiveLists, String id) {
        for (Configuration.CIncentiveList cIncentiveList : cIncentiveLists) {
            if (cIncentiveList.getId().equalsIgnoreCase(id))
                return cIncentiveList.getIncentive();
        }
        return null;
    }
}
