package com.yso.mybranch.fragment;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.yso.mybranch.managers.DatabaseReferenceManager;
import com.yso.mybranch.model.Branch;
import com.yso.mybranch.R;
import com.yso.mybranch.activity.MainActivity;
import com.yso.mybranch.utils.Utils;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBranch extends Fragment
{

    private static final String TAG = AddBranch.class.getSimpleName();

//    private List<Branch> myBranches;
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
//        getAllBranches();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_branch, container, false);

        final EditText etName = (EditText) view.findViewById(R.id.add_branch_name);
        final EditText etManager = (EditText) view.findViewById(R.id.add_branch_manager);
        final EditText etAddress = (EditText) view.findViewById(R.id.add_branch_address);


        mAddBtn = (Button) view.findViewById(R.id.add_branch_btn);
//        mAddBtn.setEnabled(false);
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
//                    myBranches.add(branch);
                    DatabaseReferenceManager.addBrancheToDBR(branch);

                    Utils.closeKeyboard(getActivity(), mAddBtn);

                    getActivity().getSupportFragmentManager().popBackStack();
                    ((MainActivity) getActivity()).myOnResume();
                    //                    getActivity().getSupportFragmentManager().beginTransaction().remove(AddBranch.this).commit();
                }
                else
                {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "אנא מלא את כל הפרטים", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
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

//    private void getAllBranches()
//    {
//        DatabaseReferenceManager.getAllBranches(new DatabaseRefCallBack()
//        {
//            @Override
//            public void onGetBranches(List<Branch> branches)
//            {
//                myBranches = branches;
//                mAddBtn.setEnabled(true);
//            }
//        });
//    }
}
