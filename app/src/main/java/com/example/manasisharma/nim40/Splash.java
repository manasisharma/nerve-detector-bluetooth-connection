package com.example.manasisharma.nim40;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by manasisharma on 8/20/15.
 */
public class Splash extends Activity {

    private boolean backbtnPress;
    private static final int SPLASH_DURATION = 2000;
    private Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                if(!backbtnPress)
                {
                    Intent intent = new Intent(Splash.this,Home.class);
                    Splash.this.startActivity(intent);
                }
            }
        }, SPLASH_DURATION);
    }

    public void onBackPressed()
    {
        backbtnPress = true;
        super.onBackPressed();
    }

 }
