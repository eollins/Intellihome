package doctorwho.ethan.purplemorocco;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }

    public void viewTasks(View v) {
        Intent i = new Intent(Options.this, SecondActivity.class);
        startActivity(i);
    }

    public void myBoards(View v) {
        Intent i = new Intent(Options.this, Boards.class);
        startActivity(i);
    }
}
