package org.md2k.scheduler;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.messagehandler.ResultCallback;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.utilities.Apps;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;
import org.md2k.utilities.permission.PermissionInfo;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.*;
import rx.schedulers.Schedulers;

public class ActivityMain extends AppCompatActivity {
    private static final String TAG = ActivityMain.class.getSimpleName();
    Subscription subscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load();
    }

    private void load() {
        final Button buttonService = (Button) findViewById(R.id.button_app_status);
        buttonService.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMain.this, ServiceScheduler.class);
            if (Apps.isServiceRunning(ActivityMain.this, ServiceScheduler.class.getName())) {
                stopService(intent);
            } else {
                startService(intent);
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_test:
//                intent = new Intent(this, ActivityTest.class);
//                startActivity(intent);
                break;
            case R.id.action_report:
                //TODO: add report
                break;
            case R.id.action_about:
                intent = new Intent(this, ActivityAbout.class);
                try {
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_CODE, String.valueOf(this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_NAME, this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                break;
            case R.id.action_copyright:
                intent = new Intent(this, ActivityCopyright.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
//                intent = new Intent(this, ActivityIncentiveSettings.class);
//                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        subscription = getObservableTime().subscribe();
        super.onResume();
    }

    public void onPause() {
        if(subscription!=null) subscription.unsubscribe();
        super.onPause();
    }
    private Observable<Boolean> getObservableTime(){
        return Observable.interval(1000, TimeUnit.MILLISECONDS,AndroidSchedulers.mainThread())
                .map(aLong -> {
                    long time = Apps.serviceRunningTime(ActivityMain.this, ServiceScheduler.class.getName());
                    if (time < 0) {
                        ((TextView) findViewById(R.id.button_app_status)).setText("START");
                        findViewById(R.id.button_app_status).setBackground(ContextCompat.getDrawable(ActivityMain.this, R.drawable.button_status_off));

                    } else {

                        ((TextView) findViewById(R.id.button_app_status)).setText(DateTime.convertTimestampToTimeStr(time));
                        findViewById(R.id.button_app_status).setBackground(ContextCompat.getDrawable(ActivityMain.this, R.drawable.button_status_on));
                    }
                    return true;
                });

    }
}
