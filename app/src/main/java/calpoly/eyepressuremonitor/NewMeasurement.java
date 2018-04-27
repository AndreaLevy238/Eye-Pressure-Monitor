package calpoly.eyepressuremonitor;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class NewMeasurement extends AppCompatActivity {
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button start = findViewById(R.id.newMeasurement);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                newMeasurement();
                view.setEnabled(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_measurement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void newMeasurement() {
        displayPressure(20.002);
        displayTime();
        displayFreq(35.555);
    }

    public void displayTime() {
        TextView timeView = findViewById(R.id.time);
        timeView.setText(getDateString());
        timeView.setVisibility(View.VISIBLE);
    }

    public void displayPressure(double pressure) {
        TextView textView = findViewById(R.id.pressure);
        DecimalFormat df = new DecimalFormat("#.##");
        textView.setText(df.format(pressure));
        textView.setVisibility(View.VISIBLE);
    }

    public void displayFreq(double raw) {
        TextView textView = findViewById(R.id.raw);
        DecimalFormat df = new DecimalFormat("#.###");
        textView.setText(df.format(raw));
        textView.setVisibility(View.VISIBLE);
    }

    public String getDateString() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        date = new GregorianCalendar().getTime();
        return dateFormat.format(date);
    }

    public String ASCII_date() {
        String pattern = "yyyy-MM-dd HH:mm:SS";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
