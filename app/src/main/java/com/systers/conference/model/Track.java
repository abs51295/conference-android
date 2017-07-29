package com.systers.conference.model;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;


public class Track extends RealmObject {
    @LinkingObjects("tracks")
    private final RealmResults<Session> sessions = null;
    @PrimaryKey
    private String name;
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public RealmResults<Session> getSessions() {
        return sessions;
    }
}
