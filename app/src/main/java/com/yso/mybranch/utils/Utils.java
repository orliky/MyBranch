package com.yso.mybranch.utils;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;
import com.yso.mybranch.interfaces.DatabaseRefCallBack;
import com.yso.mybranch.managers.DatabaseReferenceManager;
import com.yso.mybranch.managers.PersistenceManager;
import com.yso.mybranch.model.Branch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 15-Nov-17.
 */

public final class Utils
{
    private static Branch mBranch;

    public static void closeKeyboard(Context context, View view)
    {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static Branch getCloseBranch(final Location location)
    {
        mBranch = null;
        DatabaseReferenceManager.getAllBranches(new DatabaseRefCallBack()
        {
            @Override
            public void onGetBranches(List branches)
            {
                for (Location location1 : getAllLocations((ArrayList<Branch>) branches))
                {
                    if (location.distanceTo(location1) < 40)
                    {
                        LatLng latLng = new LatLng(location1.getAltitude(), location1.getLongitude());
                        mBranch = PersistenceManager.getInstance().getBranchMap().get(latLng);
                    }
                }
            }
        });
        return mBranch;
    }

    private static ArrayList<Location> getAllLocations(ArrayList<Branch> branches)
    {
        ArrayList<Branch> branchArrayList = branches;
        ArrayList<Location> branchesLoc = new ArrayList<>();
        for (Branch branch : branchArrayList)
        {
            Location loc = new Location("loc");
            loc.setLatitude(branch.getLatLon().getLatitude());
            loc.setLongitude(branch.getLatLon().getLongitude());
            branchesLoc.add(loc);
        }
        return branchesLoc;
    }
}
