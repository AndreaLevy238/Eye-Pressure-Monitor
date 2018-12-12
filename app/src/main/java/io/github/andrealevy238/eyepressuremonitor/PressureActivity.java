package io.github.andrealevy238.eyepressuremonitor;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PressureActivity extends AppCompatActivity {
    private MeasurementViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNav();
        model = ViewModelProviders.of(this).get(MeasurementViewModel.class);
        GraphView pGraph = findViewById(R.id.pressureGraph);
        DataPoint[] data = getMeasurements();
        graph(pGraph, data);
    }

    /**
     * Creates the Graph for this activity which is all the raw measurements in the last 6 months
     *
     * @param graphView  the view for the graph
     * @param dataPoints the data in the graph
     */
    private void graph(GraphView graphView, DataPoint[] dataPoints) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        String pattern;
        if (series.isEmpty()) {
            return;
        }
        graphView.addSeries(series);
        if (dataPoints.length < 2) {
            pattern = "hh:mm:ss";
            graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        } else {
            pattern = "MM/dd";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
        DateAsXAxisLabelFormatter d = new DateAsXAxisLabelFormatter(getApplicationContext(), simpleDateFormat);
        graphView.getGridLabelRenderer().setLabelFormatter(d);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalableY(true);
        graphView.getViewport().setScrollableY(true);
    }

    /**
     * @return a list of all the raw measurements from the last 6 months
     */
    private DataPoint[] getMeasurements() {
        List<Measurement> measurements = model.getMeasurements().getValue();
        if (measurements == null) {
            Log.e("Null Measurements", "measurements are null");
            DataPoint[] pressures = new DataPoint[1];
            pressures[0] = new DataPoint(Calendar.getInstance().getTime(), 0);
            return pressures;
        }
        int size = measurements.size();
        DataPoint[] pressures = new DataPoint[size];
        for (int i = 0; i < size; i++) {
            Measurement m = measurements.get(i);
            pressures[i] = new DataPoint(m.time, m.pressure);
        }
        return pressures;
    }

    /**
     * Sets the navigation between activities
     */
    private void setNav() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);


                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        startNewActivity(menuItem);
                        return true;
                    }
                });
    }

    /**
     * Starts a new activity based on the MenuItem selected
     * @param menuItem the menu item selected
     */
    private void startNewActivity(MenuItem menuItem) {
        Intent intent = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_new_measurement:
                intent = new Intent(this, NewMeasurement.class);
                break;
            case R.id.nav_frequency:
                intent = new Intent(this, FrequencyActivity.class);
                break;
            case R.id.nav_pressure:
                intent = new Intent(this, PressureActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (upIntent == null) {
                    return false;
                } else if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
