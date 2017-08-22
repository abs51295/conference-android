package com.systers.conference.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.systers.conference.util.LogUtils.makeLogTag;

/**
 * Account and login utilities. This class manages a local shared preferences object
 * that stores which account is currently active, and can store associated information
 * such as Google+ profile info (name, image URL, cover URL) and also the auth token
 * associated with the account.
 */

public class AccountUtils {
    private static final String LOG_TAG = makeLogTag(AccountUtils.class);
    private static final String PREFIX_PREF_REGISTER = "register_";

    private static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setRegisterVisited(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putBoolean(PREFIX_PREF_REGISTER, true).apply();
    }

    public static boolean getRegisterVisited(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_REGISTER);
    }
}
