package doctorwho.ethan.purplemorocco;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataStorage extends IntentService {
    public DataStorage() {
        super("DataStorage");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);

        String type = intent.getStringExtra("type");
        String action = intent.getStringExtra("action");
        String data = intent.getStringExtra("data");

        try {
            if (type.equals("time")) {
                if (action.equals("add")) {
                    List<String> dataComponents = Arrays.asList(data.split("•"));

                    String dateTimeKey = "com.doctorwho.ethan.times";
                    String full = dataComponents.get(0) + "~" + dataComponents.get(1) + "~" + dataComponents.get(2) + "`";
                    String locations = prefs.getString(dateTimeKey, "");
                    prefs.edit().putString(dateTimeKey, locations + full).apply();
                }
                else if (action.equals("remove")) {
                    List<String> dataComponents = Arrays.asList(data.split("~"));

                    String timeKey = "com.doctorwho.ethan.times";
                    String times = prefs.getString(timeKey, "");
                    //data += "`";
                    List<String> timeList = Arrays.asList(times.split("`"));


                    List<String> finalTimeList = new ArrayList<>();
                    for (int i = 0; i < timeList.size(); i++) {
                        finalTimeList.add(timeList.get(i));
                    }
                    finalTimeList.remove(data);


                    String newData = "";
                    for (String s : finalTimeList) {
                        newData += s + "`";
                    }

                    prefs.edit().putString(timeKey, newData).apply();
                }
            } else if (type.equals("location")) {
                if (action.equals("add")) {
                    String dateTimeKey = "com.doctorwho.ethan.geofences";
                    String locations = prefs.getString(dateTimeKey, "");
                    prefs.edit().putString(dateTimeKey, locations + data).apply();

                } else if (action.equals("remove")) {
                    String finalData = data.substring(data.indexOf(";") + 1);
                    String dateTimeKey = "com.doctorwho.ethan.geofences";
                    String locations = prefs.getString(dateTimeKey, "");
                    List<String> list = Arrays.asList(locations.split("~"));

                    String s = "";
                    for (String str : list) {
                        if (str.equals(finalData)) {
                        } else {
                            s += str + "~";
                        }
                    }

                    prefs.edit().putString(dateTimeKey, s).apply();
                } else if (action.equals("clear")) {
                    String dateTimeKey = "com.doctorwho.ethan.geofences";
                    prefs.edit().putString(dateTimeKey, "").apply();

                    String identifierKey = "com.doctorwho.ethan.identifiers";
                    prefs.edit().putString(identifierKey, "").apply();

                    String retiredIdentifierKey = "com.doctorwho.ethan.retiredidentifiers";
                    prefs.edit().putString(retiredIdentifierKey, "").apply();
                } else if (action.equals("disable")) {
                    String timeKey = "com.doctorwho.ethan.times";
                    String locationKey = "com.doctorwho.ethan.geofences";
                    String identifierKey = "com.doctorwho.ethan.retiredidentifiers";
                    String times = prefs.getString(timeKey, "");
                    String locations = prefs.getString(locationKey, "");
                    String identifiers = prefs.getString(identifierKey, "");

                    List<String> components = Arrays.asList(data.split("`"));
                    String identifier = components.get(0);
                    identifiers += identifier + "-";
                    prefs.edit().putString(identifierKey, identifiers);
                }
            }
            else if (type.equals("log")) {
                if (action.equals("append")) {

                }
                else if (action.equals("clear")) {
                    
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
