package com.systers.conference.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.systers.conference.R;
import com.systers.conference.model.FacebookUser;
import com.systers.conference.register.PreRegistrationActivity;
import com.systers.conference.util.AccountUtils;
import com.systers.conference.util.LogUtils;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * A login screen that offers login via Google and Facebook.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = LogUtils.makeLogTag(LoginActivity.class);
    private static final int GOOGLE_SIGN_IN = 9001;
    @BindView(R.id.google_sign_in_button)
    SignInButton mSignInButton;
    @BindView(R.id.fb_login_button)
    LoginButton mLoginButton;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;

    @OnClick(R.id.google_sign_in_button)
    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AccountUtils.getLoginVisited(this)) {
            startActivity(new Intent(this, PreRegistrationActivity.class));
            ActivityCompat.finishAffinity(this);
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_work_history"));
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFbSignInResult(loginResult);
            }

            @Override
            public void onCancel() {
                LogUtils.LOGE(LOG_TAG, "Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtils.LOGE(LOG_TAG, error.toString());
            }
        });
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtils.LOGE(LOG_TAG, connectionResult.getErrorMessage());
    }

    private void handleFbSignInResult(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        LogUtils.LOGE(LOG_TAG, object.toString());
                        FacebookUser facebookUser = new Gson().fromJson(object.toString(), FacebookUser.class);
                        if (facebookUser.getAccountId() != null) {
                            AccountUtils.setActiveFacebookAccount(getApplicationContext(), facebookUser.getAccountId());
                        }
                        if (facebookUser.getFirstName() != null) {
                            AccountUtils.setFirstName(getApplicationContext(), facebookUser.getFirstName());
                        }
                        if (facebookUser.getLastName() != null) {
                            AccountUtils.setLastName(getApplicationContext(), facebookUser.getLastName());
                        }
                        if (facebookUser.getEmail() != null) {
                            AccountUtils.setEmail(getApplicationContext(), facebookUser.getEmail());
                        }
                        if (facebookUser.getCompany() != null) {
                            AccountUtils.setCompanyName(getApplicationContext(), facebookUser.getCompany());
                        }
                        if (facebookUser.getRole() != null) {
                            AccountUtils.setCompanyRole(getApplicationContext(), facebookUser.getRole());
                        }
                        if (facebookUser.getPictureUrl() != null) {
                            LogUtils.LOGE(LOG_TAG, facebookUser.getPictureUrl());
                            AccountUtils.setProfilePictureUrl(getApplicationContext(), facebookUser.getPictureUrl());
                        }
                        startRegisterActivity();
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,work,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void startRegisterActivity() {
        AccountUtils.setLoginVisited(this);
        startActivity(new Intent(this, PreRegistrationActivity.class));
        ActivityCompat.finishAffinity(this);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account.getGivenName() != null) {
                AccountUtils.setFirstName(getApplicationContext(), account.getGivenName());
            }
            if (account.getFamilyName() != null) {
                AccountUtils.setLastName(getApplicationContext(), account.getFamilyName());
            }
            if (account.getEmail() != null) {
                AccountUtils.setEmail(getApplicationContext(), account.getEmail());
            }
            if (account.getId() != null) {
                AccountUtils.setActiveGoggleAccount(getApplicationContext(), account.getId());
            }
            if (account.getPhotoUrl() != null) {
                AccountUtils.setProfilePictureUrl(getApplicationContext(), account.getPhotoUrl().toString());
            }
            startRegisterActivity();
        } else {
            LogUtils.LOGE(LOG_TAG, "Failed Sign In");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}

