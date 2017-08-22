package com.systers.conference.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.systers.conference.BaseActivity;
import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Attendee;
import com.systers.conference.util.AccountUtils;
import com.systers.conference.util.FirebaseAuthUtil;
import com.systers.conference.util.FirebaseDatabaseUtil;
import com.systers.conference.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;


/**
 * A screen that allows user to verify details and register for the event.
 */
public class RegisterActivity extends BaseActivity {

    private static final String LOG_TAG = LogUtils.makeLogTag(RegisterActivity.class);
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.register_first_name)
    EditText mFirstName;
    @BindView(R.id.register_last_name)
    EditText mLastName;
    @BindView(R.id.register_email)
    EditText mEmail;
    @BindView(R.id.register_company_name)
    EditText mCompany;
    @BindView(R.id.register_role)
    EditText mRole;
    @BindView(R.id.radio_attendeeType_group)
    RadioGroup mRadioGroup;
    @BindView(R.id.text_input_firstname)
    TextInputLayout mTextFirstName;
    @BindView(R.id.text_input_last_name)
    TextInputLayout mTextLastName;
    @BindView(R.id.text_input_email)
    TextInputLayout mTextEmail;
    private Attendee mAttendee;
    private RealmDataRepository mRealmRepo = RealmDataRepository.getDefaultInstance();
    private String mAttendeeType;
    private ProgressDialog mProgressDialog;
    private Realm realm;

    @OnClick(R.id.register_button)
    public void register() {
        int radioButtonID = mRadioGroup.getCheckedRadioButtonId();
        View radioButton = mRadioGroup.findViewById(radioButtonID);
        int indexOfChild = mRadioGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton) mRadioGroup.getChildAt(indexOfChild);
        mAttendeeType = r.getText().toString();
        checkForErrors();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mProgressDialog = new ProgressDialog(this);
        realm = Realm.getDefaultInstance();
        mAttendee = mRealmRepo.getAttendeeFromRealm(firebaseUid);
        mFirstName.setText(mAttendee.getFirstName());
        mLastName.setText(mAttendee.getLastName());
        mEmail.setText(mAttendee.getEmail());
        mCompany.setText(mAttendee.getCompany());
        mRole.setText(mAttendee.getTitle());
        if (TextUtils.isEmpty(mEmail.getText().toString())) {
            mEmail.setEnabled(true);
        }
    }

    private void checkForErrors() {
        mTextFirstName.setError(null);
        mTextLastName.setError(null);
        mTextEmail.setError(null);
        mTextFirstName.setErrorEnabled(false);
        mTextLastName.setErrorEnabled(false);
        mTextEmail.setErrorEnabled(false);
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(mFirstName.getText().toString())) {
            mTextFirstName.setErrorEnabled(true);
            mTextFirstName.setError(getString(R.string.error_field_required));
            focusView = mFirstName;
            cancel = true;
        } else if (TextUtils.isEmpty(mLastName.getText().toString())) {
            mTextLastName.setErrorEnabled(true);
            mTextLastName.setError(getString(R.string.error_field_required));
            focusView = mLastName;
            cancel = true;
        } else if (TextUtils.isEmpty(mEmail.getText().toString())) {
            mTextEmail.setErrorEnabled(true);
            mTextEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgressDialog();
            updateEmailInFirebase();
        }
    }

    private void updateEmailInFirebase() {
        if (mEmail.isEnabled()) {
            FirebaseAuthUtil.getFirebaseAuthInstance().getCurrentUser().updateEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        storeDataInRealm();
                    } else {
                        hideProgressDialog();
                        LogUtils.LOGE(LOG_TAG, task.getException().getMessage());
                        mTextEmail.setErrorEnabled(true);
                        mTextEmail.setError("E-mail already exists. Please try another one");
                        mEmail.requestFocus();
                    }
                }
            });
        } else {
            storeDataInRealm();
        }
    }

    private void storeDataInRealm() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mAttendee.setFirstName(mFirstName.getText().toString());
                mAttendee.setLastName(mLastName.getText().toString());
                mAttendee.setEmail(mEmail.getText().toString());
                mAttendee.setAttendeeType(mAttendeeType);
                if (!TextUtils.isEmpty(mCompany.getText().toString())) {
                    mAttendee.setCompany(mCompany.getText().toString());
                }
                if (!TextUtils.isEmpty(mRole.getText().toString())) {
                    mAttendee.setTitle(mRole.getText().toString());
                }
            }
        });
        saveAttendeeInFirebase();
    }

    private void saveAttendeeInFirebase() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String attendeeJson = gson.toJson(realm.copyFromRealm(mAttendee));
        DatabaseReference mFireBaseDatabaseRef = FirebaseDatabaseUtil.getDatabase().getReference().child("Users").child(firebaseUid);
        Map<String, Object> jsonMap = gson.fromJson(attendeeJson, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        mFireBaseDatabaseRef.setValue(jsonMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressDialog();
                if (task.isSuccessful()) {
                    AccountUtils.setRegisterVisited(RegisterActivity.this);
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    LogUtils.LOGE(LOG_TAG, task.getException().getMessage());
                }
            }
        });
    }

    private void showProgressDialog() {
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.progressdialog_message));
        mProgressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_skip) {
            AccountUtils.setRegisterVisited(this);
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

