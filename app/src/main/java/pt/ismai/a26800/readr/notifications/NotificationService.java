package pt.ismai.a26800.readr.notifications;

import android.app.IntentService;
import android.content.Intent;

public class NotificationService extends IntentService {
    public NotificationService() {
        super("Article Notifications");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        System.out.println("fdsfs");
    }
}
