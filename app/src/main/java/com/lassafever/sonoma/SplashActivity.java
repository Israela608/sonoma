package com.lassafever.sonoma;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.logging.LogRecord;

public class SplashActivity extends Activity {
    //Handler handler;
    //ImageView imageSonama;
    //TextView textSonoma;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.splash_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//always portrait



        /*imageSonama = findViewById(R.id.image_sonoma);
        textSonoma = findViewById(R.id.text_sonoma);
        imageSonama.setScaleX(0.7f);
        imageSonama.setScaleY(0.7f);
        textSonoma.setTranslationY(500);

        textSonoma.animate().translationYBy(-500).setDuration(300);
        imageSonama.animate().alpha(1).scaleX(1).scaleY(1).setDuration(500);*/

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

        /*handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);*/
    }
}
