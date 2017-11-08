package com.yso.mybranch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.yso.mybranch.R;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private final int RC_SIGN_IN_GOOGLE = 9001;

    private GoogleApiClient mGoogleApiClient;
    private GoogleConnectionCallback mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN_FACEBOOK)
//        {
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        }

        if (requestCode == RC_SIGN_IN_GOOGLE)
        {
            mCallback.onSignIn(data);
        }
    }

    public GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    public void googleSignIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(getGoogleApiClient());
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);

    }

    public void googleSignOut()
    {
        Auth.GoogleSignInApi.signOut(getGoogleApiClient()).setResultCallback(new ResultCallback<Status>()
        {
            @Override
            public void onResult(@NonNull Status status)
            {
                mCallback.onSignOut(status);
            }
        });
    }

    public void googleRevokeAccess()
    {
        Auth.GoogleSignInApi.revokeAccess(getGoogleApiClient()).setResultCallback(new ResultCallback<Status>()
        {
            @Override
            public void onResult(@NonNull Status status)
            {
                mCallback.onRevoke(status);
            }
        });
    }

    public interface GoogleConnectionCallback
    {
        void onSignIn(Intent data);
        void onSignOut(Status status);
        void onRevoke(Status status);
    }

    public void setListener(GoogleConnectionCallback callback)
    {
        mCallback = callback;
    }
}
