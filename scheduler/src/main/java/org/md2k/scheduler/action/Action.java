package org.md2k.scheduler.action;
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
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.scheduler.what.Parameter;
import org.md2k.scheduler.task.Response;
import org.md2k.scheduler.task.Task;
import org.md2k.scheduler.task.Tasks;

import rx.Observable;
import rx.functions.Func1;

public class Action {
    private String id;
    private String start_state;
    private Transition[] transitions;
    private Response curResponse;
    private Throwable error;

    public String getId() {
        return id.trim().toUpperCase();
    }

    public Observable<Boolean> getObservable(Context context, String path, Logger logger, Parameter[] parameters, DataKitManager dataKitManager, Conditions conditions, Tasks tasks) {
        path+="/"+id;
        String finalPath = path;
        curResponse=new Response(start_state, null, null);
        Observable<Boolean> observable= Observable.just(true)
                .map(aBoolean -> curResponse)
                .flatMap(new Func1<Response, Observable<Response>>() {
                    @Override
                    public Observable<Response> call(Response response) {
                        Task task=tasks.get(encode(parameters, response.getCurrentState()));
                        if(task==null) return Observable.error(new ConfigurationFileFormatError());
                        return task.getObservable(context, finalPath, logger, dataKitManager, conditions, response);
                    }
                }).map(response -> new Response(Action.this.decode(parameters, response.getCurrentState()), response.getInput(), response.getDataType()))
                .flatMap(new Func1<Response, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Response response) {
                        curResponse= new Response(Action.this.getNextState(response.getCurrentState(), response.getInput()), null, response.getDataType());
                        if(curResponse.getCurrentState()==null)
                            return Observable.error(new Throwable("completed"));
                        return Observable.just(true);
                    }
                }).repeatWhen(observable1 -> observable1)
                .onErrorReturn(new Func1<Throwable, Boolean>() {
                    @Override
                    public Boolean call(Throwable throwable) {
                        error=throwable;
                        return throwable.getMessage().equals("completed");
                    }
                }).flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        if(aBoolean) return Observable.just(true);
                        return Observable.error(error);
                    }
                });
/*.takeUntil(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean;
                    }
                })*/;
/*                .takeUntil(new Func1<Response, Boolean>() {
                    @Override
                    public Boolean call(Response response) {
                        if (response == null || response.getCurrentState() == null) return false;
                        else curResponse = response;
                        return true;
                    }
                }).map(new Func1<Response, Boolean>() {
                    @Override
                    public Boolean call(Response string) {
                        return true;
                    }
                })*/
        return observable;
    }
    private String encode(Parameter[] parameters, String state){
        if(parameters==null) return state;
        for (Parameter parameter : parameters) {
            if (parameter.getId().equals(state.trim().toUpperCase())) return parameter.getValue();
        }
        return state;
    }
    private String decode(Parameter[] parameters, String state){
        if(parameters==null) return state;

        for (Parameter parameter : parameters) {
            if (parameter.getValue().equals(state.trim().toUpperCase())) return parameter.getId();
        }
        return state;
    }
    private String getNextState(String curState, String input){
        if(transitions==null) return null;
        if(input==null) return null;
        if(curState==null) return null;
        for (Transition transition : transitions) {
            if (transition.isEqual(curState, input))
                return transition.getNext_state();
        }
        return null;
    }
}
