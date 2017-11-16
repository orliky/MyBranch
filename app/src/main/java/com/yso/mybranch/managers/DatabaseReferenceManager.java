package com.yso.mybranch.managers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yso.mybranch.interfaces.DatabaseRefCallBack;
import com.yso.mybranch.model.Branch;
import com.yso.mybranch.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Admin on 15-Nov-17.
 */

public final class DatabaseReferenceManager
{
    private static final String TAG = DatabaseReferenceManager.class.getSimpleName();
    private static List<Branch> mBranches = new ArrayList<>();
    private static List<User> msUsers = new ArrayList<>();

    public static void addUsersToDBR(User user)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                msUsers = getUsers((ArrayList) msUsers, dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        msUsers.add(user);
        databaseReference.setValue(msUsers);
    }

    public static void addBrancheToDBR(Branch branch)
    {
        mBranches.add(branch);
        FirebaseDatabase.getInstance().getReference().child("branches").setValue(mBranches);
    }

    public static void getAllBranches(final DatabaseRefCallBack databaseRefCallBack)
    {

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("branches");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //Get map of users in datasnapshot
                databaseRefCallBack.onGetBranches(getBranches((ArrayList) mBranches, dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                //handle databaseError
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public static void addBranchesListener(final DatabaseRefCallBack databaseRefCallBack)
    {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("branches");
        databaseRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                databaseRefCallBack.onGetBranches(getBranches((ArrayList) mBranches, dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private static ArrayList getBranches(ArrayList list, DataSnapshot dataSnapshot)
    {
        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren())
        {
            Branch branch = dataSnapshotChild.getValue(Branch.class);
            list.add(branch);
            assert branch != null;
            Log.d(TAG, "Value is: " + branch.toString());
        }
        return list;
    }

    private static ArrayList getUsers(ArrayList list, DataSnapshot dataSnapshot)
    {
        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren())
        {
            User user = dataSnapshotChild.getValue(User.class);
            list.add(user);
            assert user != null;
            Log.d(TAG, "Value is: " + user.toString());
        }
        return list;
    }
}
