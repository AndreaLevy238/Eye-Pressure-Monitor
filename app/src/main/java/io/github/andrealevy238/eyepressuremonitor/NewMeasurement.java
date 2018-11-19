package io.github.andrealevy238.eyepressuremonitor;

import android.content.Context;
import android.os.Bundle;
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
    static final int BAUD = 38400;
    protected volatile byte[] cur;
    Button start, save;
    private Date date;
    private int numConnected_ = 0;
    private int ticks;
    private double freq;
    MeasurementViewModel model;
    public static double getFrequency(int clockticks) {
        return (clockticks * 8.0) / 499;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        freq = -1;
        model = new MeasurementViewModel(getApplication());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setStartButton();
        setSave();
    }

    private void setStartButton() {
        int BUFSIZE = 2;
        cur = new byte[BUFSIZE];
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] measured = cur;
                ticks = getNum(measured);
                displayPressure(ticks);
                displayTime();
                displayFreq(ticks);
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

        @Override
        protected void setup() throws ConnectionLostException {
            showVersions(ioio_, "IOIO connected!");
            uart_ = ioio_.openUart(new DigitalInput.Spec(rx), new DigitalOutput.Spec(tx), BAUD, Uart.Parity.NONE, Uart.StopBits.ONE);
            enableUi(true);
            try {
                Thread.sleep(500);
                in_ = uart_.getInputStream();
                Log.v("SetupUART", "sleep complete");
            } catch (InterruptedException e) {
                Log.e("Setup_Interrupted-UART", e.getMessage());
            }
        }

        @Override
        public void loop() {
            Log.d("UART", "new measurement starting");
            if (uart_ != null) {
                readUART();
            }
        }

        private void readUART() {
            byte[] raw = new byte[10];
            try {
                if (in_.available() > 0) {
                    int i = in_.read(raw);
                    Log.v("UART", "read complete, read " + String.valueOf(i) + " cur");
                } else {
                    Log.v("UART", "Read failed");
                    raw = null;
                }
            } catch (IOException e) {
                Log.e("UART_IO", e.getMessage());
                raw = null;
            }
            if (raw != null) {
                Log.d("UART-read", toHex(raw));
                cur[0] = raw[0];
                cur[1] = raw[1];
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

