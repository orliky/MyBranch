package com.yso.mybranch.managers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yso.mybranch.model.Branch;
import com.yso.mybranch.model.User;
import com.yso.mybranch.utils.SecurePreferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class PersistenceManager
{

    private static final String LOG_TAG = PersistenceManager.class.getSimpleName();

    private static final String PREF_LOGGED_IN = "pref.LOGGED_IN";
    private static final String PREF_CHECK_IN = "pref.CHECK_IN";
    private static final String PREF_USER = "pref.USER";
    private static final String PREF_BRANCHES_MAP = "pref.BRANCHES_MAP";

    private static PersistenceManager msInstance;
    private Gson mGson;

    public static PersistenceManager initInstance(Context context)
    {
        if (msInstance == null)
        {
            msInstance = new PersistenceManager(context);
        }
        return msInstance;
    }

    public static PersistenceManager getInstance()
    {
        if (msInstance == null)
        {
            Log.e(LOG_TAG, "getInstance(), fail, PersistenceManager is not init");
        }
        return msInstance;
    }

    private PersistenceManager(Context context)
    {
        SecurePreferences.initInstance(context);
        mGson = new Gson();
    }

    public void setIsLoggedIn(boolean selected)
    {
        SecurePreferences.getInstance().setBoolean(PREF_LOGGED_IN, selected);
    }

    public boolean isLoggedIn()
    {
        return SecurePreferences.getInstance().getBoolean(PREF_LOGGED_IN, false);
    }

    public void setIsCheckedIn(boolean selected)
    {
        SecurePreferences.getInstance().setBoolean(PREF_CHECK_IN, selected);
    }

    public boolean isCheckedIn()
    {
        return SecurePreferences.getInstance().getBoolean(PREF_CHECK_IN, false);
    }

    public void setUser(User user)
    {
        SecurePreferences.getInstance().setString(PREF_USER,  mGson.toJson(user));
    }

    public User getUser()
    {
        String userString = SecurePreferences.getInstance().getString(PREF_USER, null);
        return mGson.fromJson(userString, User.class);
    }

    public void setBranchMap(HashMap<LatLng, Branch> hashMap)
    {
        SecurePreferences.getInstance().setString(PREF_BRANCHES_MAP, mGson.toJson(hashMap));
    }

    public HashMap<LatLng, Branch> getBranchMap()
    {
        String branchMapDataString = SecurePreferences.getInstance().getString(PREF_BRANCHES_MAP, mGson.toJson(new ArrayList<>()));
        Type listType = new TypeToken<HashMap<LatLng, Branch>>() {
        }.getType();

        return mGson.fromJson(branchMapDataString, listType);
    }
}
