package org.md2k.scheduler.task.notification.notify;

import android.content.Context;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.Logger;
import org.md2k.scheduler.task.notification.Button;
import org.md2k.scheduler.task.notification.Message;
import org.md2k.scheduler.task.notification.Notification;
import org.md2k.utilities.dialog.Dialog;
import org.md2k.utilities.dialog.DialogBuilder;
import org.md2k.utilities.dialog.DialogCallback;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
public class PhoneMessage {
    private static final String TAG = PhoneMessage.class.getSimpleName();
    private static final String DIALOG = "DIALOG";
    private static final String SINGLE_CHOICE = "SINGLE_CHOICE";
    private Dialog dialog = null, dialogCancel = null;

    Observable<String> getObservable(Context context, String path, Logger logger, Notification notification) {
        path += "/PhoneMessage";
        long interval = DateTime.getTimeInMillis(notification.getInterval());
        long startTime = DateTime.getDateTime();

        String finalPath = path;
        return Observable.from(notification.getWhen())
                .map(new Func1<String, Long>() {
                    @Override
                    public Long call(String s) {
                        long delayOffset = DateTime.getTimeInMillis(s);
                        return (startTime + delayOffset) - DateTime.getDateTime();
                    }
                }).flatMap(new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long delay) {
                        if (delay <= 0L) delay = 1L;
                        return Observable.timer(delay, TimeUnit.MILLISECONDS);
                    }
                })
                .flatMap(new Func1<Long, Observable<String>>() {
                    @Override
                    public Observable<String> call(Long bool) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                switch (notification.getFormat().trim().toUpperCase()) {
                                    case DIALOG:
                                        logger.write(finalPath, "message shown");
                                        showDialog(context, notification.getMessage(), subscriber);
                                        break;
                                    case SINGLE_CHOICE:
                                        break;
                                }
                            }
                        }).timeout(interval, TimeUnit.MILLISECONDS)
                                .onErrorReturn(new Func1<Throwable, String>() {
                                    @Override
                                    public String call(Throwable throwable) {
                                        if (dialog != null) {
                                            dialog.stop();
                                            dialog = null;
                                        }
                                        if (dialogCancel != null) {
                                            dialogCancel.stop();
                                            dialogCancel = null;
                                        }
                                        if (throwable instanceof TimeoutException)
                                            return "timeout";
                                        else return null;
                                    }
                                }).filter(new Func1<String, Boolean>() {
                                    @Override
                                    public Boolean call(String s) {
                                        if (notification.getSkip() == null) return true;
                                        if (s == null)
                                            return true;
                                        for (String aSkip : notification.getSkip())
                                            if (aSkip.trim().toUpperCase().equals(s.trim().toUpperCase()))
                                                return false;
                                        return true;
                                    }
                                });
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s;
                    }
                })
                .timeout(interval, TimeUnit.MILLISECONDS)
                .onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        if (dialog != null) {
                            dialog.stop();
                            dialog = null;
                        }
                        if (dialogCancel != null) {
                            dialogCancel.stop();
                            dialogCancel = null;
                        }
                        if (throwable instanceof TimeoutException)
                            return "timeout";
                        else return null;
                    }
                }).filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if (notification.getSkip() == null) return true;
                        if (s == null)
                            return true;
                        for (String aSkip : notification.getSkip())
                            if (aSkip.trim().toUpperCase().equals(s.trim().toUpperCase()))
                                return false;
                        return true;
                    }
                }).doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        logger.write(finalPath, "message_response="+s);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        try {
                            if (dialog != null) {
                                dialog.stop();
                                dialog = null;
                            }
                            if (dialogCancel != null) {
                                dialogCancel.stop();
                                dialogCancel = null;
                            }
                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void showDialog(Context context, Message message, Subscriber<? super String> subscriber) {
        String[] buttonText = new String[message.getButtons().length];
        for (int i = 0; i < message.getButtons().length; i++)
            buttonText[i] = message.getButtons()[i].getTitle();
        dialog = new DialogBuilder(context, Dialog.TYPE.QUESTION).setTitle(message.getTitle()).setContent(message.getMessage()).setButton(buttonText).build();
        dialog.start(new DialogCallback() {
            @Override
            public void onDialogCallback(Dialog.DialogResponse which, String[] result) {
                if (which == Dialog.DialogResponse.POSITIVE) {
                    if (message.getButtons()[0].isConfirm()) {
                        PhoneMessage.this.showAlertDialogCancel(context, message.getButtons()[0], subscriber, message);
                    } else {
                        subscriber.onNext(message.getButtons()[0].getId());
                        subscriber.onCompleted();
                    }

                } else if (which == Dialog.DialogResponse.NEGATIVE) {
                    if (message.getButtons()[1].isConfirm()) {
                        PhoneMessage.this.showAlertDialogCancel(context, message.getButtons()[1], subscriber, message);
                    } else {
                        subscriber.onNext(message.getButtons()[1].getId());
                        subscriber.onCompleted();
                    }
                } else if (which == Dialog.DialogResponse.NEUTRAL) {
                    if (message.getButtons()[2].isConfirm()) {
                        PhoneMessage.this.showAlertDialogCancel(context, message.getButtons()[2], subscriber, message);
                    } else {
                        subscriber.onNext(message.getButtons()[2].getId());
                        subscriber.onCompleted();
                    }
                }
            }
        });
    }

    private void showAlertDialogCancel(Context context, Button button, Subscriber<? super String> subscriber, Message message) {
        String[] buttonText = new String[]{"Yes", "No"};
        dialogCancel = new DialogBuilder(context, Dialog.TYPE.QUESTION).setTitle("Confirm").setContent("You selected \"" + button.getTitle() + "\". Is that right?").setButton(buttonText).build();
        dialogCancel.start(new DialogCallback() {
            @Override
            public void onDialogCallback(Dialog.DialogResponse which, String[] result) {
                if (which == Dialog.DialogResponse.POSITIVE) {
                    subscriber.onNext(button.getId());
                    subscriber.onCompleted();
                } else PhoneMessage.this.showDialog(context, message, subscriber);

            }
        });
    }
}