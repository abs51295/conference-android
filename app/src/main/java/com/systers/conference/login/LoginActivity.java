package com.systers.conference.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.text.TextUtils;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.systers.conference.R;
import com.systers.conference.register.PreRegistrationActivity;
import com.systers.conference.util.AccountUtils;
import com.systers.conference.util.LogUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * A login screen that offers login via Google and Facebook.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = LogUtils.makeLogTag(LoginActivity.class);
    private static final int GOOGLE_SIGN_IN = 9001;
    @BindView(R.id.root)
    ConstraintLayout mRootView;
    @BindView(R.id.google_button)
    Button mGoogleSignInButton;
    @BindView(R.id.twitter_button)
    Button mTwitterSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;
    private TwitterAuthClient mTwitterAuthClient;

    @OnClick(R.id.google_button)
    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @OnClick(R.id.twitter_button)
    public void twitterSignIn() {
        mTwitterAuthClient.authorize(this, new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSignInResult(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Snackbar.make(mRootView, getString(R.string.registration_unsuccessful), Snackbar.LENGTH_LONG).show();
                LogUtils.LOGE(LOG_TAG, exception.getMessage());
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mTwitterAuthClient = new TwitterAuthClient();
        if (mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, PreRegistrationActivity.class));
            ActivityCompat.finishAffinity(this);
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setDrawables();
        mProgressDialog = new ProgressDialog(this);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).
                requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(mRootView, getString(R.string.registration_unsuccessful), Snackbar.LENGTH_LONG).show();
        LogUtils.LOGE(LOG_TAG, connectionResult.getErrorMessage());
    }

    private void startRegisterActivity() {
        AccountUtils.setLoginVisited(this);
        startActivity(new Intent(this, PreRegistrationActivity.class));
        ActivityCompat.finishAffinity(this);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            showProgressDialog();
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
                AccountUtils.setActiveGoogleAccount(getApplicationContext(), account.getId());
            }
            if (account.getPhotoUrl() != null) {
                AccountUtils.setProfilePictureUrl(getApplicationContext(), account.getPhotoUrl().toString());
            }
            firebaseAuthWithGoogle(account);
        } else {
            LogUtils.LOGE(LOG_TAG, "Failed Sign In");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            startRegisterActivity();
                        } else {
                            Snackbar.make(mRootView, getString(R.string.registration_unsuccessful), Snackbar.LENGTH_LONG).show();
                            LogUtils.LOGE(LOG_TAG, task.toString());
                        }
                    }
                });
    }

    private void handleTwitterSignInResult(final TwitterSession session) {
        showProgressDialog();
        TwitterApiClient apiClient = new TwitterApiClient(session);
        TwitterCore.getInstance().addApiClient(session, apiClient);
        TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, true, true).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (!TextUtils.isEmpty(user.email)) {
                        AccountUtils.setEmail(LoginActivity.this, user.email);
                    }
                    if (!user.defaultProfileImage) {
                        AccountUtils.setProfilePictureUrl(LoginActivity.this, user.profileImageUrlHttps);
                    }
                    String[] name = user.name.split("\\s+");
                    try {
                        AccountUtils.setFirstName(LoginActivity.this, name[0]);
                        AccountUtils.setLastName(LoginActivity.this, name[1]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        LogUtils.LOGE(LOG_TAG, "Complete name not set");
                    }
                    firebaseAuthWithTwitter(session);
                } else {
                    LogUtils.LOGE(LOG_TAG, response.message());
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                LogUtils.LOGE(LOG_TAG, t.getMessage());
                hideProgressDialog();
            }
        });
    }

    private void firebaseAuthWithTwitter(final TwitterSession session) {
        AuthCredential authCredential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret
        );
        mFirebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            startRegisterActivity();
                        } else {
                            Snackbar.make(mRootView, getString(R.string.registration_unsuccessful), Snackbar.LENGTH_LONG).show();
                            LogUtils.LOGE(LOG_TAG, task.toString());
                        }
                    }
                });
    }

    private void setDrawables() {
        Drawable iconDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_googleg_color_24dp);
        mGoogleSignInButton.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null);
        iconDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_twitter_bird_white_24dp);
        mTwitterSignInButton.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null);
    }

    private void showProgressDialog() {
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.progressdialog_message));
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }
}

