package com.systers.conference.db;


import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.systers.conference.model.Attendee;
import com.systers.conference.model.Event;
import com.systers.conference.model.Session;
import com.systers.conference.model.Speaker;
import com.systers.conference.model.Track;
import com.systers.conference.util.LogUtils;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RealmDataRepository {
    private static final String LOG_TAG = LogUtils.makeLogTag(RealmDataRepository.class);
    private static RealmDataRepository realmDataRepository;
    private static HashMap<Realm, RealmDataRepository> repoCache = new HashMap<>();
    private Realm mRealm;

    private RealmDataRepository(Realm realm) {
        mRealm = realm;
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

    private Realm getRealmInstance() {
        return mRealm;
    }

    public void saveSessionInRealm(final String sessionId, final Session session) {
        Session storedSession = mRealm.where(Session.class).equalTo("id", sessionId).findFirst();
        if (storedSession == null) {
            updateSessionInRealm(sessionId, session);
        }
    }

    public void updateSessionInRealm(final String sessionId, final Session session) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                List<Speaker> speakers = session.getSpeakers();
                if (speakers != null && !speakers.isEmpty()) {
                    RealmList<Speaker> newSpeakers = new RealmList<>();
                    for (Speaker speaker : speakers) {
                        Speaker stored = bgRealm.where(Speaker.class).equalTo("id", speaker.getId()).findFirst();
                        if (stored != null) {
                            newSpeakers.add(stored);
                        } else {
                            LogUtils.LOGE("Speaker", "New added");
                            newSpeakers.add(speaker);
                        }
                    }
                    session.setSpeakers(newSpeakers);
                }
                List<Track> tracks = session.getTracks();
                if (tracks != null && !tracks.isEmpty()) {
                    RealmList<Track> newTracks = new RealmList<>();
                    for (Track track : tracks) {
                        Track stored = bgRealm.where(Track.class).equalTo("id", track.getId()).findFirst();
                        if (stored != null) {
                            newTracks.add(stored);
                        } else {
                            newTracks.add(track);
                        }
                    }
                    session.setTracks(newTracks);
                }
                session.setId(sessionId);
                bgRealm.insertOrUpdate(session);
                LogUtils.LOGE(LOG_TAG, "Session update called");
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

    public RealmResults<Session> getSessionsByDay(final String sessionDate) {
        return mRealm.where(Session.class).equalTo("sessiondate", sessionDate).findAllSortedAsync("starttime");
    }

    public Session getSessionById(final String sessionId) {
        return mRealm.where(Session.class).equalTo("id", sessionId).findFirst();
    }

    public RealmResults<Session> getBookmarkedSessions(final String sessionDate) {
        return mRealm.where(Session.class).equalTo("sessiondate", sessionDate).equalTo("isBookmarked", true).findAllSortedAsync("starttime");
    }

    public void setBookmark(final String sessionId, final boolean bookmark) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.where(Session.class).equalTo("id", sessionId).findFirst().setBookmarked(bookmark);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtils.LOGE(LOG_TAG, "Bookmark toggled");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtils.LOGE(LOG_TAG, error.getMessage());
            }
        });
    }

    public void deleteSessionFromRealm(final String sessionId) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
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

    public void saveTrackInRealm(final String trackId, final Track track) {
        Track searchTrack = mRealm.where(Track.class).equalTo("id", trackId).findFirst();
        if (searchTrack == null) {
            updateTrackInRealm(trackId, track);
        }
    }

    public void updateTrackInRealm(final String trackId, final Track track) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                track.setId(trackId);
                bgRealm.insertOrUpdate(track);
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

    public void deleteTrackFromRealm(final String trackId) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmObject deleteTrack = bgRealm.where(Track.class).equalTo("id", trackId).findFirst();
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

    public void saveSpeakerInRealm(final String speakerId, final Speaker speaker) {
        Speaker searchSpeaker = mRealm.where(Speaker.class).equalTo("id", speakerId).findFirst();
        if (searchSpeaker == null) {
            updateSpeakerInRealm(speakerId, speaker);
        }
    }

    public void updateSpeakerInRealm(final String speakerId, final Speaker speaker) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                speaker.setId(speakerId);
                bgRealm.insertOrUpdate(speaker);
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

    public void deleteSpeakerFromRealm(final String speakerId) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmObject deleteSpeaker = bgRealm.where(Speaker.class).equalTo("id", speakerId).findFirst();
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

    public Speaker getSpeakerFromRealm(final String speakerId) {
        return mRealm.where(Speaker.class).equalTo("id", speakerId).findFirstAsync();
    }

    public void saveEventInRealm(final String eventId, final Event event) {
        Event storedEvent = mRealm.where(Event.class).equalTo("id", eventId).findFirst();
        if (storedEvent == null) {
            updateEventInRealm(eventId, event);
        }
    }

    public void updateEventInRealm(final String eventId, final Event event) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                event.setId(eventId);
                bgRealm.insertOrUpdate(event);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtils.LOGE(LOG_TAG, "Event Updated");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtils.LOGE(LOG_TAG, error.getMessage());
            }
        });
    }

    public void deleteEventFromRealm(final String eventId) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmObject deleteEvent = bgRealm.where(Event.class).equalTo("id", eventId).findFirst();
                if (deleteEvent.isValid()) {
                    deleteEvent.deleteFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtils.LOGE(LOG_TAG, "Event Deleted");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                LogUtils.LOGE(LOG_TAG, error.getMessage());
            }
        });
    }

    public RealmResults<Event> getEvent() {
        return mRealm.where(Event.class).findAllAsync();
    }

    public void saveAttendeeInRealmSync(final String attendeeId, final Attendee attendee, final boolean isRegistered, final String providerId) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                switch (providerId) {
                    case GoogleAuthProvider.PROVIDER_ID:
                        attendee.setGoogleLoggedIn(true);
                        break;
                    case TwitterAuthProvider.PROVIDER_ID:
                        attendee.setTwitterLoggedIn(true);
                        break;
                    default:
                        LogUtils.LOGE(LOG_TAG, "Provider not found");
                        break;
                }
                attendee.setUid(attendeeId);
                attendee.setRegistered(isRegistered);
                bgRealm.insertOrUpdate(attendee);
            }
        });
    }

    public void updateAttendeeInRealmSync(final Attendee attendee) {
        LogUtils.LOGE(LOG_TAG, "Attendee updated");
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(attendee);
            }
        });
    }

    public Attendee getAttendeeCopyFromRealmSync(final String attendeeId) {
        Attendee attendee = mRealm.where(Attendee.class).equalTo("uid", attendeeId).findFirst();
        return mRealm.copyFromRealm(attendee);
    }

    public Attendee getAttendeeFromRealmSync(final String attendeeId) {
        return mRealm.where(Attendee.class).equalTo("uid", attendeeId).findFirst();
    }
}
