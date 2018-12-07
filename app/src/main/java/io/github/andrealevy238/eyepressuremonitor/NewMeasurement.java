package io.github.andrealevy238.eyepressuremonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;

import static io.github.andrealevy238.eyepressuremonitor.DataConverter.getNum;
import static io.github.andrealevy238.eyepressuremonitor.DataConverter.toHex;

public class NewMeasurement extends AppCompactIOIOActivity {
    protected static final int rx = 3;
    static final int tx = 4;
    static final int cts = 11;
    static final int rts = 10;
    static final int BAUD = 38400;
    protected volatile byte[] cur;
    Button start, save;
    private Date date;
    private int numConnected_ = 0;
    private int ticks;
    private double freq;
    MeasurementViewModel model;
    private DrawerLayout mDrawerLayout;

    public static double getFrequency(int clockTicks) {
        return (clockTicks * 8.0) / 499;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        freq = -1;
        setNav();
        setDrawer();
        model = new MeasurementViewModel(getApplication());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setStartButton();
        setSave();

    }

    private void setDrawer() {
        mDrawerLayout = findViewById(R.id.drawerLayoutNewMeasurement);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    private void setNav() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        //open activities for new types of measurements
                        startNewActivity(menuItem);
                        return true;
                    }
                });
    }

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
            case R.id.nav_frequency_today:
                intent = new Intent(this, FrequencyToday.class);
                break;
            case R.id.nav_pressure_today:
                intent = new Intent(this, PressureToday.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }



    private void setStartButton() {
        int BUFSIZE = 2;
        cur = new byte[BUFSIZE];
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numConnected_ > 0) {
                    byte[] measured = cur;
                    ticks = getNum(measured);
                    displayPressure(ticks);
                    displayTime();
                    displayFreq(ticks);
                } else {
                    toast("Nothing connected!");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSave() {
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (freq < 0) {
                    toast("No measurement gotten! Failed to save");
                } else {
                    Measurement measurement = new Measurement(freq, ticks, date);
                    model.insert(measurement);
                    toast("Saved!");
                }
            }
        });
    }

    public void displayTime() {
        TextView timeView = findViewById(R.id.time);
        date = new GregorianCalendar().getTime();
        timeView.setText(getDateString());
        timeView.setVisibility(View.VISIBLE);
    }

    public void displayPressure(int raw) {
        TextView textView = findViewById(R.id.pressure);
        textView.setText(String.valueOf(raw));
        textView.setVisibility(View.VISIBLE);
    }

    public void displayFreq(int num) {
        TextView textView = findViewById(R.id.frequency);
        freq = getFrequency(num);
        DecimalFormat df = new DecimalFormat("#.###");
        textView.setText(df.format(freq));
        textView.setVisibility(View.VISIBLE);
    }

    public String getDateString() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        return dateFormat.format(date);
    }

    public Date getDate() {
        return date;
    }

    private void showVersions(IOIO ioio, String title) {
        toast(String.format("%s\n" +
                        "IOIOLib: %s\n" +
                        "Application firmware: %s\n" +
                        "Bootloader firmware: %s\n" +
                        "Hardware: %s",
                title,
                ioio.getImplVersion(IOIO.VersionType.IOIOLIB_VER),
                ioio.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER),
                ioio.getImplVersion(IOIO.VersionType.BOOTLOADER_VER),
                ioio.getImplVersion(IOIO.VersionType.HARDWARE_VER)));
    }

    private void toast(final String message) {
        final Context context = this.getApplicationContext();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enableUi(boolean enable) {
        // This is slightly trickier than expected to support a multi-IOIO use-case.
        final boolean enabled = enable;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enabled) {
                    if (numConnected_++ == 0) {
                        Log.d("CONNECT", "First item connected");
                    }
                } else {
                    if (--numConnected_ == 0) {
                        Log.e("DISCONNECT", "Not connected");
                    }
                }
            }
        });

    }

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new Looper();
    }

    class Looper extends BaseIOIOLooper {
        private Uart uart_;
        private InputStream in_;
        private DigitalOutput led;
        @Override
        protected void setup() throws ConnectionLostException {
            showVersions(ioio_, "IOIO connected!");
            uart_ = ioio_.openUart(new DigitalInput.Spec(rx), new DigitalOutput.Spec(tx), BAUD, Uart.Parity.NONE, Uart.StopBits.ONE);
            led = ioio_.openDigitalOutput(IOIO.LED_PIN);
            enableUi(true);
            led.write(true);
            in_ = uart_.getInputStream();
            Log.v("SetupUART", "sleep complete");
        }

        @Override
        public void loop() {
            Log.v("UART", "new measurement starting");
            if (uart_ != null) {
                try {
                    readUART();
                } catch (Exception e) {
                    Log.e("UART_Exception", e.getMessage());
                }
            }
        }

        private void readUART() throws ConnectionLostException {
            byte[] raw = new byte[10];
            int i = -1;
            try {
                led.write(false);
                i = in_.read(raw);
                led.write(true);
                Log.d("UART", "read complete, read " + String.valueOf(i) + " bytes");
            } catch (IOException e) {
                Log.e("UART_IO", e.getMessage());
                raw = null;
            }
            if (raw != null && i > 1) {
                Log.d("UART-read", toHex(raw));
                int b0 = i % 2;
                cur[0] = raw[b0];
                cur[1] = raw[b0 + 1];
            }
        }

        @Override
        public void disconnected() {
            enableUi(false);
            uart_.close();
            toast("IOIO disconnected");
        }

        @Override
        public void incompatible() {
            showVersions(ioio_, "Incompatible firmware version!");
        }

    }


}

