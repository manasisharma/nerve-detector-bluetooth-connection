package com.example.manasisharma.nim40;

/**
 * Created by manasisharma on 8/19/15.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import java.lang.ref.WeakReference;


public abstract class BaseActivity extends Activity {

    protected static final boolean DEBUG = false;
    private static final String TAG = "NimAlerts-BaseActivity";

    protected AudioManager mAudioManager;
    protected static BluetoothAdapter mBluetoothAdapter;
    protected static BluetoothSocket mBluetoothSocket;

    public static class IncomingHandler extends Handler {
        private final WeakReference<BaseActivity> mActivity;

        public IncomingHandler(BaseActivity activity)
        {
            super(Looper.getMainLooper()); // Guarantee that we're on UI thread
            mActivity = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            //if (DEBUG) Log.d(TAG, "Received message: " + msg + " arg1: " + msg.arg1);
            BaseActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    protected abstract void handleMessage(Message msg);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager == null)
        {
            Log.e(TAG, "Failed to get reference to audio manager!");
            finish();
            return;
        }

    }

    protected void playSound(int glassSound)
    {
        mAudioManager.playSoundEffect(glassSound);
    }


}

