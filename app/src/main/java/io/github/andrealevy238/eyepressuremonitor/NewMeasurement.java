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

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;

public class NewMeasurement extends AppCompactIOIOActivity {
    static final int rx = 3;
    static final int tx = 4;
    final int BUFSIZE = 2;
    volatile byte[] bytes;
    Button start;
    private Date date;
    private int numConnected_ = 0;

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    /**
     * @param bytes a byte array that specifies some integer
     * @return an iteger represented by the bytes
     */
    public static int getNum(byte[] bytes) {
        return bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bytes = new byte[BUFSIZE];
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] raw = bytes;
                int ticks = getNum(raw);
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

    public double getFrequency(int clockticks) {
        return (clockticks * 8.0) / 499;
    }

    public void displayTime() {
        TextView timeView = findViewById(R.id.time);
        timeView.setText(getDateString());
        timeView.setVisibility(View.VISIBLE);
    }

    public void displayPressure(int raw) {
        TextView textView = findViewById(R.id.pressure);
        textView.setText(String.valueOf(raw));
        textView.setVisibility(View.VISIBLE);
    }

    public void displayFreq(int num) {
        TextView textView = findViewById(R.id.raw);
        double freq = getFrequency(num);
        DecimalFormat df = new DecimalFormat("#.###");
        textView.setText(df.format(freq));
        textView.setVisibility(View.VISIBLE);
    }

    public String getDateString() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        date = new GregorianCalendar().getTime();
        return dateFormat.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
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
        private DigitalOutput led_;
        private Uart uart_;
        private InputStream in_;

        @Override
        protected void setup() throws ConnectionLostException {
            showVersions(ioio_, "IOIO connected!");
            int baud = 34800;
            led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, false);
            uart_ = ioio_.openUart(rx, tx, baud, Uart.Parity.NONE, Uart.StopBits.ONE);
            in_ = uart_.getInputStream();
            enableUi(true);
        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException {
            Log.d("UART", "new measurement starting");
            led_.write(true);
            byte[] raw = new byte[BUFSIZE];
            Thread.sleep(500);
            try {
                Log.d("UART", "attempting to read...");
                int i = in_.read(raw);
                if (i == -1) {
                    Log.i("readUART", "No data read");
                    raw = null;
                }
            } catch (IOException e) {
                Log.e("UART_IO", e.getMessage());
                raw = null;
            }
            if (raw != null) {
                Log.d("UART-read", toHex(raw));
                bytes = raw;
            }
            led_.write(false);
            Thread.sleep(10);
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

