package com.example.test2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

public class MyCodeReceiver extends BroadcastReceiver {

    private Message message;
    private static final String TAG = "MyCodeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("com.barcode.sendBroadcast")) {
            String str = intent.getStringExtra("BARCODE");
            if (!"".equals(str)) {
                message.getMsg(str);
            }
        }
    }

    interface Message {
        public void getMsg(String str);
    }

    public void setMessage(Message message) {

        this.message = message;
    }


}
