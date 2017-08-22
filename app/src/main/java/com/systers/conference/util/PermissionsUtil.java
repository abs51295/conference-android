package com.systers.conference.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Utility class that wraps access to the runtime permissions API in M and provides basic helper
 * methods.
 */

public abstract class PermissionsUtil {

    /**
     * @param permission The permission to check for.
     * @param context    Context returned by getActivity().
     * @return true if permission is granted.
     */
    private static boolean isPermissionGranted(String permission, Context context) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param permissions Permissions required at run time.
     * @param context     Context returned by getActivity().
     * @return true if all permissions are granted.
     */
    public static boolean areAllRunTimePermissionsGranted(String[] permissions, Context context) {
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            allPermissionsGranted = allPermissionsGranted && isPermissionGranted(permission, context);
        }
        return allPermissionsGranted;
    }
}
