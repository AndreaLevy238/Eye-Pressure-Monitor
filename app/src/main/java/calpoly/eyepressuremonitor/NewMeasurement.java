package calpoly.eyepressuremonitor;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.SpiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;

public class NewMeasurement extends AppCompactIOIOActivity {
    private Date date;
    boolean isLight;
    Button start;
    /** SPI settings. */
    private SpiMaster spi;
    static final int misoPin = 35;
    static final int mosiPin = 36;
    static final int clkPin = 37;
//    int[] ssPins = new int[] { 4, 5, 6, 7, 8 };
    int ssPin = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isLight = false;
        start = findViewById(R.id.newMeasurement);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    newMeasurement();
                }
                catch (ConnectionLostException e) {
                    Log.e("NewMeasurement", "Connection Lost Exception");
                }
                catch (InterruptedException e) {
                    Log.e("NewMeasurement", "Interrupted Exception");
                }

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

    public void newMeasurement() throws ConnectionLostException, InterruptedException {
        byte[] request = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        byte[] response = new byte[4];
        spi.writeRead(0, request, request.length, 7, response, response.length);
        int rawFreq = getNum(response);
        displayPressure(rawFreq);
        displayTime();
        displayFreq(rawFreq);
        isLight = !isLight;
        Log.d("LED-newMeasurement", String.valueOf(isLight));
    }

    /**
     * @param bytes a byte array that specifies some integer
     * @return an iteger represented by the bytes
     */
    private int getNum(byte[] bytes) {
        return bytes[3] & 0xFF | (bytes[2] & 0xFF) << 8 | (bytes[1] & 0xFF) << 16 | (bytes[0] & 0xFF) << 24;
    }

    public void displayTime() {
        TextView timeView = findViewById(R.id.time);
        timeView.setText(getDateString());
        timeView.setVisibility(View.VISIBLE);
    }

    public void displayPressure(int pressure) {
        TextView textView = findViewById(R.id.pressure);
//        DecimalFormat df = new DecimalFormat("#.##");
//        textView.setText(df.format(pressure));
        textView.setText(pressure);
        textView.setVisibility(View.VISIBLE);
    }


    public void displayFreq(int num) {
        TextView textView = findViewById(R.id.raw);
//        DecimalFormat df = new DecimalFormat("#.###");
////        textView.setText(df.format(raw));
        textView.setText(num);
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
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }


    class Looper extends BaseIOIOLooper {
        private DigitalOutput led_;

        @Override
        protected void setup() throws ConnectionLostException {
            showVersions(ioio_, "IOIO connected!");
            spi = ioio_.openSpiMaster(new DigitalInput.Spec(misoPin,
                            DigitalInput.Spec.Mode.PULL_DOWN), new DigitalOutput.Spec(mosiPin),
                    new DigitalOutput.Spec(clkPin),
                    new DigitalOutput.Spec[] { new DigitalOutput.Spec(ssPin) },
                    new SpiMaster.Config(SpiMaster.Rate.RATE_1M, true, true));
            enableUi(true);
        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException {
            led_.write(isLight);
            Log.d("LED", String.valueOf(isLight));
            Thread.sleep(100);
        }

        @Override
        public void disconnected() {
            enableUi(false);
            toast("IOIO disconnected");
        }

        @Override
        public void incompatible() {
            showVersions(ioio_, "Incompatible firmware version!");
        }


    }

    private int numConnected_ = 0;

    private void enableUi(final boolean enable) {
        // This is slightly trickier than expected to support a multi-IOIO use-case.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enable) {
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
