package com.yso.mybranch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.yso.mybranch.R;
import com.yso.mybranch.utils.AnimationUtils;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity
{
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this);

        setContentView(R.layout.activity_splash);

        ImageView imageView = (ImageView)findViewById(R.id.fullscreen_content);
        AnimationUtils.slideUp(imageView);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
