package io.github.AndreaLevy238;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOLooperProvider;
import ioio.lib.util.android.IOIOAndroidApplicationHelper;

public abstract class AppCompactIOIOActivity extends AppCompatActivity implements IOIOLooperProvider {
    private final IOIOAndroidApplicationHelper helper_ = new IOIOAndroidApplicationHelper(this, this);

    public AppCompactIOIOActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.helper_.create();
    }

    protected void onDestroy() {
        this.helper_.destroy();
        super.onDestroy();
    }

    protected void onStart() {
        super.onStart();
        this.helper_.start();
    }

    protected void onStop() {
        this.helper_.stop();
        super.onStop();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if((intent.getFlags() & 268435456) != 0) {
            this.helper_.restart();
        }

    }

    protected IOIOLooper createIOIOLooper() {
        throw new RuntimeException("Client must override one of the createIOIOLooper overloads!");
    }

    public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
        return this.createIOIOLooper();
    }
}
