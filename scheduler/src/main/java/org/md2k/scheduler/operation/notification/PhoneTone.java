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

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.State;
import org.md2k.scheduler.operation.AbstractOperation;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public class PhoneTone extends AbstractOperation {
    private String format;
    private long repeat;
    private long interval;
    private Long[] at;
    private MediaPlayer mPlayer;

    public PhoneTone(String format, long repeat, long interval, Long[] at) {
        this.format = format;
        this.repeat = repeat;
        this.interval = interval;
        this.at = at;
    }

    public Observable<State> getObservable(Context context, String _type, String _id) {
        Log.d("abc", "phoneTone Observable...interval=" + interval);
        load(context, format);
        return Observable.from(at)
                .map(delay -> {
                    if (delay <= 0) delay = 1L;
                    return delay;
                }).flatMap(delay -> Observable.interval(delay, interval, TimeUnit.MILLISECONDS)
                        .takeWhile(aLong -> aLong < repeat)).map(integer -> {
                    play();
                    return new State(State.STATE.PROCESS, "Phone tone...");
                }).doOnUnsubscribe(this::stop).doOnError(throwable -> stop());
    }

    private void stop() {
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
            }
            mPlayer = null;
        } catch (Exception ignored) {
            Log.e("abc", "PhoneTone..stop()...failed" + "exception=" + ignored.toString());
        }
    }

    private void load(Context context, String filename) {
        try {
            mPlayer = new MediaPlayer();
            Uri myUri = Uri.parse(filename);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(context, myUri);
            mPlayer.prepare();
        } catch (Exception e1) {
            Log.e("abc", "PhoneTone..play()..fileLoad()..failed");
            try {
                AssetFileDescriptor afd = context.getAssets().openFd("tone.mp3");
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mPlayer.prepare();
            } catch (Exception ignored) {
                Log.e("abc", "PhoneTone..play()..assetload()..failed");
            }
        }
    }

    private void play() {
        try {
            Log.d("abc", "phonetone play...");
            mPlayer.start();
        } catch (Exception e) {
            Log.e("abc", "PhoneTone..play()..start()..failed");
        }
    }
}
