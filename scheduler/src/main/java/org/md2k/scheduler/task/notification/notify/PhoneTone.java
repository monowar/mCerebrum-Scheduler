package org.md2k.scheduler.task.notification.notify;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.Constants;
import org.md2k.scheduler.Logger;
import org.md2k.scheduler.task.notification.Notification;
import org.md2k.utilities.FileManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
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
class PhoneTone {
    private MediaPlayer mPlayer;

    Observable<String> getObservable(Context context, String path, Logger logger, Notification notification) {
        long interval = DateTime.getTimeInMillis(notification.getInterval());
        int repeat = notification.getRepeat();
        long startTime = DateTime.getDateTime();
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
                        if(delay<=0L) delay=1L;
                        return Observable.interval(delay,interval, TimeUnit.MILLISECONDS).takeWhile(new Func1<Long, Boolean>() {
                            @Override
                            public Boolean call(Long aLong) {
                                if(aLong<repeat) return true;
                                else return false;
                            }
                        });
                    }
                }).map(new Func1<Long, String>() {
                    @Override
                    public String call(Long integer) {
                        logger.write(path, "tone playing...");
                        PhoneTone.this.play(context, notification.getFormat());
                        return "";
                    }
                }).onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        PhoneTone.this.stop();
                        return null;
                    }
                }).filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return false;
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        PhoneTone.this.stop();
                    }
                });
    }
    private void stop() {
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
            }
            mPlayer = null;
        } catch (Exception ignored) {

        }
    }

    private void play(Context context, String filename) {
        try {
            mPlayer = new MediaPlayer();
            if (filename != null && FileManager.isExist(Constants.CONFIG_DIRECTORY + filename)) {
                Uri myUri = Uri.parse(Constants.CONFIG_DIRECTORY + filename);
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(context, myUri);
            } else {
                AssetFileDescriptor afd = context.getAssets().openFd("tone.mp3");
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            }
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
