package doctorwho.ethan.purplemorocco;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleEventVisibility;

public class TimeCheck extends Service {
    final Context context = this;

    public TimeCheck() {
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_TIME_TICK)){
                SharedPreferences prefs = context.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
                String dateTimeKey = "com.doctorwho.ethan.times";
                String locations = prefs.getString(dateTimeKey, "");


                List<String> tasks = Arrays.asList(locations.split("`"));
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i) == "") {
                        return;
                    }

                    List<String> attributes = Arrays.asList(tasks.get(i).split("~"));
                    List<String> comparison = Arrays.asList(attributes.get(2).split(":"));

                    Date dt = new Date();
                    List<String> timeData = Arrays.asList(dt.toString().split(" "));
                    List<String> components = Arrays.asList(timeData.get(3).split(":"));

                    String hour = components.get(0);
                    String minute = components.get(1);

                    String hour2 = comparison.get(0);
                    String minute2 = comparison.get(1);

                    if (minute2.length() == 1) {
                        minute2 = "0" + minute2;
                    }

                    if (hour.equals(hour2) && minute.equals(minute2)) {
                        Intent d = new Intent(TimeCheck.this, GeofenceService.class);
                        d.putExtra("time", "");
                        d.putExtra("boardName", attributes.get(0));
                        d.putExtra("taskName", attributes.get(1));
                        startService(d);

                        Intent o = new Intent(TimeCheck.this, DataStorage.class);
                        o.putExtra("type", "time");
                        o.putExtra("action", "remove");
                        o.putExtra("data", attributes.get(0) + "~" + attributes.get(1) + "~" + hour2 + ":" + minute2);
                        startService(o);

                        Intent u = new Intent(TimeCheck.this, SendNotification.class);
                        u.putExtra("title", "Timed Task Success");
                        u.putExtra("text", attributes.get(1) + " was executed successfully.");
                        startService(u);
                    }
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent.hasExtra("task") && intent.getStringExtra("task").equals("add")) {
                String boardName = intent.getStringExtra("boardName");
                String taskName = intent.getStringExtra("taskName");
                String time = intent.getStringExtra("time");

                String data = boardName + "•" + taskName + "•" + time;

                Intent i = new Intent(TimeCheck.this, DataStorage.class);
                i.putExtra("type", "time");
                i.putExtra("action", "add");
                i.putExtra("data", data);
                startService(i);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
