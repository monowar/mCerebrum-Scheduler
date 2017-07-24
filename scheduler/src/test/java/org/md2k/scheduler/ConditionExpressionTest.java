/*
package org.md2k.scheduler;
*/
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
 *//*


import org.junit.Test;
import org.md2k.scheduler.condition.Condition;
import org.md2k.scheduler.condition.Conditions;
import org.md2k.scheduler.datakit.DataKitManager;
import org.md2k.scheduler.exception.ConfigurationFileFormatError;
import org.md2k.scheduler.exception.DataKitAccessError;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ConditionExpressionTest {
    @Test
    public void isValid()  {
        try {
//        DataKitManager dataKitManager=mock(DataKitManager.class);
        Condition condition1=mock(Condition.class);
        Condition condition2=mock(Condition.class);
        Condition condition3=mock(Condition.class);
        Condition condition4=mock(Condition.class);

        Condition[] conditionss=new Condition[4];
        conditionss[0]=condition1;
        conditionss[1]=condition2;
        conditionss[2]=condition3;
        conditionss[3]=condition4;
        Conditions conditions  = new Conditions(conditionss);
        when(condition1.getId()).thenReturn("WEEK1");
        when(condition2.getId()).thenReturn("TUESDAY");
        when(condition3.getId()).thenReturn("WEEK11");
        when(condition4.getId()).thenReturn("SATURDAY");
        when(condition1.isValid(null)).thenReturn(true);
        when(condition2.isValid(null)).thenReturn(false);
        when(condition3.isValid(null)).thenReturn(false);
        when(condition4.isValid(null)).thenReturn(true);

        boolean check1 = false;
        boolean check2 = false;
        boolean check3 = false;
        boolean check4 = false;
            check1 = conditions.isValid(null, "WEEK1==TRUE && TUESDAY==FALSE");
            check2 = conditions.isValid(null, "( WEEK1 && SATURDAY ) || ( WEEK11 && SATURDAY )");
            check3 = conditions.isValid(null, "WEEK1 || TUESDAY || (WEEK11 && SATURDAY)");
            check4 = conditions.isValid(null, "WEEK1 && TUESDAY && WEEK11 && SATURDAY");
            assertTrue(check1);
            assertTrue(check2);
            assertTrue(check3);
            assertFalse(check4);

        } catch (ConfigurationFileFormatError configurationFileFormatError) {
            configurationFileFormatError.printStackTrace();
        } catch (DataKitAccessError dataKitAccessError) {
            dataKitAccessError.printStackTrace();
        }
//        verify(condition).query("* from t");
    }
}
*/
