package org.md2k.scheduler.operation.notification;
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

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;


import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.MyApplication;
import org.md2k.scheduler.State;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.operation.AbstractOperation;
import org.md2k.scheduler.time.Time;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PhoneTone extends AbstractOperation {
    private String format;
    private long repeat;
    private long interval;
    private Long[] at;
    private MediaPlayer mPlayer;
    private String base;

    public PhoneTone(String format, long repeat, long interval, Long[] at, String base) {
        this.format = format;
        this.repeat = repeat;
        this.interval = interval;
        this.at = at;
        this.base = base;
        mPlayer = new MediaPlayer();
    }


    public Observable<State> getObservable(String path, String _type, String _id) {
        return Observable.from(at)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long delay) {
                        if (base != null) {
                            long dl;
                            long trigTime = delay + Time.getToday() + Time.getTime(base);
                            long curTime = DateTime.getDateTime();
                            if (trigTime > curTime) dl = trigTime-curTime;
                            else if (trigTime + 5000 > curTime) dl = 0;
                            else dl = -1L;
                            return dl;

                        } else {
                            if (delay <= 0) delay = 0L;
                            DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/phonetone", "at: "+DateTime.convertTimeStampToDateTime(DateTime.getDateTime()+delay));
                            return delay;
                        }
                    }
                }).filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long value) {
                        if(value<0) return false;
                        else return true;
                    }
                }).flatMap(delay -> Observable.interval(delay, interval, TimeUnit.MILLISECONDS)
                        .takeWhile(aLong -> aLong < repeat)).map(integer -> {
                    play(path+"/phonetone");
                    return new State(State.STATE.PROCESS, "Phone tone...");
                }).doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        stop(path+"/phonetone");
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        stop(path+"/phonetone");
                    }
                });
    }

    private void stop(String path) {
        try {
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.reset();
                DataKitManager.getInstance().insertSystemLog("DEBUG",path, "tone stop()");
                mPlayer.release();
            }
            mPlayer = null;
        } catch (Exception e) {
            DataKitManager.getInstance().insertSystemLog("ERROR",path, "tone stop() exception e="+e.getMessage());
        }
    }

    private void load(String path, String filename) {
        mPlayer = new MediaPlayer();
        try {
            Uri myUri = Uri.parse(filename);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(MyApplication.getContext(), myUri);
            mPlayer.prepare();
        } catch (Exception e1) {
            try {
                AssetFileDescriptor afd = MyApplication.getContext().getAssets().openFd("tone.mp3");
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mPlayer.prepare();
            } catch (Exception e) {
                DataKitManager.getInstance().insertSystemLog("ERROR",path, "tone load() exception e="+e.getMessage());
            }
        }
    }

    private void play(String path) {
        try {
            stop(path);
            load(path, format);
            DataKitManager.getInstance().insertSystemLog("DEBUG",path, "tone...");
            mPlayer.start();
        } catch (Exception e) {
            DataKitManager.getInstance().insertSystemLog("ERROR",path, "tone start() exception e="+e.getMessage());
        }
    }
}
