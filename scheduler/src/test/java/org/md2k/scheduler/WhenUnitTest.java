package org.md2k.scheduler;

import org.junit.Test;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.scheduler.scheduler.when.When;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import rx.observers.TestSubscriber;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class WhenUnitTest {
    @Test
    public void when_time() throws Exception {
/*        Calendar calendar= Calendar.getInstance();
        System.out.println("currenttime="+ DateTime.convertTimeStampToDateTime(DateTime.getDateTime()));
        calendar.add(Calendar.SECOND, 5);
        String time=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(calendar.get(Calendar.MINUTE))+":"+String.valueOf(calendar.get(Calendar.SECOND));
        System.out.println("triggerTime="+ time);
        When when=new When(null,time, "00:00:02");
        TestSubscriber<Boolean> subscriber = new TestSubscriber<>();
        when.getObservable(null).subscribe(subscriber);
//        subscriber.assertValue(true);
        subscriber.awaitTerminalEvent(12, TimeUnit.SECONDS);
        subscriber.assertNotCompleted();
        ArrayList<Boolean> res=new ArrayList<>();
        res.add(true);res.add(true);res.add(true);res.add(true);
        subscriber.assertReceivedOnNext(res);
*/
    }
}