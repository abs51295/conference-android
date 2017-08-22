package com.systers.conference;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Event;
import com.systers.conference.model.Session;
import com.systers.conference.model.Speaker;
import com.systers.conference.model.Track;
import com.systers.conference.util.FirebaseAuthUtil;
import com.systers.conference.util.FirebaseDatabaseUtil;
import com.systers.conference.util.LogUtils;

/**
 * Extend this activity to provide real-time updation of Realm DB.
 * Provides an app-wide mechanism to sync Firebase and Realm.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = LogUtils.makeLogTag(BaseActivity.class);
    protected String firebaseUid;
    private DatabaseReference mFireBaseDatabaseRef;
    private ChildEventListener mSessionChildListener;
    private ChildEventListener mSpeakerChildListener;
    private ChildEventListener mTrackChildListener;
    private ChildEventListener mEventChildListener;
    private RealmDataRepository mRealmRepo = RealmDataRepository.getDefaultInstance();
    private Gson gson = new GsonBuilder().create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFireBaseDatabaseRef = FirebaseDatabaseUtil.getDatabase().getReference();
        FirebaseUser currentUser = FirebaseAuthUtil.getFirebaseAuthInstance().getCurrentUser();
        if (currentUser != null) {
            firebaseUid = currentUser.getUid();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSessionChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRealmRepo.saveSessionInRealm(dataSnapshot.getKey(), getSessionFromDataSnapshot(dataSnapshot));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mRealmRepo.updateSessionInRealm(dataSnapshot.getKey(), getSessionFromDataSnapshot(dataSnapshot));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mRealmRepo.deleteSessionFromRealm(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                LogUtils.LOGD(LOG_TAG, s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtils.LOGE(LOG_TAG, databaseError.getMessage());
            }
        };
        mSpeakerChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRealmRepo.saveSpeakerInRealm(dataSnapshot.getKey(), getSpeakerFromDataSnapshot(dataSnapshot));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mRealmRepo.updateSpeakerInRealm(dataSnapshot.getKey(), getSpeakerFromDataSnapshot(dataSnapshot));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mRealmRepo.deleteSpeakerFromRealm(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                LogUtils.LOGD(LOG_TAG, s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtils.LOGE(LOG_TAG, databaseError.getMessage());
            }
        };
        mTrackChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRealmRepo.saveTrackInRealm(dataSnapshot.getKey(), getTrackFromDataSnapshot(dataSnapshot));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mRealmRepo.updateTrackInRealm(dataSnapshot.getKey(), getTrackFromDataSnapshot(dataSnapshot));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mRealmRepo.deleteTrackFromRealm(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                LogUtils.LOGD(LOG_TAG, s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtils.LOGE(LOG_TAG, databaseError.getMessage());
            }
        };
        mEventChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRealmRepo.saveEventInRealm(dataSnapshot.getKey(), getEventFromDataSnapshot(dataSnapshot));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mRealmRepo.updateEventInRealm(dataSnapshot.getKey(), getEventFromDataSnapshot(dataSnapshot));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mRealmRepo.deleteEventFromRealm(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                LogUtils.LOGD(LOG_TAG, s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LogUtils.LOGE(LOG_TAG, databaseError.getMessage());
            }
        };
        mFireBaseDatabaseRef.child("Event").addChildEventListener(mEventChildListener);
        mFireBaseDatabaseRef.child("Speakers").addChildEventListener(mSpeakerChildListener);
        mFireBaseDatabaseRef.child("Tracks").addChildEventListener(mTrackChildListener);
        mFireBaseDatabaseRef.child("Sessions").addChildEventListener(mSessionChildListener);
    }

    private Session getSessionFromDataSnapshot(DataSnapshot dataSnapshot) {
        String sessionJson = gson.toJson(dataSnapshot.getValue());
        return gson.fromJson(sessionJson, new TypeToken<Session>() {
        }.getType());
    }

    private Speaker getSpeakerFromDataSnapshot(DataSnapshot dataSnapshot) {
        String speakerJson = gson.toJson(dataSnapshot.getValue());
        return gson.fromJson(speakerJson, new TypeToken<Speaker>() {
        }.getType());
    }

    private Track getTrackFromDataSnapshot(DataSnapshot dataSnapshot) {
        String trackJson = gson.toJson(dataSnapshot.getValue());
        return gson.fromJson(trackJson, new TypeToken<Track>() {
        }.getType());
    }

    private Event getEventFromDataSnapshot(DataSnapshot dataSnapshot) {
        String eventJson = gson.toJson(dataSnapshot.getValue());
        return gson.fromJson(eventJson, new TypeToken<Event>() {
        }.getType());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSessionChildListener != null) {
            mFireBaseDatabaseRef.child("Sessions").removeEventListener(mSessionChildListener);
        }
        if (mSpeakerChildListener != null) {
            mFireBaseDatabaseRef.child("Speakers").removeEventListener(mSpeakerChildListener);
        }
        if (mTrackChildListener != null) {
            mFireBaseDatabaseRef.child("Tracks").removeEventListener(mTrackChildListener);
        }
        if (mEventChildListener != null) {
            mFireBaseDatabaseRef.child("Event").removeEventListener(mEventChildListener);
        }
    }

    @Override
    protected void onDestroy() {
        RealmDataRepository.compactDatabase();
        super.onDestroy();
    }
}
