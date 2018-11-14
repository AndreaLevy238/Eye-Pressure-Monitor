package io.github.AndreaLevy238;

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
import java.io.OutputStream;
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
    private Date date;
    boolean waiting, complete;
    Button start;
    static final int rx = 3;
    static final int tx = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        waiting = false;
        complete = false;
        start = findViewById(R.id.newMeasurement);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waiting = true;
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

    private String toHex(byte[] bytes) {
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
    private int getNum(byte[] bytes) {
        return bytes[1] & 0xFF | (bytes[0] & 0xFF) << 8;
    }

    public void displayTime() {
        TextView timeView = findViewById(R.id.time);
        timeView.setText(getDateString());
        timeView.setVisibility(View.VISIBLE);
    }

    public void displayPressure(int raw) {
        TextView textView = findViewById(R.id.pressure);
        textView.setText(raw);
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


    class Looper extends BaseIOIOLooper {
        private DigitalOutput led_;
        private Uart uart_;
        private OutputStream out_;
        private InputStream in_;
        @Override
        protected void setup() throws ConnectionLostException {
            showVersions(ioio_, "IOIO connected!");
            int baud = 34800;
            led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, false);
            uart_ = ioio_.openUart(rx, tx, baud, Uart.Parity.NONE, Uart.StopBits.ONE);
            in_ = uart_.getInputStream();
            out_ = uart_.getOutputStream();
            enableUi(true);
        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException {
            if (waiting) {
                Log.d("UART", "new measurement starting");
                led_.write(true);
                try {
                    byte[] raw = readUart();
                    if (raw != null) {
                        Log.d("UART-newMeasurement", toHex(raw));
                        int ticks = getNum(raw);
                        displayPressure(ticks);
                        displayTime();
                        displayFreq(ticks);
                    }
                }
                catch (IOException e) {
                    Log.e("UART_IO", e.getMessage());
                }
                waiting = false;
                led_.write(false);
            }
            else {
                byte[] one = { 0};
                try {
                    out_.write(one);
                }
                catch (IOException e) {
                    Log.e("UART_IO", e.getMessage());
                }
            }
            Thread.sleep(100);
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


        private byte[] readUart() throws IOException, InterruptedException{
            byte[] bytes = new byte[2];
            if (uart_ == null) {
                Log.e("readUART","UART is null");
                return null;
            }
            byte[] one = { 0x7E}; //request data
            out_.write(one);
            Thread.sleep(500);
            int i = in_.read(bytes);
            if (i == -1) {
                Log.e("readUART", "No data read");
                return null;
            }
            if (i != 2) {
                Log.e("readUART", "Read incomplete");
                return null;
            }
            return bytes;
        }


    }

    private int numConnected_ = 0;

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



}
