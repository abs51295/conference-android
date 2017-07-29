package com.systers.conference;

import android.app.Application;
import android.content.Context;
import io.realm.Realm;

public class ConferenceApplication extends Application {
    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        Realm.init(this);
    }
}
