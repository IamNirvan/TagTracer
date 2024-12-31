package com.tagtracer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.tagtracer.fragments.loading.LoadingFragmentV2;
import com.tagtracer.rfid.IRFIDReader;
import com.tagtracer.rfid.RFIDReaderFactory;
import com.tagtracer.util.BuildInfo;
import com.tagtracer.util.IDisposable;
import com.tagtracer.util.callbacks.IKeyEventCallback;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements IDisposable {
    public static final String TAG = MainActivity.class.getName();
    private IRFIDReader reader;
    private IKeyEventCallback keyEventCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the reader instance
        try {
            this.initializeReader();
        } catch (Exception e) {
            Toast.makeText(
                this,
                String.format("device manufacturer %s is not supported", BuildInfo.getManufacturer()),
                Toast.LENGTH_LONG
            ).show();
        }

        // Load the loading fragment
        this.loadFragment(new LoadingFragmentV2(), false, LoadingFragmentV2.TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.dispose();
    }
    //
    // Start of key event handlers
    //
    public void setKeyEventCallback(@NotNull IKeyEventCallback callback) {
        this.keyEventCallback = callback;
    }

    public void clearEventCallbacks() {
        this.keyEventCallback = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Handle the on key press event for each fragment accordingly...
        if (keyCode == 294 || keyCode == 293) {
            if (this.keyEventCallback != null) {
                this.keyEventCallback.onKeyDown();
            } else {
                Log.d(TAG, "onKeyDown: no overridden onKeyDown implementation found");
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 294 || keyCode == 293) {
            if (this.keyEventCallback != null) {
                this.keyEventCallback.onKeyUp();
            } else {
                Log.d(TAG, "onKeyUp: no overridden onKeyUp implementation found");
            }
        }
        return super.onKeyUp(keyCode, event);
    }
    //
    // Start of general utils
    //
    /**
     * This utility method allows fragments to be replaced and the
     * app's backstack to be managed
     * */
    public void loadFragment(@NotNull Fragment fragment, boolean addToBackStack, String name) {
        FragmentManager fManager = getSupportFragmentManager();

        if (!addToBackStack) {
            fManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            fManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(name)
                    .commit();
        }
    }

    /**
     * Extracts the device manufacturer and initializes a reader instance that is suitable
     * for the device
     * */
    private void initializeReader() throws Exception {
        if (this.reader == null) {
            final String manufacturer = BuildInfo.getManufacturer();
            this.reader = RFIDReaderFactory.getInstance().getRfidReader(manufacturer, getApplicationContext());
        } else {
            Log.d(TAG, "initializeReader: reader already initialized");
        }
    }

    public IRFIDReader getReader() {
        return this.reader;
    }

    @Override
    public void dispose() {
        if (this.reader != null) {
            this.reader.dispose();
        }

        this.clearEventCallbacks();
    }

}