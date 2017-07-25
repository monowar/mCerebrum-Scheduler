package org.md2k.scheduler.task;
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
import org.md2k.scheduler.condition.Conditions;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.task.application.Application;
import org.md2k.scheduler.task.application.Applications;
import org.md2k.scheduler.task.incentive.Incentive;
import org.md2k.scheduler.task.incentive.Incentives;
import org.md2k.scheduler.task.notification.Notification;
import org.md2k.scheduler.task.notification.notify.Notifications;
import org.md2k.scheduler.task.save.Save;
import org.md2k.scheduler.task.save.SaveManager;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class Task {
    private String id;
    private String type;
    private Application[] applications;
    private Notification[] notifications;
    private Incentive[] incentives;
    private Save[] save;

    public String getId() {
        return id.trim().toUpperCase();
    }

    public String getType() {
        return type.trim().toUpperCase();
    }

    public Application[] getApplications() {
        return applications;
    }

    public Notification[] getNotifications() {
        return notifications;
    }

    public Observable<Response> getObservable(Context context, String path, Logger logger, DataKitManager dataKitManager, Conditions conditions, Response response) {
        path+="/"+id;
        logger.write(path,"starts");
        ArrayList<Observable<Response>> observables = new ArrayList<>();
        Observable<Response> observableNotification = new Notifications().getObservable(context, path, logger, dataKitManager, conditions, notifications);
        Observable<Response> observableApplication = new Applications().getObservable(context, path, logger, dataKitManager, conditions, applications);
        Observable<Response> observableIncentive = new Incentives().getObservable(context, path, logger, dataKitManager, conditions, incentives);
        Observable<Response> observableSave = new SaveManager().getObservable(context, path, logger, dataKitManager, save, response);
        if (observableNotification != null) observables.add(observableNotification);
        if (observableApplication != null) observables.add(observableApplication);
        if (observableIncentive != null) observables.add(observableIncentive);
        if (observableSave != null) observables.add(observableSave);
        String finalPath = path;
        return (Observable.merge(observables))
                .flatMap(new Func1<Response, Observable<Response>>() {
                    @Override
                    public Observable<Response> call(Response s) {
                        logger.write(finalPath,"response="+s.getInput());
                        return Observable.just(new Response(id,s.getInput(), s.getDataType()));
                    }
                });

/*

        return Observable.create(new Observable.OnSubscribe<String[]>() {
            @Override
            public void call(Subscriber<? super String[]> subscriber) {
                Subscription subscription= Observable.merge(observables).subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(String s) {
                        String[] st=new String[2];
                        st[0]=id;
                        st[1]=s;
                        subscriber.onNext(st);
                        subscriber.onCompleted();
                    }
                });
            }
        });
*/
/*
        return Observable.merge(observables)
                .flatMap(new Func1<String, Observable<String[]>>() {
            @Override
            public Observable<String[]> call(String s) {
                String[] st=new String[2];
                st[0]=id;
                st[1]=s;
                return Observable.just(st);
            }
        });
*/
    }
}
