package org.md2k.scheduler.operation.incentive;
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.md2k.scheduler.Constants;
import org.md2k.scheduler.State;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.operation.AbstractOperation;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

public class IncentiveOperation extends AbstractOperation {
    private static final String TEXT_AMOUNT="<AMOUNT>";
    private static final String TEXT_TOTAL_AMOUNT="<TOTAL_AMOUNT>";

    private double amount;
    private String[] message;
    private long timeout;
    private DataKitManager dataKitManager;
    private Subscriber<? super State> subscriber;

    public IncentiveOperation(DataKitManager dataKitManager, double amount, String[] message, long timeout) {
        this.amount = amount;
        this.message = message;
        this.timeout = timeout;
        this.dataKitManager = dataKitManager;
    }
    private void prepareMessage(double totalAmount){
        try {
            String amountStr = String.format(Locale.getDefault(), "%.2f", amount);
            String amountTotalStr = String.format(Locale.getDefault(), "%.2f", totalAmount);
            for (int i = 0; i < message.length; i++) {
                message[i] = message[i].replace(TEXT_AMOUNT, amountStr);
                message[i] = message[i].replace(TEXT_TOTAL_AMOUNT, amountTotalStr);
            }
        }catch (Exception e){
            Log.e("IncentiveOperation","prepareMessage() - error: "+e.toString());
        }
    }

    @Override
    public Observable<State> getObservable(Context context, String _type, String _id) {
        return Observable.just(true).map(aBoolean -> {
            dataKitManager.insertIncentive(amount);
            double totalAmount = dataKitManager.queryTotalIncentive();
            prepareMessage(totalAmount);
            return true;
        }).flatMap(aBoolean -> Observable.create((Observable.OnSubscribe<State>) subscriber -> {
            IncentiveOperation.this.subscriber = subscriber;
            LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                    new IntentFilter(Constants.INTENT_COMMUNICATION));
            show(context);
        }).timeout(timeout+2000, TimeUnit.MILLISECONDS).doOnUnsubscribe(() -> LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver)));
    }

    private void show(Context context){
        Intent intent = new Intent(context, ActivityIncentive.class);
        intent.putExtra("message", message);
        intent.putExtra("timeout", timeout);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(Constants.INTENT_COMMUNICATION_STATUS);
            subscriber.onNext(new State(State.STATE.OUTPUT, message));
            subscriber.onCompleted();
        }
    };
}
