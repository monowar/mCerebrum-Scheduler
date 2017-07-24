package org.md2k.scheduler.configuration;
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
import org.md2k.scheduler.action.Action;
import org.md2k.scheduler.condition.Condition;
import org.md2k.scheduler.scheduler.Scheduler;
import org.md2k.scheduler.scheduler.listen.Listen;
import org.md2k.scheduler.task.Task;


public class Configuration {
    private String id;
    private String type;
    private String title;
    private String summary;
    private String description;
    private DataSource[] create_datasource;
    private Listen listen;

    private Scheduler[] schedulers;
    private Action[] actions;
    private Task[] tasks;
    private Condition[] conditions;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }
    public DataSource[] getCreate_datasource() {
        return create_datasource;
    }
    public Listen getListen() {
        return listen;
    }


    public Scheduler[] getSchedulers() {
        return schedulers;
    }
    public Action[] getActions() {
        return actions;
    }

    public Condition[] getConditions() {
        return conditions;
    }

    public Task[] getTasks() {
        return tasks;
    }
}
