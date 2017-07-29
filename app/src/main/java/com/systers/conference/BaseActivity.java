package com.systers.conference;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Session;
import com.systers.conference.model.Speaker;
import com.systers.conference.model.Track;

/**
 * Extend this activity to provide real-time updation of Realm DB.
 * Provides an app-wide mechanism to sync Firebase and Realm.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private DatabaseReference mFireBaseDatabaseRef;
    private ChildEventListener mSessionChildListener;
    private ChildEventListener mSpeakerChildListener;
    private ChildEventListener mTrackChildListener;
    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFireBaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSessionChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Gson gson = new GsonBuilder().create();
                String jsonString = gson.toJson(dataSnapshot.getValue());
                Session session = gson.fromJson(jsonString, new TypeToken<Session>() {
                }.getType());
                realmRepo.saveSessionInRealm(dataSnapshot.getKey(), session);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Gson gson = new GsonBuilder().create();
                String sessionJson = gson.toJson(dataSnapshot.getValue());
                Session session = gson.fromJson(sessionJson, new TypeToken<Session>() {
                }.getType());
                realmRepo.deleteSessionFromRealm(session.getId());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mSpeakerChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Gson gson = new GsonBuilder().create();
                String speakerJson = gson.toJson(dataSnapshot.getValue());
                Speaker speaker = gson.fromJson(speakerJson, new TypeToken<Speaker>() {
                }.getType());
                realmRepo.saveSpeakerInRealm(speaker);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Gson gson = new GsonBuilder().create();
                String speakerJson = gson.toJson(dataSnapshot.getValue());
                Speaker speaker = gson.fromJson(speakerJson, new TypeToken<Speaker>() {
                }.getType());
                realmRepo.deleteSpeakerFromRealm(speaker.getName());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mTrackChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Gson gson = new GsonBuilder().create();
                String trackJson = gson.toJson(dataSnapshot.getValue());
                Track track = gson.fromJson(trackJson, new TypeToken<Track>() {
                }.getType());
                realmRepo.saveTrackInRealm(track);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Gson gson = new GsonBuilder().create();
                String trackJson = gson.toJson(dataSnapshot.getValue());
                Track track = gson.fromJson(trackJson, new TypeToken<Track>() {
                }.getType());
                realmRepo.deleteTrackFromRealm(track.getName());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mFireBaseDatabaseRef.child("Sessions").addChildEventListener(mSessionChildListener);
        mFireBaseDatabaseRef.child("Speakers").addChildEventListener(mSpeakerChildListener);
        mFireBaseDatabaseRef.child("Tracks").addChildEventListener(mTrackChildListener);
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
    }

    @Override
    protected void onDestroy() {
        RealmDataRepository.compactDatabase();
        super.onDestroy();
    }
}
