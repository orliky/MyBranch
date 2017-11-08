package com.yso.mybranch.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.yso.mybranch.R;
import com.yso.mybranch.fragment.AddBranch;
import com.yso.mybranch.fragment.BranchFragment;
import com.yso.mybranch.managers.PersistenceManager;
import com.yso.mybranch.model.Branch;
import com.yso.mybranch.service.MyService;
import com.yso.mybranch.utils.LocationDialog;
import com.yso.mybranch.utils.SecurePreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, BaseActivity.GoogleConnectionCallback
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap mMap;
    private Marker mMarker;
    private View mapFrag;
    private Bitmap mMarkerBlueIcon;
    private Bitmap mMarkerRedIcon;
    private Location mLocation;
    private LocationManager mLocationManager;
    private String mProvider;
    private List<Branch> myBranches;
    private HashMap<LatLng, Branch> mDataHashMap;
    private FrameLayout mFragContainer;
    private boolean mIsShowing;
    PubNub pubnub;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListener(this);
        //////////////////////
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(getString(R.string.com_pubnub_subscribeKey));
        pnConfiguration.setPublishKey(getString(R.string.com_pubnub_publishKey));
        pnConfiguration.setSecretKey(getString(R.string.com_pubnub_secscribeKey));
        pnConfiguration.setSecure(false);

        pubnub = new PubNub(pnConfiguration);

        pubnub.subscribe().channels(Arrays.asList("ch1")) // subscribe to channels
                .execute();

        pubnub.addPushNotificationsOnChannels().pushType(PNPushType.GCM).channels(Arrays.asList("ch1", "ch2", "ch3")).deviceId("googleDevice").async(new PNCallback<PNPushAddChannelResult>()
        {
            @Override
            public void onResponse(PNPushAddChannelResult result, PNStatus status)
            {
                Snackbar.make(findViewById(android.R.id.content), "your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        //////////////////
        setTitle("מפת סניפים");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mFragContainer = (FrameLayout) findViewById(R.id.fragment_container);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                drawer.closeDrawers();
                item.setChecked(true);
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        mFragContainer.setVisibility(View.GONE);
                        getSupportFragmentManager().popBackStack();
                        setTitle("מפת סניפים");
                        break;

                    case R.id.nav_add_branch:
                        mFragContainer.setVisibility(View.VISIBLE);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.add(R.id.fragment_container, new AddBranch());
                        ft.addToBackStack(null);
                        ft.commit();
                        setTitle("הוספת סניף");
                        break;

                    case R.id.nav_test:

                        break;

                    case R.id.nav_logout:
                        googleRevokeAccess();
                        break;
                }
                return true;
            }
        });

        myBranches = new ArrayList<>();

        mDataHashMap = new HashMap<>();

        Environment.getExternalStorageDirectory();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mProvider = mLocationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLocation = /*mLocationManager.getLastKnownLocation(mProvider)*/getLastKnownLocation();
        ///////////////
        sendLocPush(pubnub);
        ///////////////
        mapFrag = findViewById(R.id.map);

        mMarkerBlueIcon = resizeMapIcons("pin_blue", 120, 120);
        mMarkerRedIcon = resizeMapIcons("pin_red", 120, 120);

        startService(new Intent(this, MyService.class));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
    }

    private void sendLocPush(PubNub pubnub)
    {
        if(mLocation != null)
        {
            JsonObject position = new JsonObject();
            position.addProperty("lat", mLocation.getLatitude());
            position.addProperty("lng", mLocation.getLongitude());

            System.out.println("before pub: " + position);
            pubnub.publish().message(position).channel("ch1").async(new PNCallback<PNPublishResult>()
            {
                @Override
                public void onResponse(PNPublishResult result, PNStatus status)
                {
                    if (!status.isError())
                    {
                        System.out.println("pub timetoken: " + result.getTimetoken());
                    }
                    System.out.println("pub status code: " + status.getStatusCode());
                }
            });
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("Status");
            Bundle bundle = intent.getBundleExtra("Location");
            mLocation = (Location) bundle.getParcelable("Location");
            moveCameraToLoc();
            sendLocPush(pubnub);
            LocationDialog.showLocDialog(MainActivity.this);
        }
    };


    private List<Branch> getAndAddAllBranches()
    {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("branches");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //Get map of users in datasnapshot
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                myBranches = new ArrayList<>();
                while (dataSnapshots.hasNext())
                {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    Branch branch = dataSnapshotChild.getValue(Branch.class);
                    myBranches.add(branch);
                    assert branch != null;
                    Log.d(TAG, "Value is: " + branch.toString());
                }

                addMarkersOfBranches();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                //handle databaseError
            }
        });
        return myBranches;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(this, this.getClass());
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mLocationManager.requestLocationUpdates(mProvider, 400, 1, pi);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mFragContainer.setVisibility(View.GONE);
        setTitle("מפת סניפים");
        myBranches = getAndAddAllBranches();
    }

    private void addMarkersOfBranches()
    {
        if (myBranches.size() > 0)
        {
            for (Branch branch : myBranches)
            {
                LatLng ltlg = new LatLng(branch.getLatLon().getLatitude(), branch.getLatLon().getLongitude());
                mDataHashMap.put(ltlg, branch);
                MarkerOptions markerOptions = new MarkerOptions().position(ltlg);
                if (mMap != null)
                {
                    mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mMarkerBlueIcon)));
                }
            }

        }
    }

    public void myOnResume()
    {
        this.onResume();
    }

    @Override
    public void onBackPressed()
    {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            super.onBackPressed();
        }
        else
        {
            mFragContainer.setVisibility(View.GONE);
            setTitle("מפת סניפים");
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        moveCameraToLoc();
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {

            @Override
            public View getInfoWindow(Marker marker)
            {

                @SuppressLint ("InflateParams") View v = getLayoutInflater().inflate(R.layout.default_marker_info_window, null);
                ViewFlipper markerInfoContainer = (ViewFlipper) v.findViewById(R.id.markerInfoContainer);
                View viewContainer = getLayoutInflater()./*inflate(R.layout.default_marker_info_layout, null)*/inflate(R.layout.popup, (ViewGroup) findViewById(R.id.popup_element));
                /*viewContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        mFragContainer.setVisibility(View.VISIBLE);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.add(R.id.fragment_container, new BranchFragment());
                        ft.addToBackStack(null);
                        ft.commit();
                        setTitle("סניף");
                    }
                });*/
                TextView branchName = (TextView) viewContainer.findViewById(R.id.popup_name);
                TextView branchManager = (TextView) viewContainer.findViewById(R.id.popup_manager);
                TextView branchAddress = (TextView) viewContainer.findViewById(R.id.popup_address);

                Branch branch = mDataHashMap.get(marker.getPosition());
                if (branch != null)
                {
                    branchName.setText(branch.getName());
                    branchManager.setText(branch.getManager());
                    branchAddress.setVisibility(View.VISIBLE);
                    branchAddress.setText(branch.getAddress());
                    markerInfoContainer.addView(viewContainer);

                }
                else
                {
                    viewContainer.setBackgroundColor(Color.GRAY);
                    branchName.setText("מיקומך");
                    branchAddress.setVisibility(View.GONE);
                    branchManager.setText(getCompleteAddressString(marker.getPosition().latitude, marker.getPosition().longitude));
                    markerInfoContainer.addView(viewContainer);
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker marker)
            {

                return null;

            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                mFragContainer.setVisibility(View.VISIBLE);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fragment_container, new BranchFragment());
                ft.addToBackStack(null);
                ft.commit();
                setTitle("סניף");
            }
        });
    }

    private void moveCameraToLoc()
    {
        if(mLocation != null)
        {
            setMarkerOnMap(mLocation.getLatitude(), mLocation.getLongitude());
            LatLng currentLoc = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            CameraUpdate camPosition = CameraUpdateFactory.newLatLngZoom(currentLoc, 12.0f);
            mMap.animateCamera(camPosition);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void setMarkerOnMap(double latitude, double longitude)
    {

        if (mMarker == null)
        {
            mMarker = mMap.addMarker(new MarkerOptions().position((new LatLng(latitude, longitude))).icon(BitmapDescriptorFactory.fromBitmap(mMarkerRedIcon)));
        }
        else
        {
            mMarker.setPosition(new LatLng(latitude, longitude));
        }

        Projection projection = mMap.getProjection();
        LatLng markerLatLng = new LatLng(mMarker.getPosition().latitude, mMarker.getPosition().longitude);
        Point markerScreenPosition = projection.toScreenLocation(markerLatLng);

        Point pointHalfScreenAbove = new Point(markerScreenPosition.x, (markerScreenPosition.y - (mapFrag.getHeight() / 2)) + mapFrag.getHeight() / 2);
        LatLng aboveMarkerLatLng = projection.fromScreenLocation(pointHalfScreenAbove);
        CameraUpdate camPosition = CameraUpdateFactory.newLatLngZoom(aboveMarkerLatLng, 12.0f);
        mMap.animateCamera(camPosition);
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height)
    {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    @SuppressLint ("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE)
    {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null)
            {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++)
                {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            }
            else
            {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    private Location getLastKnownLocation()
    {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
        {
            @SuppressLint ("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null)
            {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
            {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onSignIn(Intent data)
    {

    }

    @Override
    public void onSignOut(Status status)
    {

    }

    @Override
    public void onRevoke(Status status)
    {
        PersistenceManager.getInstance().setIsLoggedIn(false);
        Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(mainIntent);
        finish();
    }
}
