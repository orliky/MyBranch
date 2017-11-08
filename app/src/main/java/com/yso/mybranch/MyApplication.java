package com.yso.mybranch;

import android.app.Application;
import android.content.Context;

import com.yso.mybranch.managers.PersistenceManager;

/**
 * Created by Admin on 16-Oct-17.
 */

public class MyApplication extends Application
{

    private static Context msApplicationContext;

    @Override
    public void onCreate()
    {
        super.onCreate();

        msApplicationContext = getApplicationContext();
        PersistenceManager.initInstance(msApplicationContext);

    }

    public static Context getAppContext()
    {
        return msApplicationContext;
    }
}
