package com.yso.mybranch.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.yso.mybranch.R;
import com.yso.mybranch.managers.DatabaseReferenceManager;
import com.yso.mybranch.managers.PersistenceManager;
import com.yso.mybranch.model.User;
import com.yso.mybranch.utils.SecurePreferences;

import static com.yso.mybranch.activity.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, BaseActivity.GoogleConnectionCallback
{
    private static final String TAG = LoginActivity.class.getSimpleName();

    private final int RC_SIGN_IN_FACEBOOK = 64206;
    private final int RC_SIGN_IN_GOOGLE = 9001;
    private CallbackManager mCallbackManager;
    //    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private TextView mStatusTextView;
    private boolean mIsLoggedIn = false;
    private AccessTokenTracker mAccessTokenTracker;
    private SignInButton mGoogleSignInButton;
    private LoginButton mFacebookLoginButton;
    //    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setListener(this);
        //        mAuth = FirebaseAuth.getInstance();

        mStatusTextView = (TextView) findViewById(R.id.status);
        //        AnimationUtils.slideUp(mStatusTextView);

        //        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        //        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        //        AnimationUtils.slideUp(mGoogleSignInButton);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!mIsLoggedIn)
                {
                    googleSignIn();
                }
                else
                {
                    googleRevokeAccess();
                }
            }
        });
        //////////////////////////////////
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        //        AnimationUtils.slideUp(mFacebookLoginButton);
        mFacebookLoginButton.setReadPermissions("email", "public_profile");
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                //                handleFacebookAccessToken(loginResult.getAccessToken());
                Profile profile = Profile.getCurrentProfile();

                User user = new User();
                user.setName(profile.getName());
                DatabaseReferenceManager.addUsersToDBR(user);
                PersistenceManager.getInstance().setUser(user);

                mGoogleSignInButton.setVisibility(View.GONE);
                updateUI(true, profile.getFirstName());
                if (checkLocationPermission() && isLoggedIn())
                {
                    goToNext();
                }
            }

            @Override
            public void onCancel()
            {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error)
            {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        mAccessTokenTracker = new AccessTokenTracker()
        {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
            {
                if (currentAccessToken == null)
                {
                    updateUI(false, null);
                    mGoogleSignInButton.setVisibility(View.VISIBLE);
                }
            }
        };
        mAccessTokenTracker.startTracking();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        //        FirebaseUser currentUser = mAuth.getCurrentUser();
        //        updateUI(currentUser);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null)
        {
            String firstName = profile.getFirstName();
            updateUI(true, firstName);
            mGoogleSignInButton.setVisibility(View.GONE);
            if (checkLocationPermission() && isLoggedIn())
            {
                goToNext();
            }
        }
        else
        {

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(getGoogleApiClient());
            if (opr.isDone())
            {
                Log.d(TAG, "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            }
            else
            {
                showProgressDialog();
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>()
                {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult)
                    {
                        hideProgressDialog();
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        hideProgressDialog();
    }

    private void goToNext()
    {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_FACEBOOK)
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

//        if (requestCode == RC_SIGN_IN_GOOGLE)
//        {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
    }

    /*@Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }*/

    public boolean isLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (checkLocationPermission() && result.isSuccess())
        {
            mIsLoggedIn = true;
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

            User user = new User();
            user.setName(acct.getDisplayName());
            DatabaseReferenceManager.addUsersToDBR(user);
            PersistenceManager.getInstance().setUser(user);

            updateUI(true, result.getSignInAccount().getDisplayName());
            setGoogleButtonText(mGoogleSignInButton, getString(R.string.sign_out));
            mFacebookLoginButton.setVisibility(View.GONE);
            goToNext();
        }
        else
        {
            mIsLoggedIn = false;
            updateUI(false, result.getStatus().toString());
            setGoogleButtonText(mGoogleSignInButton, getString(R.string.sign_in));
            mFacebookLoginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog()
    {
        if (mProgressDialog == null)
        {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing())
        {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn, String userData)
    {
        if (signedIn)
        {
            String text = String.format(getString(R.string.signed_in_fmt), userData);
            mStatusTextView.setText(text);
        }
        else
        {
            mStatusTextView.setText(R.string.signed_out);
        }
    }

    protected void setGoogleButtonText(SignInButton signInButton, String buttonText)
    {
        for (int i = 0; i < signInButton.getChildCount(); i++)
        {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView)
            {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    /* private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
         Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

         AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
         mAuth.signInWithCredential(credential)
                 .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()) {
                             // Sign in success, update UI with the signed-in user's information
                             Log.d(TAG, "signInWithCredential:success");
                             FirebaseUser user = mAuth.getCurrentUser();
                             //                            updateUI(user);
                             goToNext();
                         } else {
                             // If sign in fails, display a message to the user.
                             Log.w(TAG, "signInWithCredential:failure", task.getException());
                             Toast.makeText(LoginActivity.this, "Authentication failed.",
                                     Toast.LENGTH_SHORT).show();
                             //                            updateUI(null);
                         }

                         // ...
                     }
                 });
     }

     private void handleFacebookAccessToken(AccessToken token) {
         Log.d(TAG, "handleFacebookAccessToken:" + token);

         AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
         mAuth.signInWithCredential(credential)
                 .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()) {
                             // Sign in success, update UI with the signed-in user's information
                             Log.d(TAG, "signInWithCredential:success");
                             FirebaseUser user = mAuth.getCurrentUser();
                             //                            updateUI(user);
                         } else {
                             // If sign in fails, display a message to the user.
                             Log.w(TAG, "signInWithCredential:failure", task.getException());
                             Toast.makeText(LoginActivity.this, "Authentication failed.",
                                     Toast.LENGTH_SHORT).show();
                             //                            updateUI(null);
                         }

                         // ...
                     }
                 });
     }*/
    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                new AlertDialog.Builder(this).setTitle(R.string.title_location_permission).setMessage(R.string.text_location_permission).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                }).create().show();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        //                        goToNext();
                    }
                }
                else
                {

                }
                return;
            }

        }
    }

    @Override
    public void onSignIn(Intent data)
    {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
        mIsLoggedIn = true;
        PersistenceManager.getInstance().setIsLoggedIn(mIsLoggedIn);
    }

    @Override
    public void onSignOut(Status status)
    {
        updateUI(false, status.toString());
        setGoogleButtonText(mGoogleSignInButton, getString(R.string.sign_in));
    }

    @Override
    public void onRevoke(Status status)
    {
        updateUI(false, status.toString());
        setGoogleButtonText(mGoogleSignInButton, getString(R.string.sign_in));
        mFacebookLoginButton.setVisibility(View.VISIBLE);

        mIsLoggedIn = false;
        PersistenceManager.getInstance().setIsLoggedIn(mIsLoggedIn);
    }
}
