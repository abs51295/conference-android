package com.systers.conference.profile;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
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
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mvc.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;
import com.systers.conference.ConferenceApplication;
import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Attendee;
import com.systers.conference.util.FirebaseAuthUtil;
import com.systers.conference.util.FirebaseDatabaseUtil;
import com.systers.conference.util.LogUtils;
import com.systers.conference.util.PermissionsUtil;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.HashMap;
import java.util.Map;

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
    @BindView(R.id.edit_attendee_type)
    EditText mAttendeeType;
    @BindView(R.id.edit_company_name)
    EditText mCompanyName;
    @BindView(R.id.edit_role)
    EditText mRole;
    @BindView(R.id.twitter_button)
    Button mTwitterButton;
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
    private boolean mIsTwitterConnected;
    private boolean mIsAvatarPresent;
    private Attendee mAttendee;
    private RealmDataRepository mRealmRepo = RealmDataRepository.getDefaultInstance();
    private TwitterAuthClient mTwitterAuthClient;
    private ProgressDialog mProgressDialog;
    private Intent intent;

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

    @OnClick(R.id.twitter_button)
    public void twitterSignInOrSignOut() {
        if (mIsTwitterConnected) {
            DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    revokeTwitterAccess();
                }
            };
            createDialog(positiveListener);
        } else {
            mTwitterAuthClient.authorize(this, new Callback<TwitterSession>() {

                @Override
                public void success(Result<TwitterSession> result) {
                    mIsTwitterConnected = true;
                    mAttendee.setTwitterLoggedIn(true);
                    updateTwitterButton();
                }

                @Override
                public void failure(TwitterException exception) {
                    Snackbar.make(mLayout, getString(R.string.sign_in_cancelled), Snackbar.LENGTH_LONG).show();
                    LogUtils.LOGE(LOG_TAG, exception.getMessage());
                }
            });
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
                        mAttendee.setAvatarUrl(null);
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
        mAttendee = mRealmRepo.getAttendeeCopyFromRealmSync(FirebaseAuthUtil.getFirebaseAuthInstance().getCurrentUser().getUid());
        mFirstName.setText(mAttendee.getFirstName());
        mLastName.setText(mAttendee.getLastName());
        mEmail.setText(mAttendee.getEmail());
        mAttendeeType.setText(mAttendee.getAttendeeType());
        mCompanyName.setText(mAttendee.getCompany());
        mRole.setText(mAttendee.getTitle());
        mProgressDialog = new ProgressDialog(this);
        updateAvatar();
        mIsGoogleConnected = mAttendee.isGoogleLoggedIn();
        updateGoogleButton();
        mIsTwitterConnected = mAttendee.isTwitterLoggedIn();
        updateTwitterButton();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        mTwitterAuthClient = new TwitterAuthClient();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImagePicker.setMinQuality(getResources().getInteger(R.integer.avatar_dimen), getResources().getInteger(R.integer.avatar_dimen));
        intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.edit_profile), true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtils.LOGE(LOG_TAG, connectionResult.getErrorMessage());
    }

    private void deleteOldAvatar() {
        if (mAttendee.getAvatarUrl() != null) {
            if (!Patterns.WEB_URL.matcher(mAttendee.getAvatarUrl()).matches()) {
                Uri uri = Uri.parse(mAttendee.getAvatarUrl());
                getContentResolver().delete(uri, null, null);
            }
        }
    }

    private void updateAvatar() {
        Drawable icon = AppCompatResources.getDrawable(this, R.drawable.ic_photo_camera_black_24dp);
        mEditIcon.setIconDrawable(icon);
        if (mAttendee.getAvatarUrl() != null) {
            LogUtils.LOGE(LOG_TAG, mAttendee.getAvatarUrl());
            Picasso.with(this)
                    .load(Uri.parse(mAttendee.getAvatarUrl()))
                    .resize(getResources().getInteger(R.integer.avatar_dimen), getResources().getInteger(R.integer.avatar_dimen))
                    .centerCrop()
                    .placeholder(R.drawable.male_icon_9_glasses)
                    .error(R.drawable.male_icon_9_glasses)
                    .into(mAvatar, new com.squareup.picasso.Callback() {
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

    private void updateTwitterButton() {
        if (mIsTwitterConnected) {
            mTwitterButton.setBackgroundColor(ContextCompat.getColor(this, R.color.twitter_color));
            Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_twitter_bird_white_24dp);
            Drawable rightDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_check_white_24dp);
            mTwitterButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable, null);
            mTwitterButton.setText(getString(R.string.connected));
            mTwitterButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            mTwitterButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_background));
            Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_twitter_bird_black_24dp);
            mTwitterButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
            mTwitterButton.setText(getString(R.string.twitter_button));
            mTwitterButton.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            mIsGoogleConnected = true;
            mAttendee.setGoogleLoggedIn(true);
            updateGoogleButton();
        } else {
            Snackbar.make(mLayout, getString(R.string.sign_in_cancelled), Snackbar.LENGTH_LONG).show();
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

    @SuppressWarnings("deprecation")
    private void revokeTwitterAccess() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(ConferenceApplication.getAppContext());
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
        mAttendee.setTwitterLoggedIn(false);
        mIsTwitterConnected = false;
        updateTwitterButton();
    }

    private void revokeGoogleAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            mAttendee.setGoogleLoggedIn(false);
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
            mAttendee.setAvatarUrl(selectedImagePath);
            updateAvatar();
        } else {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
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
                        Snackbar.make(mLayout, R.string.permissions_snackbar, Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.open_settings), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openApplicationSettings();
                                    }
                                }).show();
                    }
                });
                builder.show();
            } else {
                mEditIcon.performClick();
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
        boolean dataChanged = false;
        if (mAttendee.getFirstName() != null && mAttendee.getLastName() != null && mAttendee.getCompany() != null && mAttendee.getTitle() != null) {
            dataChanged = !((mFirstName.getText().toString().equals(mAttendee.getFirstName())) &&
                    (mLastName.getText().toString().equals(mAttendee.getLastName())) &&
                    (mCompanyName.getText().toString().equals(mAttendee.getCompany())) &&
                    (mRole.getText().toString().equals(mAttendee.getTitle())));
        }
        if (isBackPressed) {
            if (dataChanged) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(getString(R.string.discard_changes));
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
                        dialog.dismiss();
                        mRealmRepo.updateAttendeeInRealmSync(mAttendee);
                        launchMainActivity();
                    }
                });
                builder.show();
            } else {
                mRealmRepo.updateAttendeeInRealmSync(mAttendee);
                launchMainActivity();
            }
        } else {
            if (dataChanged) {
                mAttendee.setFirstName(mFirstName.getText().toString());
                mAttendee.setLastName(mLastName.getText().toString());
                mAttendee.setCompany(mCompanyName.getText().toString());
                mAttendee.setTitle(mRole.getText().toString());
                showProgressDialog();
                mRealmRepo.updateAttendeeInRealmSync(mAttendee);
                if (mAttendee.isRegistered()) {
                    saveAttendeeInFirebase();
                } else {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.save_toast), Toast.LENGTH_LONG).show();
                    launchMainActivity();
                }
            } else {
                mRealmRepo.updateAttendeeInRealmSync(mAttendee);
                launchMainActivity();
            }
        }
    }

    private void saveAttendeeInFirebase() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String attendeeJson = gson.toJson(mAttendee);
        DatabaseReference mFireBaseDatabaseRef = FirebaseDatabaseUtil.getDatabase().getReference().child("Users").child(FirebaseAuthUtil.getFirebaseAuthInstance().getCurrentUser().getUid());
        Map<String, Object> jsonMap = gson.fromJson(attendeeJson, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        mFireBaseDatabaseRef.updateChildren(jsonMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.save_toast), Toast.LENGTH_LONG).show();
                    launchMainActivity();
                } else {
                    hideProgressDialog();
                    LogUtils.LOGE(LOG_TAG, task.getException().getMessage());
                }
            }
        });
    }

    private void launchMainActivity() {
        hideProgressDialog();
        hideKeyboard();
        startActivity(intent);
        finish();
    }

    private void showProgressDialog() {
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.progressdialog_message));
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
