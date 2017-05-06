package doctorwho.ethan.purplemorocco;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.utils.Async;

public class NotificationReceiver extends Service {
    public NotificationReceiver() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        ParticleCloudSDK.init(this);

        subscription sub = new subscription();
        sub.execute();
    }

    private class subscription extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                ParticleCloudSDK.getCloud().subscribeToAllEvents("mainBoard:notification", new ParticleEventHandler() {
                    @Override
                    public void onEvent(String eventName, ParticleEvent particleEvent) {
                        List<String> info = Arrays.asList(particleEvent.dataPayload.split("~ "));

                        Intent i = new Intent(NotificationReceiver.this, SendNotification.class);
                        i.putExtra("title", info.get(0));
                        i.putExtra("text", info.get(1));
                        startService(i);
                    }

                    @Override
                    public void onEventError(Exception e) {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
