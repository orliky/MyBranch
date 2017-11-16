package com.yso.mybranch.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.yso.mybranch.activity.LocationDialog;
import com.yso.mybranch.activity.MainActivity;
import com.yso.mybranch.managers.PersistenceManager;
import com.yso.mybranch.model.Branch;
import com.yso.mybranch.utils.Utils;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Admin on 05-Nov-17.
 */

@SuppressLint ("Registered")
public class MyService extends Service
{
    private static final String TAG = MyService.class.getSimpleName();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private boolean mIsShowing;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            //            sendMessageToActivity(location, "LocationUpdates", getBaseContext());

            if(!PersistenceManager.getInstance().isCheckedIn())
            {
//                Intent mainIntent = new Intent(MyService.this, LocationDialog.class);
//                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(mainIntent);

                if (Utils.getCloseBranch(mLastLocation) != null)
                {
                    Intent mainIntent = new Intent(MyService.this, LocationDialog.class);
                    LatLng latLng = new LatLng(mLastLocation.getAltitude(), mLastLocation.getLongitude());
                    HashMap<LatLng, Branch> hashMap = PersistenceManager.getInstance().getBranchMap();
                    mainIntent.putExtra("Branch", (Parcelable) hashMap.get(latLng));
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                }
                else
                {
//                    Snackbar.make(findViewById(android.R.id.content), "אינך קרוב לסניף", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

    }

    LocationListener[] mLocationListeners = new LocationListener[]{new LocationListener(LocationManager.GPS_PROVIDER), new LocationListener(LocationManager.NETWORK_PROVIDER)};

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");

        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        if (currentHour >8 && currentHour < 23)
        {
            initializeLocationManager();
            try
            {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[1]);
            } catch (java.lang.SecurityException ex)
            {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex)
            {
                Log.d(TAG, "network provider does not exist, " + ex.getMessage());
            }
            try
            {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);
            } catch (java.lang.SecurityException ex)
            {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex)
            {
                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            }
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null)
        {
            for (LocationListener mLocationListener : mLocationListeners)
            {
                try
                {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex)
                {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager()
    {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null)
        {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private static void sendMessageToActivity(Location location, String msg, Context context)
    {
        Intent intent = new Intent("GPSLocationUpdates");
        intent.putExtra("Status", msg);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Location", location);
        intent.putExtra("Location", bundle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /*public void showChangeLangDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setTitle("זיהוי כניסה");
        dialogBuilder.setMessage("הגעת לסניף כעת,\nאנא אשר תחילת פעילות");
        dialogBuilder.setView(dialogView);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        b.setCancelable(false);
        ImageButton vCheck = dialogView.findViewById(R.id.v_btn);
        vCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                b.dismiss();
                mIsShowing = false;
            }
        });

        TextView notThanks = dialogView.findViewById(R.id.n_btn);
        notThanks.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                b.dismiss();
                mIsShowing = false;
            }
        });

       *//* dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });*//*
        if (!mIsShowing)
        {
            b.show();
            mIsShowing = true;
        }
    }*/
}