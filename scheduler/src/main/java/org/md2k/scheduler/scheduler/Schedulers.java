package org.md2k.scheduler.scheduler;
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
import org.md2k.scheduler.datakit.Data;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.md2k.scheduler.task.Tasks;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;

public class Schedulers {
    private Scheduler[] schedulers;

    public Schedulers(Scheduler[] schedulers) {
        this.schedulers = schedulers;
    }
    public Observable<Boolean> getObservableSchedule(Context context, String path, Logger logger, Data data, AtomicBoolean isRunning, DataKitManager dataKitManager, Conditions conditions, Tasks tasks, Actions actions) throws ConfigurationFileFormatError, DataKitAccessError {
        ArrayList<Observable<Boolean>> observables=new ArrayList<>();
        for (Scheduler scheduler : schedulers)
            observables.add(scheduler.execute(context, path, logger, isRunning, dataKitManager, conditions, tasks, actions));
        return Observable.merge(observables);
    }
}
