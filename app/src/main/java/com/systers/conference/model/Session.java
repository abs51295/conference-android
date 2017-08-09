package com.systers.conference.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Session extends RealmObject {
    @PrimaryKey
    private String id;
    @Index
    private String name;
    private String sessiondate;
    private String starttime;
    private String endtime;
    private String sessiontype;
    private String location;
    private String description;
    private boolean isBookmarked;
    private RealmList<Track> tracks;
    private RealmList<Speaker> speakers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSessionDate() {
        return sessiondate;
    }

    public void setSessionDate(String sessiondate) {
        this.sessiondate = sessiondate;
    }

    public String getStartTime() {
        return starttime;
    }

    public void setStartTime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndTime() {
        return endtime;
    }

    public void setEndTime(String endtime) {
        this.endtime = endtime;
    }

    public String getSessionType() {
        return sessiontype;
    }

    public void setSessionType(String sessiontype) {
        this.sessiontype = sessiontype;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RealmList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(RealmList<Track> tracks) {
        this.tracks = tracks;
    }

    public RealmList<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(RealmList<Speaker> speakers) {
        this.speakers = speakers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }
}