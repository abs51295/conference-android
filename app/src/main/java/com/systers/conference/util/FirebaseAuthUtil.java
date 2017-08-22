package com.systers.conference.util;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthUtil {
    private static FirebaseAuth mFirebaseAuth;

    public static FirebaseAuth getFirebaseAuthInstance() {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }
}
