package com.yso.mybranch.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.yso.mybranch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleSignInButton extends Fragment
{

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;


    public GoogleSignInButton()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.button_goorle_sign_in, container, false);

        SignInButton signInButton = (SignInButton) view.findViewById(R.id.google_sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                GoogleSignInFragment myf = new GoogleSignInFragment();
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.add(R.id.container, myf);
//                transaction.commit();
            }
        });

        return view;
    }
}
