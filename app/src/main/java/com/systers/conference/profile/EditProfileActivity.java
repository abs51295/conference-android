package com.systers.conference.profile;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mvc.imagepicker.ImagePicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.util.AccountUtils;
import com.systers.conference.util.LogUtils;
import com.systers.conference.util.PermissionsUtil;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int GOOGLE_SIGN_IN = 9001;
    private static final String LOG_TAG = LogUtils.makeLogTag(EditProfileActivity.class);
    private static final String[] RUN_TIME_PERMISSIONS = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ? new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE} : new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_CALLBACK = 100;
    @BindView(R.id.profile_coordinator_layout)
    CoordinatorLayout mLayout;
    @BindView(R.id.avatar)
    CircleImageView mAvatar;
    @BindView(R.id.edit_icon)
    FloatingActionButton mEditIcon;
    @BindView(R.id.edit_first_name)
    EditText mFirstName;
    @BindView(R.id.edit_last_name)
    EditText mLastName;
    @BindView(R.id.edit_email)
    EditText mEmail;
    @BindView(R.id.edit_company_name)
    EditText mCompanyName;
    @BindView(R.id.edit_role)
    EditText mRole;
    @BindView(R.id.facebook_button)
    Button mFacebookButton;
    @BindView(R.id.google_button)
    Button mGoogleButton;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.text_input_firstname)
    TextInputLayout mTextFirstName;
    @BindView(R.id.text_input_last_name)
    TextInputLayout mTextLastName;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsGoogleConnected;
    private boolean mIsFacebookConnected;
    private boolean mIsAvatarPresent;
    private CallbackManager mCallbackManager;

    @OnClick(R.id.google_button)
    public void googleSignInOrSignOut() {
        if (mIsGoogleConnected) {
            DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Handle revoke access here.
                    revokeGoogleAccess();
                }
            };
            createDialog(positiveListener);
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        }
    }

    @OnClick(R.id.facebook_button)
    public void FbSignInOrSignOut() {
        if (mIsFacebookConnected) {
            DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    revokeFacebookAccess();
                }
            };
            createDialog(positiveListener);
        } else {
            //Getting only profile since we have already registered the user and want only ID to determine the state.
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }
    }

    @OnClick(R.id.edit_icon)
    public void editAvatar() {
        if (PermissionsUtil.areAllRunTimePermissionsGranted(RUN_TIME_PERMISSIONS, this)) {
            final CharSequence[] items = mIsAvatarPresent ? new CharSequence[]{getString(R.string.edit_avatar), getString(R.string.delete_avatar)}
                    : new CharSequence[]{getString(R.string.edit_avatar)};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (items[which].equals(getString(R.string.edit_avatar))) {
                        dialog.dismiss();
                        ImagePicker.pickImage(EditProfileActivity.this, getString(R.string.select_avatar));
                    } else if (items[which].equals(getString(R.string.delete_avatar))) {
                        dialog.dismiss();
                        deleteOldAvatar();
                        AccountUtils.setProfilePictureUrl(EditProfileActivity.this, null);
                        updateAvatar();
                    }
                }
            });
            builder.show();
        } else {
            LogUtils.LOGE(LOG_TAG, "Permission not granted");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                requestRunTimePermissions();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mFirstName.setText(AccountUtils.getFirstName(this));
        mLastName.setText(AccountUtils.getLastName(this));
        mEmail.setText(AccountUtils.getEmail(this));
        mCompanyName.setText(AccountUtils.getCompanyName(this));
        mRole.setText(AccountUtils.getCompanyRole(this));
        updateAvatar();
        mIsGoogleConnected = AccountUtils.hasActiveGoogleAccount(this);
        updateGoogleButton();
        mIsFacebookConnected = AccountUtils.hasActiveFacebookAccount(this);
        updateFacebookButton();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            AccountUtils.setActiveFacebookAccount(getApplicationContext(), currentProfile.getId());
                            mIsFacebookConnected = true;
                            updateFacebookButton();
                            mProfileTracker.stopTracking();
                        }
                    };
                } else {
                    Profile profile = Profile.getCurrentProfile();
                    AccountUtils.setActiveFacebookAccount(getApplicationContext(), profile.getId());
                    mIsFacebookConnected = true;
                    updateFacebookButton();
                }
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImagePicker.setMinQuality(getResources().getInteger(R.integer.avatar_dimen), getResources().getInteger(R.integer.avatar_dimen));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtils.LOGE(LOG_TAG, connectionResult.getErrorMessage());
    }

    private void deleteOldAvatar() {
        if (AccountUtils.getProfilePictureUrl(this) != null) {
            if (!Patterns.WEB_URL.matcher(AccountUtils.getProfilePictureUrl(this)).matches()) {
                Uri uri = Uri.parse(AccountUtils.getProfilePictureUrl(this));
                getContentResolver().delete(uri, null, null);
            }
        }
    }

    private void updateAvatar() {
        Drawable icon = AppCompatResources.getDrawable(this, R.drawable.ic_photo_camera_black_24dp);
        mEditIcon.setIconDrawable(icon);
        if (AccountUtils.getProfilePictureUrl(this) != null) {
            Picasso.with(this)
                    .load(Uri.parse(AccountUtils.getProfilePictureUrl(this)))
                    .resize(getResources().getInteger(R.integer.avatar_dimen), getResources().getInteger(R.integer.avatar_dimen))
                    .centerCrop()
                    .placeholder(R.drawable.male_icon_9_glasses)
                    .error(R.drawable.male_icon_9_glasses)
                    .into(mAvatar, new Callback() {
                        @Override
                        public void onSuccess() {
                            mIsAvatarPresent = true;
                        }

                        @Override
                        public void onError() {
                            mIsAvatarPresent = false;
                        }
                    });
        } else {
            mAvatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.male_icon_9_glasses));
            mIsAvatarPresent = false;
        }
    }

    private void updateGoogleButton() {
        if (mIsGoogleConnected) {
            mGoogleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.google_plus_color));
            Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_google_plus_box_white);
            Drawable rightDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_check_white_24dp);
            mGoogleButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable, null);
            mGoogleButton.setText(getString(R.string.connected));
            mGoogleButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            mGoogleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_background));
            Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_google_plus_box);
            mGoogleButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
            mGoogleButton.setText(getString(R.string.google_button));
            mGoogleButton.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }

    private void updateFacebookButton() {
        if (mIsFacebookConnected) {
            mFacebookButton.setBackgroundColor(ContextCompat.getColor(this, R.color.facebook_color));
            Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_facebook_box_white);
            Drawable rightDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_check_white_24dp);
            mFacebookButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable, null);
            mFacebookButton.setText(getString(R.string.connected));
            mFacebookButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            mFacebookButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_background));
            Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_facebook_box);
            mFacebookButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
            mFacebookButton.setText(getString(R.string.facebook_button));
            mFacebookButton.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
            if (googleSignInAccount.getId() != null) {
                AccountUtils.setActiveGoogleAccount(this, googleSignInAccount.getId());
                mIsGoogleConnected = true;
                updateGoogleButton();
            }
        } else {
            LogUtils.LOGE(LOG_TAG, "Failed Sign In");
        }
    }

    private void createDialog(DialogInterface.OnClickListener positiveListener) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_message))
                .setPositiveButton(android.R.string.ok, positiveListener)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void revokeFacebookAccess() {
        if (AccessToken.getCurrentAccessToken() == null) {
            LogUtils.LOGE(LOG_TAG, "Already logged out from FB");
            return;
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                AccountUtils.clearActiveFacebookAccount(getApplicationContext());
                mIsFacebookConnected = false;
                updateFacebookButton();
            }
        }).executeAsync();
    }

    private void revokeGoogleAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            AccountUtils.clearActiveGoogleAccount(getApplicationContext());
                            mIsGoogleConnected = false;
                            updateGoogleButton();
                        } else {
                            LogUtils.LOGE(LOG_TAG, "Error while revoking");
                        }
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        attemptToSave(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_check) {
            attemptToSave(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(googleSignInResult);
        } else if (requestCode == ImagePicker.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            LogUtils.LOGE(LOG_TAG, "Avatar");
            Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
            String selectedImagePath = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    getString(R.string.image_title),
                    getString(R.string.image_description)
            );
            deleteOldAvatar();
            AccountUtils.setProfilePictureUrl(this, selectedImagePath);
            updateAvatar();
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @TargetApi(23)
    private void requestRunTimePermissions() {
        if (!PermissionsUtil.areAllRunTimePermissionsGranted(RUN_TIME_PERMISSIONS, this)) {
            requestPermissions(RUN_TIME_PERMISSIONS, PERMISSION_CALLBACK);
        }
    }

    private void openApplicationSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void showSnackBar() {
        Snackbar.make(mLayout, R.string.permissions_snackbar, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.open_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();
                    }
                }).show();
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK) {
            boolean showRationale = false;
            for (int i = 0, len = permissions.length; i < len; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    showRationale = showRationale || ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                }
            }
            if (!PermissionsUtil.areAllRunTimePermissionsGranted(permissions, this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle(getString(R.string.permissions_dialog_title));
                builder.setMessage(getString(R.string.permissions_dialog_message));
                if (showRationale) {
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestPermissions(permissions, PERMISSION_CALLBACK);
                        }
                    });
                } else {
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            openApplicationSettings();
                        }
                    });
                }
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showSnackBar();
                    }
                });
                builder.show();
            } else {
                mAvatar.performClick();
            }
        }
    }

    private void attemptToSave(boolean isBackPressed) {
        mTextFirstName.setError(null);
        mTextLastName.setError(null);
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(firstName)) {
            mTextFirstName.setError(getString(R.string.error_field_required));
            focusView = mFirstName;
            cancel = true;
        } else if (TextUtils.isEmpty(lastName)) {
            mTextLastName.setError(getString(R.string.error_field_required));
            focusView = mLastName;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            saveChanges(isBackPressed);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void saveChanges(boolean isBackPressed) {
        LogUtils.LOGE(LOG_TAG, mFirstName.getText().toString() + ' ' + AccountUtils.getFirstName(this));
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.edit_profile), true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        boolean dataChanged = !((mFirstName.getText().toString().equals(AccountUtils.getFirstName(this))) &&
                (mLastName.getText().toString().equals(AccountUtils.getLastName(this))) &&
                (mCompanyName.getText().toString().equals(AccountUtils.getCompanyName(this))) &&
                (mRole.getText().toString().equals(AccountUtils.getCompanyRole(this))));
        if (isBackPressed) {
            if (dataChanged) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("Are you sure you want to discard your changes?");
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        saveChanges(false);
                    }
                });
                builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard();
                        dialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            } else {
                hideKeyboard();
                startActivity(intent);
                finish();
            }
        } else {
            if (dataChanged) {
                AccountUtils.setFirstName(this, mFirstName.getText().toString());
                AccountUtils.setLastName(this, mLastName.getText().toString());
                AccountUtils.setCompanyName(this, mCompanyName.getText().toString());
                AccountUtils.setCompanyRole(this, mRole.getText().toString());
                Toast.makeText(this, getString(R.string.save_toast), Toast.LENGTH_LONG).show();
            }
            hideKeyboard();
            startActivity(intent);
            finish();
        }
    }
}
