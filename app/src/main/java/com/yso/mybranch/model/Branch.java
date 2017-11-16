package com.yso.mybranch.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Admin on 19-Oct-17.
 */

public class Branch
{
    private int id;
    private String name;
    private String manager;
    private String address;
    private LatLon latLon;

    public Branch()
    {
    }

    public Branch(int id, String name, String manager, String address, LatLon latLon)
    {
        super();
        this.id = id;
        this.name = name;
        this.manager = manager;
        this.address = address;
        this.latLon = latLon;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getManager()
    {
        return manager;
    }

    public void setManager(String manager)
    {
        this.manager = manager;
    }

    public LatLon getLatLon()
    {
        return latLon;
    }

    public void setLatLon(LatLon latLon)
    {
        this.latLon = latLon;
    }

    public static class LatLon
    {
        double latitude;
        double longitude;

        public LatLon()
        {
        }

        public LatLon(double latitude, double longitude)
        {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude()
        {
            return latitude;
        }

        public void setLatitude(double latitude)
        {
            this.latitude = latitude;
        }

        public double getLongitude()
        {
            return longitude;
        }

        public void setLongitude(double longitude)
        {
            this.longitude = longitude;
        }
    }

    @Override
    public String toString()
    {
        return "Branch [id=" + id + ", name=" + name + ", address=" + address + ", manager=" + manager + ", latLon=" + latLon.getLatitude() + "," + latLon.getLongitude() + "]";
    }
}
