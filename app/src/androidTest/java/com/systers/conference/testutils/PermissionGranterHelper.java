package com.systers.conference.testutils;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class PermissionGranterHelper {
    private static final int PERMISSIONS_DIALOG_DELAY = 1000;
    private static final String PERMISSIONS_DIALOG_ALLOW_ID = "com.android.packageinstaller:id/permission_allow_button";
    private static final String PERMISSIONS_DIALOG_DENY_ID = "com.android.packageinstaller:id/permission_deny_button";

    public static void grantPermissions() {
        try {
            Thread.sleep(PERMISSIONS_DIALOG_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject allowPermissions = device.findObject(new UiSelector()
                .clickable(true)
                .checkable(false)
                .resourceId(PERMISSIONS_DIALOG_ALLOW_ID));
        if (allowPermissions.exists()) {
            try {
                allowPermissions.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void denyPermissions() {
        try {
            Thread.sleep(PERMISSIONS_DIALOG_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject allowPermissions = device.findObject(new UiSelector()
                .clickable(true)
                .checkable(false)
                .resourceId(PERMISSIONS_DIALOG_DENY_ID));
        if (allowPermissions.exists()) {
            try {
                allowPermissions.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
