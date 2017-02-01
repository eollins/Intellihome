package doctorwho.ethan.purplemorocco;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;

public class Boards extends AppCompatActivity {
    List<String> boards = new ArrayList<>();
    List<Boolean> connections = new ArrayList<>();
    List<String> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards);

        boards.add("Select a board");
        connections.add(false);
        ids.add("gallbladder");

        final Spinner dropdown6 = (Spinner)findViewById(R.id.spinner5);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, boards);
        dropdown6.setAdapter(adapter);

        ParticleCloudSDK.init(this);

        login log = new login();
        log.execute();

        getDevices gd = new getDevices();
        gd.execute();

        update();

        final Spinner s = (Spinner)findViewById(R.id.spinner5);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView t = (TextView) findViewById(R.id.textView9);

                    boolean bool = connections.get(s.getSelectedItemPosition());
                    String b = "";

                    if (bool) {
                        b = "Online";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            t.setTextColor(getResources().getColor(R.color.online, null));
                        }
                    }
                    else {
                        b = "Offline";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            t.setTextColor(getResources().getColor(R.color.offline, null));
                        }
                    }

                    t.setText(boards.get(s.getSelectedItemPosition()) + "\n" + b);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private class login extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                ParticleCloudSDK.getCloud().logIn("ethanollins6@gmail.com", "33263326e");
            } catch (ParticleCloudException e) {
                e.printStackTrace();
            }

            return "wibbly wobbly timey wimey";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    private class getDevices extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            List<ParticleDevice> devices = null;
            try {
                devices = ParticleCloudSDK.getCloud().getDevices();
            } catch (ParticleCloudException e) {
                e.printStackTrace();
            }
            for (ParticleDevice device : devices) {
                boards.add(device.getName());
                connections.add(device.isConnected());
                ids.add(device.getID());
            }

            return "wibbly wobbly timey wimey";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    public void reload(View v) {
        boards.clear();
        boards.add("Select a board");

        connections.clear();
        connections.add(false);

        ids.clear();
        ids.add("gallbladder");

        TextView t = (TextView)findViewById(R.id.textView9);
        t.setText("");

        getDevices gd = new getDevices();
        gd.execute();
    }

    public void update() {
        final Spinner dropdown6 = (Spinner)findViewById(R.id.spinner5);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, boards);
        dropdown6.setAdapter(adapter);

        dropdown6.setSelection(0);
    }
}
