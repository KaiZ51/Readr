package com.pedro.readr.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, NotificationService.class);
        i.putExtra("cats", intent.getStringArrayExtra("cats"));
        context.startService(i);
    }
}
