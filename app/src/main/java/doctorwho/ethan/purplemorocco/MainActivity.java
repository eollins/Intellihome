package doctorwho.ethan.purplemorocco;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import
android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.internal.LocationRequestInternal;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.common.io.Files;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.ParticleEventVisibility;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    Calendar calendar = Calendar.getInstance();

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    int sHour = 0;
    int sMinute = 0;

    boolean canAccessLocation = false;

    List<String> tasks = new ArrayList<String>();
    List<String> newTasks = new ArrayList<String>();
    List<String> allBoards = new ArrayList<>();
    List<String> statuses = new ArrayList<>();

    String selectedBoard;
    String selectedTask;

    List<String> boards = new ArrayList<>();

    Spinner dropdown = null;

    String coordinates = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParticleDeviceSetupLibrary.init(this.getApplicationContext(), MainActivity.class);

        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        new Login().execute();

        new Subscribe().execute();

        Intent q = new Intent(this, TimeCheck.class);
        startService(q);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boards.add("Select a board");
        newTasks.add("Select a task");

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        registerTasks();

        getDevices get = new getDevices();
        get.execute();

        Spinner dropdown3 = (Spinner) findViewById(R.id.boardSpinner);

        List<String> newBoards = new ArrayList<>();
        newBoards.add("Select a board");
        for (Object s : allBoards) {
            newBoards.add(s.toString());
        }

        ArrayAdapter<String> adapt3er = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, newBoards);
        dropdown3.setAdapter(adapt3er);

        thirdSubscription ts = new thirdSubscription();
        ts.execute();

        Spinner s = (Spinner) findViewById(R.id.spinner);
        String[] options = new String[]{"Run Immediately", "Run at Time", "Run at Location"};
        ArrayAdapter<String> sa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        s.setAdapter(sa);
        sa.notifyDataSetChanged();

        secondSubscription ss = new secondSubscription();
        ss.execute();

        Intent i = new Intent(this, LocationCheck2.class);
        this.startService(i);

        Intent i9 = new Intent(this, NotificationReceiver.class);
        startService(i9);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                2);

//        Intent r = new Intent(MainActivity.this, DataStorage.class);
//        r.putExtra("type", "location");
//        r.putExtra("action", "clear");
//        startService(r);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    Button b = (Button) findViewById(R.id.select);
                    b.setVisibility(View.INVISIBLE);

                    TextView t = (TextView) findViewById(R.id.selection);
                    t.setVisibility(View.INVISIBLE);
                } else if (position == 1) {
                    Button b = (Button) findViewById(R.id.select);
                    b.setVisibility(View.VISIBLE);
                    b.setText("Select time");

                    TextView t = (TextView) findViewById(R.id.selection);
                    t.setVisibility(View.VISIBLE);
                    t.setText("12:00 AM");
                } else if (position == 2) {
                    Button b = (Button) findViewById(R.id.select);
                    b.setVisibility(View.VISIBLE);

                    if (canAccessLocation) {
                        b.setEnabled(true);
                    } else {
                        b.setEnabled(false);
                    }

                    b.setText("Select location");

                    TextView t = (TextView) findViewById(R.id.selection);
                    t.setVisibility(View.VISIBLE);
                    t.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        final Button send = (Button) findViewById(R.id.button3);

        Button btn = (Button) findViewById(R.id.button4);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Toaster.s(MainActivity.this, "Task scheduled for " + selectedHour + ":" + selectedMinute);
                        sHour = selectedHour;
                        sMinute = selectedMinute;
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("");
                mTimePicker.show();
            }
        });

//        List<String> newBoards = new ArrayList<>();
//        for (String s3 : boards) {
//            String s2 = s3.toString();
//            s2 = s2.substring(s2.indexOf("-") + 1);
//            newBoards.add(s2);
//        }

        final Spinner dropdown6 = (Spinner) findViewById(R.id.boardSpinner);

        List<String> newBoards2 = new ArrayList<>();
        newBoards2.add("Select a board");
        for (Object s2 : allBoards) {
            newBoards2.add(s2.toString());
        }

        ArrayAdapter<String> adapt5er = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, newBoards2);
        dropdown6.setAdapter(adapt5er);

        reload2();

        dropdown6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateTasks(position - 1);
                selectedBoard = dropdown6.getSelectedItem().toString();

                try {
                    if (statuses.get(position) == "true") {
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.online)));
                    }
                    if (statuses.get(position) == "false") {
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.offline)));
                    }
                    if (position == 0) {
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                if (position == 0) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        final Spinner dropdown2 = (Spinner) findViewById(R.id.taskSpinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, newTasks);
        dropdown2.setAdapter(adapter2);

        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedTask = dropdown2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    canAccessLocation = true;
                } else {

                    canAccessLocation = false;
                    Toast.makeText(MainActivity.this, "Location functionality has been disabled.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    canAccessLocation = true;
                } else {

                    canAccessLocation = false;
                    Toast.makeText(MainActivity.this, "Location functionality has been disabled.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Internet functionality has been disabled.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void optionsClick(View v) {
        Intent i = new Intent(this, Options.class);
        startActivity(i);
    }

    public void selectCondition(View v) {
        Button b = (Button) findViewById(R.id.select);

        if (b.getText() == "Select time") {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            final int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    sHour = selectedHour;
                    sMinute = selectedMinute;

                    String full = "";
                    if (sHour > 11) {
                        int two = sHour - 12;
                        String three = Integer.toString(sMinute);

                        if (two == 0) {
                            two = 12;
                        }

                        if (sMinute < 10) {
                            three = "0" + sMinute;
                        }

                        full = two + ":" + three + " PM";
                    } else {
                        int two = sHour;
                        String three = Integer.toString(sMinute);
                        if (sHour == 0) {
                            two = 12;
                        }

                        if (sMinute < 10) {
                            three = "0" + sMinute;
                        }

                        full = two + ":" + three + " AM";
                    }

                    TextView t = (TextView) findViewById(R.id.selection);
                    t.setText(full);
                }
            }, hour, minute, false);
            mTimePicker.setTitle("");
            mTimePicker.show();
        } else if (b.getText() == "Select location") {
            try {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                Intent intent = intentBuilder.build(MainActivity.this);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);

            } catch (GooglePlayServicesRepairableException
                    | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    public void reload(View v) {
        boards.clear();
        newTasks.clear();

        boards.add("Select a board");
        newTasks.add("Select a task");

        registerTasks();

        Spinner dropdown3 = (Spinner) findViewById(R.id.boardSpinner);

        List<String> newBoards = new ArrayList<>();
        newBoards.add("Select a board");
        for (Object s : allBoards) {
            newBoards.add(s.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, newBoards);
        dropdown3.setAdapter(adapter);

//        Spinner dropdown2 = (Spinner)findViewById(R.id.taskSpinner);
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, newTasks);
//        dropdown2.setAdapter(adapter2);
    }

    public void reload2() {
        boards.clear();
        newTasks.clear();

        boards.add("Select a board");
        newTasks.add("Select a task");

        registerTasks();

        Spinner dropdown3 = (Spinner) findViewById(R.id.boardSpinner);

        List<String> newBoards = new ArrayList<>();
        newBoards.add("Select a board");
        for (Object s : allBoards) {
            newBoards.add(s.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, newBoards);
        dropdown3.setAdapter(adapter);

//        Spinner dropdown2 = (Spinner)findViewById(R.id.taskSpinner);
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, newTasks);
//        dropdown2.setAdapter(adapter2);
    }

    int type = 0;

    public void sendTask(View v) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to run this task?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendCompleteTask();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    String dataPhase1 = "";

    public void sendCompleteTask() {
        Spinner s = (Spinner) findViewById(R.id.spinner);
        TextView t = (TextView) findViewById(R.id.selection);

        Spinner boardSpinner = (Spinner) findViewById(R.id.boardSpinner);
        Spinner taskSpinner = (Spinner) findViewById(R.id.taskSpinner);

        if (s.getSelectedItemPosition() == 0) {
            type = 0;
        } else if (s.getSelectedItemPosition() == 1) {
            type = 1;
        } else {
            type = 2;

            String identifier = generateIdentifier();
            dataPhase1 = identifier + "`" + boardSpinner.getSelectedItem().toString() + "`" + taskSpinner.getSelectedItem().toString();
        }

        new sendTask().execute();
    }

    public void runNow(View v) {
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox);

        Button b = (Button) findViewById(R.id.button4);
        if (cb.isChecked()) {
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }

    public void updateBoards(List<String> boardList) {
    }

    public void updateTasks(int id) {
        Spinner dropdown2 = (Spinner) findViewById(R.id.taskSpinner);
        List<String> allTasks = new ArrayList<String>();

        for (int i = 0; i < newTasks.size(); i++) {
            if (newTasks.get(i).toString().startsWith((String.valueOf(id)))) {
                allTasks.add(newTasks.get(i).substring(newTasks.get(i).indexOf('-') + 1));
            }
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, allTasks);
        dropdown2.setAdapter(adapter2);
    }

    public void setTime(View view) {
        new TimePickerDialog(getApplicationContext(), (TimePickerDialog.OnTimeSetListener) this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false).show();
    }

    public void clear(View v) {
        Intent i = new Intent(MainActivity.this, DataStorage.class);
        i.putExtra("type", "location");
        i.putExtra("action", "clear");
        startService(i);
    }

    public void registerTasks() {
        new registerTasks().execute();
    }

    public void placeApi(View v) {
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(MainActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            TextView t = (TextView) findViewById(R.id.selection);
            t.setText(address);

            final LatLng coords = place.getLatLng();
            coordinates = String.valueOf(coords.latitude) + " " + String.valueOf(coords.longitude);
            Toaster.s(MainActivity.this, coordinates);

            //t.setText(address);
            //mAddress.setText(address);
            //mAttributions.setText(Html.fromHtml(attributions));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void subscriptionEvent(String info, String data, String name, String tasks, String boardID) {
        int boardId = boards.size() - 1;

        boards.add(boardId + "-" + name);
        String[] taskArray = tasks.split(",");

        SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
        String boardIDKey = "com.doctorwho.ethan.authorizedBoards";
        String authorizedBoards = prefs.getString(boardIDKey, "");

        for (int i = 0; i < taskArray.length; i++) {
            newTasks.add(boardId + "-" + taskArray[i]);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class Login extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                ParticleCloudSDK.getCloud().logIn("ethanollins6@gmail.com", "33263326e");
                log("Logged into Particle Cloud.");

            } catch (ParticleCloudException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class Subscribe extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            long subscriptionId;
            try {
                subscriptionId = ParticleCloudSDK.getCloud().subscribeToAllEvents(
                        "mainBoard",  // the first argument, "eventNamePrefix", is optional
                        new ParticleEventHandler() {
                            public void onEvent(String eventName, ParticleEvent event) {
                                //Toaster.s(MainActivity.this, event.dataPayload);

                                String info = event.dataPayload;
                                String data = info.substring(info.indexOf(':') + 1);
                                String name = data.substring(data.indexOf('=') + 1, data.indexOf(';'));
                                String tasks = data.substring(data.indexOf(';') + 1, data.indexOf('~'));
                                String boardID = data.substring(data.indexOf('~') + 1);

                                subscriptionEvent(info, data, name, tasks, boardID);
                            }

                            public void onEventError(Exception e) {
                                Log.e("some tag", "Event error: ", e);
                            }
                        });
                log("Subscribed to register events.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class secondSubscription extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            long subscriptionId;
            try {
                subscriptionId = ParticleCloudSDK.getCloud().subscribeToAllEvents("mainBoard:status", new ParticleEventHandler() {
                    @Override
                    public void onEvent(String eventName, ParticleEvent particleEvent) {
                        String info = particleEvent.dataPayload;

                        List<String> components = Arrays.asList(info.split("~"));
                        String boardName = components.get(0);
                        String taskName = components.get(1);
                        String time = components.get(2);
                        String status = components.get(3);

                        List<String> timeComponents = Arrays.asList(time.split(":"));
                        if (Integer.parseInt(timeComponents.get(0)) > 7) {
                            int hour = Integer.parseInt(timeComponents.get(0));
                            hour -= 7;
                            timeComponents.set(0, String.valueOf(hour));
                        } else {
                            int hour = Integer.parseInt(timeComponents.get(0));
                            hour = 24 - (8 - hour);
                            timeComponents.set(0, String.valueOf(hour));
                        }

                        Intent i = new Intent(MainActivity.this, SendNotification.class);

                        if (status.equals("success")) {
                            i.putExtra("title", "Successful Task");
                            i.putExtra("text", boardName + "'s task " + taskName + " was completed at " + timeComponents.get(0) + ":" + timeComponents.get(1) + ".");
                            //i.putExtra("text", "this is a test.\ni am at a new line");
                        } else {
                            i.putExtra("title", "Task Failure");
                            i.putExtra("text", boardName + "'s task " + taskName + " failed.");
                        }

                        startService(i);

                        //sample payload: Purple_Morocco:LED On:0:00:success
                    }

                    @Override
                    public void onEventError(Exception e) {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            log("Subscribed to status updates.");

            return null;
        }
    }

    int nadf = 0;

    private class thirdSubscription extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                ParticleCloudSDK.getCloud().subscribeToAllEvents("mainBoard:information", new ParticleEventHandler() {
                    @Override
                    public void onEvent(String eventName, ParticleEvent particleEvent) {
                        String data = particleEvent.dataPayload;
                        List<String> info = Arrays.asList(data.split("~"));

                        Intent i = new Intent(MainActivity.this, SendNotification.class);
                        i.putExtra("title", "Task Information");
                        i.putExtra("text", info.get(nadf));
                        startService(i);

                        log("Received information \"" + info.get(nadf) + "\" from board " + particleEvent.deviceId + " regarding task " + particleEvent.dataPayload);
                    }

                    @Override
                    public void onEventError(Exception e) {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            log("Subscribed to information responses.");
            return null;
        }
    }

    private class fourthSubscription extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                ParticleCloudSDK.getCloud().subscribeToAllEvents("mainBoard:notification", new ParticleEventHandler() {
                    @Override
                    public void onEvent(String eventName, ParticleEvent particleEvent) {
                        List<String> info = Arrays.asList(particleEvent.dataPayload.split("~"));

                        Intent i = new Intent(MainActivity.this, SendNotification.class);
                        i.putExtra("title", info.get(0));
                        i.putExtra("text", info.get(1));
                        startService(i);

                        log("Received notification \"" + info.get(1) + "\" from board " + particleEvent.deviceId);
                    }

                    @Override
                    public void onEventError(Exception e) {

                    }
                });

                log("Subscribed to board notifications.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void popup(String title, String text) {

    }


    private class sendTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                CheckBox box = (CheckBox) findViewById(R.id.checkBox);

                if (type == 0) {
                    ParticleCloudSDK.getCloud().publishEvent(selectedBoard.substring(selectedBoard.indexOf('-') + 1), selectedTask.substring(selectedTask.indexOf('-') + 1) + ";", ParticleEventVisibility.PRIVATE, 60);
                    log("Requested task " + selectedTask + " of board " + selectedBoard.substring(selectedBoard.indexOf('-') + 1) + " to run immediately.");
                } else if (type == 1) {
                    Date dt = new Date();
                    List<String> timeData = Arrays.asList(dt.toString().split(" "));
                    List<String> components = Arrays.asList(timeData.get(3).split(":"));

                    if (Integer.toString(sHour).equals(components.get(0)) && Integer.toString(sMinute).equals(components.get(1))) {
                        ParticleCloudSDK.getCloud().publishEvent(selectedBoard.substring(selectedBoard.indexOf('-') + 1), selectedTask.substring(selectedTask.indexOf('-') + 1) + ";", ParticleEventVisibility.PRIVATE, 60);
                        return "";
                    }

                    Intent i = new Intent(MainActivity.this, TimeCheck.class);
                    i.putExtra("task", "add");

                    log("Requested task " + selectedTask.substring(Integer.parseInt(selectedTask.indexOf('-') + " of board " + selectedBoard.substring(selectedBoard.indexOf('-') + 1) + " to run at " + sHour + ":" + sMinute + ".")));

                    i.putExtra("boardName", selectedBoard.substring(selectedBoard.indexOf('-') + 1));
                    i.putExtra("taskName", selectedTask.substring(selectedTask.indexOf('-') + 1));
                    i.putExtra("time", sHour + ":" + sMinute);
                    startService(i);
//                    ParticleCloudSDK.getCloud().publishEvent(selectedBoard.substring(selectedBoard.indexOf('-') + 1), (selectedTask.substring(selectedTask.indexOf('-') + 1) + ";" + sHour + ":" + sMinute), ParticleEventVisibility.PRIVATE, 60);
                } else {
                    String[] coords = coordinates.split(" ");

                    String lon = coords[0];
                    String lat = coords[1];

                    String dataToStore = dataPhase1 + "`" + lon + "`" + lat + "~";

                    Intent i = new Intent(MainActivity.this, LocationCheck2.class);
                    i.putExtra("task", "add");
                    i.putExtra("data", dataToStore);
                    startService(i);

                    log("Requested task " + selectedTask.substring(Integer.parseInt(selectedTask.indexOf('-') + " of board " + selectedBoard.substring(selectedBoard.indexOf('-') + 1) + " to run at " + lon + " " + lat + ".")));
                }

            } catch (ParticleCloudException e) {
                e.printStackTrace();
            }
            return "Requested";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            information();
        }

        if (id == R.id.action_settings3) {
            Intent i = new Intent(MainActivity.this, doctorwho.ethan.purplemorocco.Log.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


    private class registerTasks extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                ParticleCloudSDK.getCloud().publishEvent("register", "", ParticleEventVisibility.PRIVATE, 60);
            } catch (ParticleCloudException e) {
                e.printStackTrace();
            }

            log("Published a universal registration command.");

            return "wibbly wobbly timey wimey";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }


    char[] characters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public String generateIdentifier() {
        boolean confirmed = false;
        String full = "";

        while (!confirmed) {
            for (int i = 0; i < 8; i++) {
                full += characters[(int) (Math.random() * 62 + 0)];
            }

            SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
            String dateTimeKey = "com.doctorwho.ethan.identifiers";
            String identifiers = prefs.getString(dateTimeKey, "");

            List identifierList = Arrays.asList(identifiers.split("-"));
            if (identifierList.contains(full)) {
                full = "";
            } else {
                confirmed = true;
            }
        }

        return full;
    }

    public void sendNotification(String title, String text) {

    }

    String s;
    String p;

    public void information() {
        Spinner boardSpinner = (Spinner) findViewById(R.id.boardSpinner);
        String str = boards.get(boardSpinner.getSelectedItemPosition());
        String str2 = str.substring(str.indexOf("-") + 1);
        s = str2;

        Spinner taskSpinner = (Spinner) findViewById(R.id.taskSpinner);
        p = taskSpinner.getSelectedItem().toString();

        nadf = taskSpinner.getSelectedItemPosition();

        infoRequest in = new infoRequest();
        in.execute();
    }

    private class infoRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                ParticleCloudSDK.getCloud().publishEvent(s + ":infoRequest", p, ParticleEventVisibility.PRIVATE, 60);

                log("Published an information request to board " + s + "'s task " + p + ".");
            } catch (ParticleCloudException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class getDevices extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            allBoards.clear();
            statuses.clear();

            List<ParticleDevice> devices = new ArrayList<>();
            try {
                devices = ParticleCloudSDK.getCloud().getDevices();
            } catch (ParticleCloudException e) {
                e.printStackTrace();
            }
            for (ParticleDevice device : devices) {
                allBoards.add(device.getName());
                statuses.add(String.valueOf(device.isConnected()));
            }

            return null;
        }
    }

    public void log(String data) {
        DateTime dt = DateTime.now();

        String minute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
        if (minute.length() == 1) {
            minute = "0" + minute;
        }

        Intent i = new Intent(MainActivity.this, DataStorage.class);
        i.putExtra("type", "log");
        i.putExtra("action", "append");
        i.putExtra("data", "[" + Calendar.getInstance().get(Calendar.HOUR) + ":" + minute + ":" + Calendar.getInstance().get(Calendar.SECOND) + " " + Calendar.getInstance().get(Calendar.MONTH) + "/" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + Calendar.getInstance().get(Calendar.YEAR) + "] " + data + "~");
        startService(i);
    }
}

//just this once, everybody lives