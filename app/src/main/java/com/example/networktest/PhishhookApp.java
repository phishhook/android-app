package com.example.networktest;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

public class PhishhookApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerLinkClickReceiver();
        // Initialize your application-wide components here
    }

    private void registerLinkClickReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_VIEW);
        intentFilter.addCategory(Intent.CATEGORY_BROWSABLE);
        registerReceiver(new LinkClickReceiver(), intentFilter);
    }
}
