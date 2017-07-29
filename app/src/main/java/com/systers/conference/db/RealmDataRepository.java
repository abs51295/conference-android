package com.systers.conference.db;


import com.systers.conference.model.Session;
import com.systers.conference.model.Speaker;
import com.systers.conference.model.Track;
import com.systers.conference.util.LogUtils;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmDataRepository {
    private static final String LOG_TAG = LogUtils.makeLogTag(RealmDataRepository.class);
    private static RealmDataRepository realmDataRepository;

    private static HashMap<Realm, RealmDataRepository> repoCache = new HashMap<>();
    private Realm realm;

    private RealmDataRepository(Realm realm) {
        this.realm = realm;
    }

    public static RealmDataRepository getDefaultInstance() {
        if (realmDataRepository == null)
            realmDataRepository = new RealmDataRepository(Realm.getDefaultInstance());

        return realmDataRepository;
    }

    /**
     * For threaded operation, a separate Realm instance is needed, not the default
     * instance, and thus all Realm objects can not pass through threads, extra care
     * must be taken to close the Realm instance after use or else app will crash
     * onDestroy of MainActivity. This is to ensure the database remains compact and
     * application remains free of silent bugs
     *
     * @param realmInstance Separate Realm instance to be used
     * @return Realm Data Repository
     */
    public static RealmDataRepository getInstance(Realm realmInstance) {
        if (!repoCache.containsKey(realmInstance)) {
            repoCache.put(realmInstance, new RealmDataRepository(realmInstance));
        }
        return repoCache.get(realmInstance);
    }

    public static void compactDatabase() {
        Realm realm = realmDataRepository.getRealmInstance();
        Realm.compactRealm(realm.getConfiguration());
    }

    public Realm getRealmInstance() {
        return realm;
    }

    public void saveSessionInRealm(final String sessionId, final Session session) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                List<Speaker> speakers = session.getSpeakers();
                if (speakers != null && !speakers.isEmpty()) {
                    RealmList<Speaker> newSpeakers = new RealmList<>();
                    for (Speaker speaker : speakers) {
                        Speaker stored = bgRealm.where(Speaker.class).equalTo("name", speaker.getName()).findFirst();
                        if (stored != null) {
                            newSpeakers.add(stored);
                        } else {
                            newSpeakers.add(speaker);
                        }
                    }
                    session.setSpeakers(newSpeakers);
                }
                List<Track> tracks = session.getTracks();
                if (tracks != null && !tracks.isEmpty()) {
                    RealmList<Track> newTracks = new RealmList<>();
                    for (Track track : tracks) {
                        Track stored = bgRealm.where(Track.class).equalTo("name", track.getName()).findFirst();
                        if (stored != null) {
                            newTracks.add(stored);
                        } else {
                            newTracks.add(track);
                        }
                    }
                    session.setTracks(newTracks);
                }
                session.setId(sessionId);
                bgRealm.copyToRealmOrUpdate(session);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtils.LOGE(LOG_TAG, "Session Updated");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtils.LOGE(LOG_TAG, error.getMessage());
            }
        });
    }

    public void saveTrackInRealm(final Track track) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(track);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtils.LOGE(LOG_TAG, "Track Updated");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtils.LOGE(LOG_TAG, error.getMessage());
            }
        });
    }

    public void saveSpeakerInRealm(final Speaker speaker) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(speaker);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtils.LOGE(LOG_TAG, "Speaker Updated");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtils.LOGE(LOG_TAG, error.getMessage());
            }
        });
    }

    public void deleteSessionFromRealm(final String sessionId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmObject deleteSession = bgRealm.where(Session.class).equalTo("id", sessionId).findFirst();
                if (deleteSession.isValid()) {
                    deleteSession.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtils.LOGE(LOG_TAG, "Session deleted");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtils.LOGE(LOG_TAG, error.getMessage());
            }
        });
    }

    public void deleteTrackFromRealm(final String trackName) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmObject deleteTrack = bgRealm.where(Track.class).equalTo("name", trackName).findFirst();
                if (deleteTrack.isValid()) {
                    deleteTrack.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtils.LOGE(LOG_TAG, "Track deleted");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtils.LOGE(LOG_TAG, error.getMessage());
            }
        });
    }

    public void deleteSpeakerFromRealm(final String speakerName) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmObject deleteSpeaker = bgRealm.where(Speaker.class).equalTo("name", speakerName).findFirst();
                if (deleteSpeaker.isValid()) {
                    deleteSpeaker.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtils.LOGE(LOG_TAG, "Speaker Deleted");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtils.LOGE(LOG_TAG, error.getMessage());
            }
        });
    }
}
