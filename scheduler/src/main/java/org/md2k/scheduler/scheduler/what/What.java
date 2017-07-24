package org.md2k.scheduler.scheduler.what;
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

import org.md2k.scheduler.Logger;
import org.md2k.scheduler.action.Actions;
import org.md2k.scheduler.condition.Conditions;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.task.Tasks;

import rx.Observable;

public class What {
    private String condition;
    private Act act;
    public boolean isValid(String id, Logger logger, DataKitManager dataKitManager, Conditions conditions) throws ConfigurationFileFormatError, DataKitAccessError {
        return conditions.isValid(id, logger, dataKitManager, condition);
    }
    public Observable<Boolean> getObservable(Context context, String path, Logger logger, DataKitManager dataKitManager, Conditions conditions, Actions actions, Tasks tasks) throws ConfigurationFileFormatError {
        return actions.getObservable(context, path+"/what", logger, act.getId(), act.getParameters(), dataKitManager, conditions, tasks);
/*
        return _notifications.getObservable(dataKitManager, notifications)
        .flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String notificationResponse) {
                return new PerformManager().getObservable(context, perform, notificationResponse);
            }
        });
*/
    }
}
