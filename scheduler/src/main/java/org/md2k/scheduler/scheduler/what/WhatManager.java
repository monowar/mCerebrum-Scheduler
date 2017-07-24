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

import java.util.Random;

import rx.Observable;

public class WhatManager {
    public Observable<Boolean> getObservable(Context context, String path, Logger logger, DataKitManager dataKitManager, Conditions conditions, What[][] what, Actions actions, Tasks tasks) throws ConfigurationFileFormatError, DataKitAccessError {
        What w = getWhatOrdered(path + "/what/ordered", logger, dataKitManager, conditions, getWhatRandom(path + "/what/random", logger, what));
        if (w == null) {
            logger.write(path + "/what", "Nothing is selected");
            return Observable.just(false);
        } else
            return w.getObservable(context, path, logger, dataKitManager, conditions, actions, tasks);
    }

    private What getWhatOrdered(String path, Logger logger, DataKitManager dataKitManager, Conditions conditions, What[] whats) throws ConfigurationFileFormatError, DataKitAccessError {
        if (whats == null || whats.length == 0) {
            logger.write(path, "Nothing is selected");
            return null;
        }
        for (int i = 0; i < whats.length; i++)
            if (whats[i].isValid(path, logger, dataKitManager, conditions)) {
                if(whats.length!=1)
                    logger.write(path, "selected=" + String.valueOf(i));
                return whats[i];
            }
        return null;
    }

    private What[] getWhatRandom(String path, Logger logger, What[][] whats) {
        if (whats == null || whats.length == 0) {
            logger.write(path, "Nothing is selected");
            return null;
        } else if (whats.length == 1) {
            return whats[0];
        } else {
            Random random = new Random();
            int rand = random.nextInt(whats.length);
            logger.write(path, "randomly selected=" + String.valueOf(rand));
            return whats[rand];
        }
    }
}
