package doctorwho.ethan.purplemorocco;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;

public class IntentReceiver extends BroadcastReceiver {
    public IntentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, TimeCheck.class);
        i.putExtra("task", "run");
        //context.startService(i);
    }
}

