package com.test.helloworld.androidtest2.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SmsTestSentBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (getResultCode() == Activity.RESULT_OK) {
            Toast.makeText(context, "发送成功,哈哈", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "发送失败，呜呜", Toast.LENGTH_SHORT).show();
        }
    }
}
