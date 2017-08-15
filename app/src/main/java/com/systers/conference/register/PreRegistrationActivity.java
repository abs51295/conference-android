package com.systers.conference.register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.google.gson.Gson;
import com.systers.conference.MainActivity;
import com.systers.conference.R;
import com.systers.conference.api.DataDownloadManager;
import com.systers.conference.callback.ListResponseCallback;
import com.systers.conference.model.Question;
import com.systers.conference.util.AccountUtils;
import com.systers.conference.util.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PreRegistrationActivity extends AppCompatActivity implements ListResponseCallback<Question> {

    @BindView(R.id.pre_registration_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.yes)
    Button mYes;
    @BindView(R.id.no)
    Button mNo;
    private ProgressDialog mProgress;

    @OnClick(R.id.yes)
    public void proceedToRegistration() {
        mProgress.setIndeterminate(true);
        mProgress.setCancelable(false);
        mProgress.setMessage(getString(R.string.progressdialog_message));
        mProgress.show();
        DataDownloadManager.getInstance().downloadQuestions(this, null);
    }

    @OnClick(R.id.no)
    public void skipRegistration() {
        LogUtils.LOGE("HEHE", "No clicked");
        AccountUtils.setRegistrationPreference(this, "no");
        startActivity(new Intent(this, MainActivity.class));
        ActivityCompat.finishAffinity(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.LOGE("HEHE", "Oncreate called");
        setContentView(R.layout.activity_pre_registration);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mProgress = new ProgressDialog(this);
        if (AccountUtils.getRegistrationPreference(this) != null) {
            if (AccountUtils.getRegistrationPreference(this).equals("yes") && AccountUtils.getRegisterVisited(this)) {
                startActivity(new Intent(this, MainActivity.class));
                ActivityCompat.finishAffinity(this);
            } else if (AccountUtils.getRegistrationPreference(this).equals("yes")) {
                proceedToRegistration();
            } else {
                skipRegistration();
            }
        }
    }

    @Override
    public void onSuccess(List<Question> response) {
        mProgress.dismiss();
        AccountUtils.setRegistrationPreference(this, "yes");
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(getString(R.string.registration), new Gson().toJson(response));
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
    }
}
