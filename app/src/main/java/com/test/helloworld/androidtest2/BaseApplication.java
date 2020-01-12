package com.test.helloworld.androidtest2;

import android.app.Application;
import android.os.StrictMode;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
}
