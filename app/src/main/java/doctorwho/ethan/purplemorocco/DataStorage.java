package doctorwho.ethan.purplemorocco;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

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

        if (type.equals("time")) {
            if (action.equals("add")) {
                List<String> dataComponents = Arrays.asList(data.split("•"));

                String dateTimeKey = "com.doctorwho.ethan.times";
                String full = dataComponents.get(0) + "~" + dataComponents.get(1) + "~" + dataComponents.get(2) + "`";
                String locations = prefs.getString(dateTimeKey, "");
                prefs.edit().putString(dateTimeKey, locations + full).apply();
            }
        }
        else if (type.equals("location")) {
            if (action.equals("add")) {
                String dateTimeKey = "com.doctorwho.ethan.geofences";
                String locations = prefs.getString(dateTimeKey, "");
                prefs.edit().putString(dateTimeKey, locations + data).apply();

            }
            else if (action.equals("remove")) {
                String finalData = data.substring(data.indexOf(";") + 1);
                String dateTimeKey = "com.doctorwho.ethan.geofences";
                String locations = prefs.getString(dateTimeKey, "");
                List<String> list = Arrays.asList(locations.split("~"));

                String s = "";
                for (String str : list) {
                    if (str.equals(finalData)) { }
                    else {
                        s += str + "~";
                    }
                }

                prefs.edit().putString(dateTimeKey, s).apply();
            }
            else if(action.equals("clear")) {
                String dateTimeKey = "com.doctorwho.ethan.geofences";
                prefs.edit().putString(dateTimeKey, "").apply();

                String identifierKey = "com.doctorwho.ethan.identifiers";
                prefs.edit().putString(identifierKey, "").apply();

                String retiredIdentifierKey = "com.doctorwho.ethan.retiredidentifiers";
                prefs.edit().putString(retiredIdentifierKey, "").apply();
            }
        }
    }
}
