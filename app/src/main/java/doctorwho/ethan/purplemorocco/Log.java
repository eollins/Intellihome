package doctorwho.ethan.purplemorocco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Log extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        ListView myListView = (ListView) findViewById(R.id.listView1);
        SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
        String dateTimeKey = "com.doctorwho.ethan.log";
        String current = prefs.getString(dateTimeKey, "");

        List<String> bub = Arrays.asList(current.split("~"));
        List<String> fruits_list = new ArrayList<>();
        for (int i = bub.size() - 1; i >= 0; i--) {
            fruits_list.add(bub.get(i));
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, fruits_list);

        // DataBind ListView with items from ArrayAdapter
        myListView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log, menu);
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
            Intent i = new Intent(Log.this, DataStorage.class);
            i.putExtra("type", "log");
            i.putExtra("action", "clear");
            startService(i);

            finish();
            startActivity(getIntent());
        }

        return super.onOptionsItemSelected(item);
    }
}
