package doctorwho.ethan.purplemorocco;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import doctorwho.ethan.purplemorocco.MainActivity;
import doctorwho.ethan.purplemorocco.R;
import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.ParticleEventVisibility;
import io.particle.android.sdk.utils.Toaster;

public class GeofenceService extends IntentService {
    public GeofenceService() {
        super("GeofenceService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ParticleCloudSDK.init(this);

        try {
            ParticleCloudSDK.getCloud().logIn("ethanollins6@gmail.com", "33263326e");
        } catch (ParticleCloudException e) {
            e.printStackTrace();
        }

        String board = "0-Test".substring("0-Test".indexOf('-') + 1);
        String task = "LED On;";

        if (intent.hasExtra("time")) {
            String taskFull = intent.getStringExtra("taskName") + ";";

            try {
                ParticleCloudSDK.getCloud().publishEvent(intent.getStringExtra("boardName"), taskFull, ParticleEventVisibility.PRIVATE, 60);
            } catch (ParticleCloudException e) {
                e.printStackTrace();
            }

            return;
        }

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {

        } else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();

            if (geofences != null) {
                Geofence geofence = geofences.get(0);
                String requestId = geofence.getRequestId();

                List<String> components = Arrays.asList(requestId.split("~"));
                String boardName = components.get(1).substring(components.get(1).indexOf('-') + 1);
                String taskName = components.get(2);

                String identifier = components.get(0);

                SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
                String dateTimeKey = "com.doctorwho.ethan.retiredidentifiers";
                String identifiers = prefs.getString(dateTimeKey, "");

                List identifierList = Arrays.asList(identifiers.split("-"));
                if (identifierList.contains(identifier)) {
                    return;
                }

                String retiredIdentifierKey = "com.doctorwho.ethan.retiredidentifiers";
                String retiredIdentifiers = prefs.getString(retiredIdentifierKey, "");
                prefs.edit().putString(retiredIdentifierKey, retiredIdentifiers + (identifier + "-")).apply();

                String taskFull = taskName + ";";

                Intent intent1 = new Intent(GeofenceService.this, SendNotification.class);
                intent1.putExtra("title", "Location Task Success");
                intent1.putExtra("text", taskName + " was executed successfully.");
                startService(intent1);

                try {
                    ParticleCloudSDK.getCloud().publishEvent(boardName, taskFull, ParticleEventVisibility.PRIVATE, 60);
                } catch (ParticleCloudException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(GeofenceService.this, LocationCheck2.class);
                i.putExtra("task", "remove");
                i.putExtra("data", requestId);
                startService(i);

                Log.d("", "");
            }
        }
    }
}
