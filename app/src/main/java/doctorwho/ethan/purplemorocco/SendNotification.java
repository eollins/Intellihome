package doctorwho.ethan.purplemorocco;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.Random;

public class SendNotification extends IntentService {
    public SendNotification() {
        super("SendNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        //text = "this is a test.\n i am at a new line";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text));


        //mBuilder.setStyle(new NotificationCompat.InboxStyle());

        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        nManager.notify(new Random().nextInt(), mBuilder.build());
    }
}
