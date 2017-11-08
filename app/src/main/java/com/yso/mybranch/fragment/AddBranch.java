package com.yso.mybranch.fragment;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yso.mybranch.model.Branch;
import com.yso.mybranch.R;
import com.yso.mybranch.activity.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBranch extends Fragment
{

    private static final String TAG = AddBranch.class.getSimpleName();

    private List<Branch> myBranches;
    private DatabaseReference mDatabaseRef;
    private Button mAddBtn;
//    private List<Branch> mMyDBBranches;

    public AddBranch()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
//        final MySQLiteHelper db = new MySQLiteHelper(getContext());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("branches");
        myBranches = getAllBranches();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_branch, container, false);

        final EditText etName = (EditText) view.findViewById(R.id.add_branch_name);
        final EditText etManager = (EditText) view.findViewById(R.id.add_branch_manager);
        final EditText etAddress = (EditText) view.findViewById(R.id.add_branch_address);


        mAddBtn = (Button) view.findViewById(R.id.add_branch_btn);
        mAddBtn.setEnabled(false);
        mAddBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String name = etName.getText().toString();
                final String manager = etManager.getText().toString();
                final String address = etAddress.getText().toString();

                if (!name.equals("") && !manager.equals("") && !address.equals(""))
                {
                    Branch branch = new Branch();
                    branch.setName(name);
                    branch.setManager(manager);
                    branch.setAddress(address);
                    LatLng latLng = getLocationFromAddress(getContext(), address);
                    Branch.LatLon latLon = new Branch.LatLon();
                    latLon.setLatitude(latLng.latitude);
                    latLon.setLongitude(latLng.longitude);
                    branch.setLatLon(latLon);
//                    db.addBranch(branch);
//                    myBranches = db.getAllBranches();
                    myBranches.add(branch);
                    mDatabaseRef.setValue(myBranches);

                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(mAddBtn.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    getActivity().getSupportFragmentManager().popBackStack();
                    ((MainActivity)getActivity()).myOnResume();
//                    getActivity().getSupportFragmentManager().beginTransaction().remove(AddBranch.this).commit();
                }
                else
                {
                    Toast.makeText(getContext(), "אנא מלא את כל הפרטים", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Read from the database
        mDatabaseRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
//                mMyDBBranches = new ArrayList<>();
                while (dataSnapshots.hasNext())
                {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    Branch branch = dataSnapshotChild.getValue(Branch.class);
//                    mMyDBBranches.add(branch);
                    Log.d(TAG, "Value is: " + branch.toString());
                }


            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return view;
    }

    public LatLng getLocationFromAddress(Context context, String strAddress)
    {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex)
        {

            ex.printStackTrace();
        }

        return p1;
    }

    private List<Branch> getAllBranches()
    {
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener()
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
                    Log.d(TAG, "Value is: " + branch.toString());
                }
                mAddBtn.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                //handle databaseError
            }
        });
        return myBranches;
    }
}
