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
import org.md2k.scheduler.operation.AbstractOperation;
import org.md2k.scheduler.operation.notification.PhoneDialog;
import org.md2k.scheduler.operation.notification.PhoneTone;
import org.md2k.scheduler.operation.notification.PhoneVibrate;
import org.md2k.scheduler.time.Time;

import java.util.ArrayList;

import rx.Observable;

class Config2Notification {
    private static final String TYPE_NOTIFICATION_PHONE_VIBRATE = "PHONE_VIBRATION";
    private static final String TYPE_NOTIFICATION_PHONE_TONE = "PHONE_TONE";
    private static final String TYPE_NOTIFICATION_PHONE_SCREEN = "PHONE_SCREEN";
    private static final String TYPE_NOTIFICATION_PHONE_DIALOG = "PHONE_DIALOG";
    private static final String TYPE_NOTIFICATION_PHONE_DIALOG_SINGLE_CHOICE = "PHONE_DIALOG_SINGLE_CHOICE";


    public static Observable<State> getObservable(Context context, String _type, String _id, DataKitManager dataKitManager, Configuration.CNotificationList[] notification_list, Configuration.CNotificationDetails[] notification_details, ConditionManager conditionManager, String id) {
        ArrayList<Observable<State>> observables = new ArrayList<>();
        Configuration.CNotification[] cNotificationList = get(notification_list, id);
        if (cNotificationList == null) return null;
        String[] cNotificationDetailsIds = get(cNotificationList, conditionManager);
        if (cNotificationDetailsIds == null) return null;
        for (String cNotificationDetailsId : cNotificationDetailsIds) {
            AbstractOperation o = get(notification_details, cNotificationDetailsId);
            if (o != null)
                observables.add(o.getObservable(context, _type, _id));
        }
        return Observable.merge(observables);
    }

    private static AbstractOperation get(Configuration.CNotificationDetails[] cNotificationDetails, String id) {
        for (Configuration.CNotificationDetails cNotificationDetail : cNotificationDetails) {
            if (cNotificationDetail.getId().equalsIgnoreCase(id))
                return getOperation(cNotificationDetail);
        }
        return null;
    }

    private static AbstractOperation getOperation(Configuration.CNotificationDetails cNotificationDetails) {
        long interval;
        Long[] at;
        try {
            switch (cNotificationDetails.getType().toUpperCase()) {
                case TYPE_NOTIFICATION_PHONE_VIBRATE:
                    interval = Time.getTime(cNotificationDetails.getInterval());
                    at = getTime(cNotificationDetails.getAt());
                    return new PhoneVibrate(cNotificationDetails.getRepeat(), interval, at);
                case TYPE_NOTIFICATION_PHONE_TONE:
                    interval = Time.getTime(cNotificationDetails.getInterval());
                    at = getTime(cNotificationDetails.getAt());
                    return new PhoneTone(cNotificationDetails.getFormat(), cNotificationDetails.getRepeat(), interval, at);
                case TYPE_NOTIFICATION_PHONE_DIALOG:
                    interval = Time.getTime(cNotificationDetails.getInterval());
                    String[] buttons = getButtons(cNotificationDetails.getMessage().getButtons());
                    boolean[] confirms = getConfirms(cNotificationDetails.getMessage().getButtons());
                    return new PhoneDialog(cNotificationDetails.getMessage().getTitle(), cNotificationDetails.getMessage().getContent(), buttons, confirms, Time.getTime(cNotificationDetails.getAt()[0]), interval);

            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static String[] getButtons(Configuration.CNotificationDetails.CNotiPhButton[] buttons) {
        String[] title = new String[buttons.length];
        for (int i = 0; i < buttons.length; i++)
            title[i] = buttons[i].getTitle();
        return title;
    }

    private static boolean[] getConfirms(Configuration.CNotificationDetails.CNotiPhButton[] buttons) {
        boolean[] confirms = new boolean[buttons.length];
        for (int i = 0; i < buttons.length; i++)
            confirms[i] = buttons[i].isConfirm();
        return confirms;
    }

    private static Long[] getTime(String[] timeStr) throws ConfigurationFileFormatError {
        try {
            Long[] at = new Long[timeStr.length];
            for (int k = 0; k < timeStr.length; k++)
                at[k] = Time.getTime(timeStr[k]);
            return at;
        } catch (Exception e) {
            throw new ConfigurationFileFormatError();
        }
    }

    private static Configuration.CNotification[] get(Configuration.CNotificationList[] cNotificationLists, String id) {
        for (Configuration.CNotificationList cNotificationList : cNotificationLists) {
            if (cNotificationList.getId().equalsIgnoreCase(id))
                return cNotificationList.getNotification();
        }
        return null;
    }

    private static String[] get(Configuration.CNotification[] cNotifications, ConditionManager conditionManager) {
        for (Configuration.CNotification cNotification : cNotifications) {
            if(conditionManager.isTrue(cNotification.getCondition()))
                return cNotification.getNotification_details_id();
        }
        return null;
    }
}
