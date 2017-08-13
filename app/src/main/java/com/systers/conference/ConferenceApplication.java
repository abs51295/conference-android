package com.systers.conference;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .debug(true)
                .twitterAuthConfig(new TwitterAuthConfig(BuildConfig.TwitterApiKey, BuildConfig.TwitterSecretKey)).build();
        Twitter.initialize(twitterConfig);
    }
}
