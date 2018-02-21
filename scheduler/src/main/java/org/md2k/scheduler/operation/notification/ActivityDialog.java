package org.md2k.scheduler.operation.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import org.md2k.scheduler.R;

public class ActivityDialog extends AppCompatActivity {

    public static Activity fa;
    public static final String TYPE = "TYPE";
    public static final String TITLE = "TITLE";
    public static final String CONTENT = "CONTENT";
    public static final String BUTTONS = "BUTTONS";
    public static final String CONFIRM = "CONFIRM";
    public static final String RESULT = "RESULT";
    public static final String INTENT_RESULT = "INTENT_RESULT";

    String title, content, type;
    String[] buttons;
    boolean[] confirm;
    MaterialDialog dialog, dialogConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        fa = this;
        getValues();
        showDialog();
    }
    @Override
    protected void onDestroy(){
        try{
            dialog.dismiss();
        }catch (Exception ignored){}
        try{
            dialogConfirm.dismiss();
        }catch (Exception ignored){}
        super.onDestroy();
    }

    private void showDialog() {
        MaterialDialog.Builder md = new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .cancelable(false);
        if (buttons.length > 0) {
            md = md.positiveText(buttons[0]).onPositive((dialog, which) -> {
                if (confirm[0]) {
                    showDialogConfirm(buttons[0]);
                } else sendData(buttons[0]);
            });
        }
        if (buttons.length > 1) {
            md = md.negativeText(buttons[1]).onNegative((dialog, which) -> {
                if (confirm[1]) {
                    showDialogConfirm(buttons[1]);
                } else sendData(buttons[1]);
            });
        }
        if (buttons.length > 2) {
            md = md.neutralText(buttons[2]).onNeutral((dialog, which) -> {
                if (confirm[2]) {
                    showDialogConfirm(buttons[2]);
                } else sendData(buttons[2]);
            });
        }
        dialog = md.build();
        dialog.show();
    }
    private void showDialogConfirm(String text) {
        MaterialDialog.Builder md = new MaterialDialog.Builder(this)
                .title("Confirm")
                .content(content+".\n\nYou select: \""+text+"\"")
                .cancelable(false)
                .positiveText("Confirm")
                .onPositive((dialog, which) -> sendData(text)).negativeText("Cancel").onNegative((dialog, which) -> {
                    dialogConfirm.dismiss();
                    showDialog();
                });
        dialogConfirm=md.build();
        dialogConfirm.show();
    }

    void getValues() {
        type = getIntent().getStringExtra(TYPE);
        title = getIntent().getStringExtra(TITLE);
        content = getIntent().getStringExtra(CONTENT);
        buttons = getIntent().getStringArrayExtra(BUTTONS);
        confirm = getIntent().getBooleanArrayExtra(CONFIRM);
    }
    void sendData(String data){
        Intent intent = new Intent(INTENT_RESULT);
        // You can also include some extra data.
        intent.putExtra(RESULT, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
