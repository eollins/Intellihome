package doctorwho.ethan.purplemorocco;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
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

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {

        } else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();

            if (geofences != null) {
                Geofence geofence = geofences.get(0);
                String requestId = geofence.getRequestId();
            }
        }
    }
}
