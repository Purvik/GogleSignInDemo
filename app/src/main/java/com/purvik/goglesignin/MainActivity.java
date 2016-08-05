package com.purvik.goglesignin;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    //Signin button
    private SignInButton signInButton;

    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    ImageView profileImage;
    TextView tvAccDetails;
    //Button btnSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing google signin option
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_app_client_id))
                .requestEmail()
                .build();

        //Initializing signinbutton
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        tvAccDetails = (TextView) findViewById(R.id.tvAccDetails);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        //btnSignOut = (Button) findViewById(R.id.btnSignOut);

        assert signInButton != null;
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //Setting onclick listener to signing button
        signInButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in_button) {
            //call Sign In Activity
            signIn();
        }/*else if(v.getId() == R.id.btnSignOut){
            signOut();
        }*/
    }

    /*private void signOut() {

        mGoogleApiClient.connect();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        signInButton.setVisibility(View.VISIBLE);
                        btnSignOut.setVisibility(View.GONE);
                    }
                });
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }

    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {

            signInButton.setVisibility(View.GONE);
            //btnSignOut.setVisibility(View.VISIBLE);

            Log.d("Tag", "handleSignInResult: In Success");
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            Picasso.with(MainActivity.this).load(acct.getPhotoUrl()).into(profileImage);

            String details = "";

            details = "\nName: " + acct.getDisplayName() +
                      "\n Email: " +  acct.getEmail() +
                      "\n Photo Url:" + acct.getPhotoUrl();

            tvAccDetails.setText(details);

            /*//Displaying name and email
            textViewName.setText(acct.getDisplayName());
            textViewEmail.setText(acct.getEmail());

            //Initializing image loader
            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                    .getImageLoader();

            imageLoader.get(acct.getPhotoUrl().toString(),
                    ImageLoader.getImageListener(profilePhoto,
                            R.mipmap.ic_launcher,
                            R.mipmap.ic_launcher));

            //Loading image
            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader)*/;

        } else {

            Log.d("Tag", "handleSignInResult: In Fail");
            //If login fails
            Toast.makeText(this, "Login Failed. Try Again.", Toast.LENGTH_LONG).show();
        }
    }


    //This function will option signing intent
    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
