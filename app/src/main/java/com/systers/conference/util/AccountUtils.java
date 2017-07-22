package com.systers.conference.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static com.systers.conference.util.LogUtils.LOGE;
import static com.systers.conference.util.LogUtils.makeLogTag;

/**
 * Account and login utilities. This class manages a local shared preferences object
 * that stores which account is currently active, and can store associated information
 * such as Google+ profile info (name, image URL, cover URL) and also the auth token
 * associated with the account.
 */

public class AccountUtils {
    private static final String LOG_TAG = makeLogTag(AccountUtils.class);

    private static final String PREFIX_PREF_FIRST_NAME = "first_name_";
    private static final String PREFIX_PREF_LAST_NAME = "last_name_";
    private static final String PREFIX_PREF_EMAIL = "email_";
    private static final String PREFIX_PREF_COMPANY = "company_";
    private static final String PREFIX_PREF_ROLE = "role_";
    private static final String PREFIX_PREF_PHOTO_URL = "url_profile_photo_";
    private static final String PREFIX_PREF_GOOGLE_ID = "google_id_";
    private static final String PREFIX_PREF_FB_ID = "facebook_id";
    private static final String PREFIX_PREF_ACCESS_TOKEN = "access_token";
    private static final String PREFIX_PREF_REGISTER = "register_";
    private static final String PREFIX_PREF_LOGIN = "login_";
    private static final String PREFIX_PREF_PRE_REG = "pre_reg_";

    private static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean hasActiveGoogleAccount(final Context context) {
        return !TextUtils.isEmpty(getActiveGoogleAccount(context));
    }

    @Nullable
    public static String getActiveGoogleAccount(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_GOOGLE_ID) ? preferences.getString(PREFIX_PREF_GOOGLE_ID, null) : null;
    }

    public static void setActiveGoggleAccount(final Context context, final String accountId) {
        LOGE(LOG_TAG, "Set active google account id to: " + accountId);
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_GOOGLE_ID, accountId).apply();
    }

    public static void clearActiveGoogleAccount(final Context context) {
        LOGE(LOG_TAG, "Clearing Google Account");
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().remove(PREFIX_PREF_GOOGLE_ID).apply();
    }

    public static boolean hasActiveFacebookAccount(final Context context) {
        return !TextUtils.isEmpty(getActiveFacebookAccount(context));
    }

    @Nullable
    public static String getActiveFacebookAccount(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_FB_ID) ? preferences.getString(PREFIX_PREF_FB_ID, null) : null;
    }

    public static void setActiveFacebookAccount(final Context context, final String accountId) {
        LOGE(LOG_TAG, "Set active facebook account id to: " + accountId);
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_FB_ID, accountId).apply();
    }

    public static void clearActiveFacebookAccount(final Context context) {
        LOGE(LOG_TAG, "Clearing Facebook Account");
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().remove(PREFIX_PREF_FB_ID).apply();
    }

    public static void setFirstName(final Context context, String firstName) {
        LOGE(LOG_TAG, "Setting first name to: " + firstName);
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_FIRST_NAME, firstName).apply();
    }


    @Nullable
    public static String getFirstName(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_FIRST_NAME) ? preferences.getString(PREFIX_PREF_FIRST_NAME, null) : null;
    }

    public static void setLastName(final Context context, final String lastName) {
        LOGE(LOG_TAG, "Setting last name to: " + lastName);
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_LAST_NAME, lastName).apply();
    }


    @Nullable
    public static String getLastName(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_LAST_NAME) ? preferences.getString(PREFIX_PREF_LAST_NAME, null) : null;
    }

    public static void setEmail(final Context context, final String email) {
        LOGE(LOG_TAG, "Setting email to: " + email);
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_EMAIL, email).apply();
    }


    public static String getEmail(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_EMAIL) ? preferences.getString(PREFIX_PREF_EMAIL, null) : null;
    }

    public static void setProfilePictureUrl(final Context context, final String url) {
        LOGE(LOG_TAG, "Setting url to: " + url);
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_PHOTO_URL, url).apply();
    }

    @Nullable
    public static String getProfilePictureUrl(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_PHOTO_URL) ? preferences.getString(PREFIX_PREF_PHOTO_URL, null) : null;
    }

    public static void setCompanyName(final Context context, final String companyName) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_COMPANY, companyName).apply();
    }

    @Nullable
    public static String getCompanyName(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_COMPANY) ? preferences.getString(PREFIX_PREF_COMPANY, null) : null;
    }

    public static void setCompanyRole(final Context context, final String companyRole) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_ROLE, companyRole).apply();
    }

    @Nullable
    public static String getCompanyRole(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_ROLE) ? preferences.getString(PREFIX_PREF_ROLE, null) : null;
    }

    public static void setAccessToken(final Context context, final String accessToken) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_ACCESS_TOKEN, accessToken).apply();
        LOGE(LOG_TAG, "Setting access token to: " + accessToken);
    }

    @Nullable
    public static String getAccessToken(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_ACCESS_TOKEN) ? preferences.getString(PREFIX_PREF_ACCESS_TOKEN, null) : "";
    }

    public static void setRegisterVisited(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putBoolean(PREFIX_PREF_REGISTER, true).apply();
    }

    public static boolean getRegisterVisited(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_REGISTER);
    }

    public static void setLoginVisited(final Context context){
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putBoolean(PREFIX_PREF_LOGIN, true).apply();
    }

    public static boolean getLoginVisited(final Context context){
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_LOGIN);
    }

    public static void setRegistrationPreference(final Context context, String value) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(PREFIX_PREF_PRE_REG, value).apply();
    }

    public static String getRegistrationPreference(final Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFIX_PREF_PRE_REG) ? preferences.getString(PREFIX_PREF_PRE_REG, null) : null;
    }
}
