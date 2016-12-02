package doctorwho.ethan.purplemorocco;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import
android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
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

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
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

    List<String> tasks = new ArrayList<String>();
    List<String> newTasks = new ArrayList<String>();

    String selectedBoard;
    String selectedTask;

    List boards = new ArrayList<>();

    Spinner dropdown = null;

    String coordinates = "";

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

        registerTasks();

        Spinner dropdown3 = (Spinner)findViewById(R.id.boardSpinner);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, boards);
        dropdown3.setAdapter(adp);

        Spinner s = (Spinner)findViewById(R.id.spinner);
        String[] options = new String[]{"Run Immediately", "Run at Time", "Run at Location" };
        ArrayAdapter<String> sa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        s.setAdapter(sa);
        sa.notifyDataSetChanged();

        Intent i= new Intent(this, LocationCheck2.class);
        this.startService(i);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    Button b = (Button)findViewById(R.id.select);
                    b.setVisibility(View.INVISIBLE);

                    TextView t = (TextView)findViewById(R.id.selection);
                    t.setVisibility(View.INVISIBLE);
                }
                else if (position == 1) {
                    Button b = (Button)findViewById(R.id.select);
                    b.setVisibility(View.VISIBLE);
                    b.setText("Select time");

                    TextView t = (TextView)findViewById(R.id.selection);
                    t.setVisibility(View.VISIBLE);
                    t.setText("12:00 AM");
                }
                else if (position == 2) {
                    Button b = (Button)findViewById(R.id.select);
                    b.setVisibility(View.VISIBLE);
                    b.setText("Select location");

                    TextView t = (TextView)findViewById(R.id.selection);
                    t.setVisibility(View.VISIBLE);
                    t.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        final Button send = (Button)findViewById(R.id.button3);

        Button btn = (Button)findViewById(R.id.button4);
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

        dropdown = (Spinner)findViewById(R.id.boardSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, boards);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateTasks(position - 1);
                selectedBoard = dropdown.getSelectedItem().toString();

                if (position == 0) {
                    send.setEnabled(false);
                }
                else {
                    send.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        final Spinner dropdown2 = (Spinner)findViewById(R.id.taskSpinner);
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

        try {
            FileOutputStream fos = openFileOutput("Locations.txt", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            osw.write("");
            osw.flush();
            osw.close();

        } catch (FileNotFoundException e) {
            //catch errors opening file
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void optionsClick(View v) {
        Intent i = new Intent(this, SecondActivity.class);
        startActivity(i);
    }

    public void selectCondition(View v) {
        Button b = (Button)findViewById(R.id.select);

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
                    }
                    else {
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

                    TextView t = (TextView)findViewById(R.id.selection);
                    t.setText(full);
                }
            }, hour, minute, false);
            mTimePicker.setTitle("");
            mTimePicker.show();
        }
        else if (b.getText() == "Select location") {
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

        Spinner dropdown3 = (Spinner)findViewById(R.id.boardSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, boards);
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
        Spinner s = (Spinner)findViewById(R.id.spinner);
        TextView t = (TextView)findViewById(R.id.selection);

        Spinner boardSpinner = (Spinner)findViewById(R.id.boardSpinner);
        Spinner taskSpinner = (Spinner)findViewById(R.id.taskSpinner);

        if (s.getSelectedItemPosition() == 0) {
            type = 0;
        }
        else if (s.getSelectedItemPosition() == 1) {
            type = 1;
        }
        else {
            type = 2;
            dataPhase1 = "0" + "`" + boardSpinner.getSelectedItem().toString() + "`" + taskSpinner.getSelectedItem().toString();
        }

        new sendTask().execute();
    }

    public void runNow(View v) {
        CheckBox cb = (CheckBox)findViewById(R.id.checkBox);

        Button b = (Button)findViewById(R.id.button4);
        if (cb.isChecked()) {
            b.setEnabled(false);
        }
        else {
            b.setEnabled(true);
        }
    }

    public void updateBoards(List<String> boardList) {
    }

    public void updateTasks(int id) {
        Spinner dropdown2 = (Spinner)findViewById(R.id.taskSpinner);
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

            TextView t = (TextView)findViewById(R.id.selection);
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

    public void subscriptionEvent(String info, String data, String name, String tasks) {
        int boardId = boards.size() - 1;

        boards.add(boardId + "-" + name);
        String[] taskArray = tasks.split(",");

        for (int i = 0; i < taskArray.length; i++) {
            newTasks.add(boardId + "-" + taskArray[i]);
        }
    }

    private class Login extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                ParticleCloudSDK.getCloud().logIn("ethanollins6@gmail.com", "33263326e");
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
                                String tasks = data.substring(data.indexOf(';') + 1);

                                subscriptionEvent(info, data, name, tasks);
                            }

                            public void onEventError(Exception e) {
                                Log.e("some tag", "Event error: ", e);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class sendTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                CheckBox box = (CheckBox)findViewById(R.id.checkBox);

                if (type == 0) {
                    ParticleCloudSDK.getCloud().publishEvent(selectedBoard.substring(selectedBoard.indexOf('-') + 1), selectedTask.substring(selectedTask.indexOf('-') + 1) + ";", ParticleEventVisibility.PRIVATE, 60);
                }
                else if (type == 1) {
                    Date dt = new Date();
                    List<String> timeData = Arrays.asList(dt.toString().split(" "));
                    List<String> components = Arrays.asList(timeData.get(3).split(":"));

                    if (Integer.toString(sHour).equals(components.get(0)) && Integer.toString(sMinute).equals(components.get(1))) {
                        ParticleCloudSDK.getCloud().publishEvent(selectedBoard.substring(selectedBoard.indexOf('-') + 1), selectedTask.substring(selectedTask.indexOf('-') + 1) + ";", ParticleEventVisibility.PRIVATE, 60);
                        return "";
                    }

                    Intent i = new Intent(MainActivity.this, TimeCheck.class);
                    i.putExtra("task", "add");
                    i.putExtra("boardName", selectedBoard.substring(selectedBoard.indexOf('-') + 1));
                    i.putExtra("taskName", selectedTask.substring(selectedTask.indexOf('-') + 1));
                    i.putExtra("time", sHour + ":" + sMinute);
                    startService(i);
//                    ParticleCloudSDK.getCloud().publishEvent(selectedBoard.substring(selectedBoard.indexOf('-') + 1), (selectedTask.substring(selectedTask.indexOf('-') + 1) + ";" + sHour + ":" + sMinute), ParticleEventVisibility.PRIVATE, 60);
                }
                else {
                    String[] coords = coordinates.split(" ");

                    String lon = coords[0];
                    String lat = coords[1];

                    String dataToStore = dataPhase1 + "`" + lon + "`" + lat + "~";

                    Intent i = new Intent(MainActivity.this, LocationCheck2.class);
                    i.putExtra("task", "add");
                    i.putExtra("data", dataToStore);
                    startService(i);
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

    private class registerTasks extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                ParticleCloudSDK.getCloud().publishEvent("register", "", ParticleEventVisibility.PRIVATE, 60);
            } catch (ParticleCloudException e) {
                e.printStackTrace();
            }

            return "wibbly wobbly timey wimey";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}

//just this once, everybody lives
