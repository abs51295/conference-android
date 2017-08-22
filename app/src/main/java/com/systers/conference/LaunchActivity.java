package com.systers.conference;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.systers.conference.login.LoginActivity;
import com.systers.conference.register.RegisterActivity;
import com.systers.conference.util.AccountUtils;
import com.systers.conference.util.FirebaseAuthUtil;

import java.util.Timer;
import java.util.TimerTask;

public class LaunchActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final boolean firebaseUser = FirebaseAuthUtil.getFirebaseAuthInstance().getCurrentUser() != null;
        final boolean registerVisited = AccountUtils.getRegisterVisited(this);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (firebaseUser) {
                    if (registerVisited) {
                        startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(LaunchActivity.this, RegisterActivity.class));
                    }
                } else {
                    startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 1000);
    }
}
