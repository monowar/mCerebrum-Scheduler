package org.md2k.scheduler._test;
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

import org.md2k.scheduler.condition.ConditionManager;

import java.math.BigDecimal;

public class ConditionTest {
    public static void test(ConditionManager conditionManager){
        BigDecimal b;
        boolean res;
        b=conditionManager.evaluate("is_active_day() == true && ((get_sample_no(get_last_sample(0,DAY,START), get_last_sample(0,DAY,START)+time_offset(04:00:00), EMI, SMOKING)==0 && now()>=get_last_sample(0,DAY,START) && now()<get_last_sample(0,DAY,START)+time_offset(04:00:00)) || (get_sample_no(get_last_sample(0,DAY,START)+time_offset(04:00:00), get_last_sample(0,DAY,START)+time_offset(08:00:00), EMI, SMOKING)==0 && now()>=get_last_sample(0,DAY,START)+time_offset(04:00:00) && now()<get_last_sample(0,DAY,START)+time_offset(08:00:00)) || (get_sample_no(get_last_sample(0,DAY,START)+time_offset(08:00:00), get_last_sample(0,DAY,START)+time_offset(12:00:00), EMI, SMOKING)==0 && now()>=get_last_sample(0,DAY,START)+time_offset(08:00:00) && now()<get_last_sample(0,DAY,START)+time_offset(12:00:00)))");

        b=conditionManager.evaluate("");
        b=conditionManager.evaluate("");
        b=conditionManager.evaluate("");
        b=conditionManager.evaluate("");

/*
        b=conditionManager.evaluate("get_last_sample(0,STUDY,START)<=now() && now()>=today()+time_offset(12:30:00) && now()<today()+time_offset(14:30:00) && get_sample_no(today()+time_offset(12:30:00),today()+time_offset(14:30:00),EMA,\"1230pm\")==0");
        b=conditionManager.evaluate("is_day_of_week(TUESDAY)==true && get_study_week()==1");
        b=conditionManager.evaluate("get_last_sample(0,STUDY,START)<=now() && now()>=today()+time_offset(19:30:00) && now()<today()+time_offset(21:30:00) && get_sample_no(today()+time_offset(19:30:00),today()+time_offset(21:30:00),EMA,\"730pm\")==0");

        b=conditionManager.evaluate("get_last_sample(0,STUDY,START)<=now()");
        b=conditionManager.evaluate("get_study_week()");

        b=conditionManager.evaluate("is_active_day()");
        b=conditionManager.evaluate("get_last_sample(0,BATTERY)");
        b=conditionManager.evaluate("get_last_sample_time(BATTERY)");
        b=conditionManager.evaluate("get_sample_no(now()-time_offset(00:01:00), now(), BATTERY)");
        b=conditionManager.evaluate("is_driving(now()-time_offset(00:05:00),now())");


        b=conditionManager.evaluate("now()");
        b=conditionManager.evaluate("today()");

        b=conditionManager.evaluate("time_offset(2:00:00)");
        b=conditionManager.evaluate("today()+time_offset(2:00:00)");
        b=conditionManager.evaluate("get_phone_battery()");
        b = conditionManager.evaluate("day_of_week()");
        res = conditionManager.isTrue("is_day_of_week(TUESDAY)");
        res = conditionManager.isTrue("is_day_of_week(THURSDAY)");

*/
    }
}
