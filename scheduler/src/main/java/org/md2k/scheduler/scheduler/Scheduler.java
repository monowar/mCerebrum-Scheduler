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


import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.State;
import org.md2k.scheduler.configuration.Configuration;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.listen.ListenData;
import org.md2k.scheduler.what.WhatManager;
import org.md2k.scheduler.when.WhenManager;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

public class Scheduler {
    private String type, id;
    private Subscription subscription;
    private Configuration.CListen cListen;
    private WhenManager whenManager;
    private WhatManager whatManager;

    public Scheduler(String type, String id, Configuration.CListen cListen, WhenManager whenManager, WhatManager whatManager) {
        this.type = type;
        this.id = id;
        this.cListen = cListen;
        this.whenManager = whenManager;
        this.whatManager = whatManager;
        this.subscription = null;
    }

    public void restartIfMatch(String path, ListenData listenData){
        if(listenData==null || isMatch(listenData)){
            String s="null";
            if(listenData!=null)
                s=listenData.toString();
            if(subscription==null || subscription.isUnsubscribed()){
                start(path);
            }else{
                stop(path);
                start(path);
            }
        }
    }
    private boolean isMatch(ListenData listenData){
        switch(listenData.getType()){
            case TIME:
                if(cListen.getTime()==null) return false;
                for(int i=0;i<cListen.getTime().length;i++)
                    if(cListen.getTime()[i].equals(listenData.getTime())) return true;
                break;
            case DATASOURCE:
                if(cListen.getDatasource()==null) return false;
                for(int i=0;i<cListen.getDatasource().length;i++){
                    if(isMatch(cListen.getDatasource()[i], listenData.getDataSource())) return true;
                }

        }
        return false;
    }
    private boolean isMatch(DataSource a, DataSource b){
        if(a.getId()!=null && b.getId()==null) return false;
        if(a.getId()!=null && b.getId()!=null && !a.getId().equals(b.getId())) return false;
        if(a.getType()!=null && b.getType()==null) return false;
        if(a.getType()!=null && b.getType()!=null && !a.getType().equals(b.getType())) return false;
        if(a.getPlatform()!=null && b.getPlatform()==null) return false;
        if(a.getPlatform()!=null && b.getPlatform()!=null){
            if(a.getPlatform().getId()!=null && b.getPlatform().getId()==null) return false;
            if(a.getPlatform().getId()!=null && b.getPlatform().getId()!=null && !a.getPlatform().getId().equals(b.getPlatform().getId())) return false;
            if(a.getPlatform().getType()!=null && b.getPlatform().getType()==null) return false;
            if(a.getPlatform().getType()!=null && b.getPlatform().getType()!=null && !a.getPlatform().getType().equals(b.getPlatform().getType())) return false;
        }
        if(a.getPlatformApp()!=null && b.getPlatformApp()!=null){
            if(a.getPlatformApp().getId()!=null && b.getPlatformApp().getId()==null) return false;
            if(a.getPlatformApp().getId()!=null && b.getPlatformApp().getId()!=null && !a.getPlatformApp().getId().equals(b.getPlatformApp().getId())) return false;
            if(a.getPlatformApp().getType()!=null && b.getPlatformApp().getType()==null) return false;
            if(a.getPlatformApp().getType()!=null && b.getPlatformApp().getType()!=null && !a.getPlatformApp().getType().equals(b.getPlatformApp().getType())) return false;
        }
        if(a.getApplication()!=null && b.getApplication()!=null){
            if(a.getApplication().getId()!=null && b.getApplication().getId()==null) return false;
            if(a.getApplication().getId()!=null && b.getApplication().getId()!=null && !a.getApplication().getId().equals(b.getApplication().getId())) return false;
            if(a.getApplication().getType()!=null && b.getApplication().getType()==null) return false;
            if(a.getApplication().getType()!=null && b.getApplication().getType()!=null && !a.getApplication().getType().equals(b.getApplication().getType())) return false;
        }
        return true;
    }

    public void stop(String path) {
        DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/Scheduler("+type+"-"+id+")","Scheduler stop");
        if(subscription!=null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription=null;
    }
    public void start(String path) {
        DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/Scheduler("+type+"-"+id+")","Scheduler start");
        subscription = whenManager.getObservable(path+"/Scheduler("+type+"-"+id+")").flatMap(state -> {
            try {
                return whatManager.getObservable(path+"/Scheduler("+type+"-"+id+")", DateTime.getDateTime());
            } catch (ConfigurationFileFormatError configurationFileFormatError) {
                return Observable.error(configurationFileFormatError);
            }
        }).subscribe(new Observer<State>() {
            @Override
            public void onCompleted() {
                DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/Scheduler("+type+"-"+id+")","Scheduler completed");
//                stop();
            }

            @Override
            public void onError(Throwable e) {
                DataKitManager.getInstance().insertSystemLog("ERROR",path+"/Scheduler("+type+"-"+id+")","Scheduler error");
            }

            @Override
            public void onNext(State state) {
                DataKitManager.getInstance().insertSystemLog("DEBUG",path+"/"+type+"-"+id,"Scheduler next state="+state.getMessage());
            }
        });
    }
}
