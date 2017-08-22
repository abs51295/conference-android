package com.systers.conference.model;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Attendee extends RealmObject {
    @PrimaryKey
    private String uid;
    @Expose
    private String fname;
    @Expose
    private String lname;
    @Expose
    private String email;
    @Expose
    private String attendeeType;
    private boolean isRegistered;
    @Expose
    private String company = "";
    @Expose
    private String title = "";
    private String avatarUrl;
    private boolean isGoogleLoggedIn = false;
    private boolean isTwitterLoggedIn = false;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return fname;
    }

    public void setFirstName(String fname) {
        this.fname = fname;
    }

    public String getLastName() {
        return lname;
    }

    public void setLastName(String lname) {
        this.lname = lname;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAttendeeType() {
        return attendeeType;
    }

    public void setAttendeeType(String attendeeType) {
        this.attendeeType = attendeeType;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isGoogleLoggedIn() {
        return isGoogleLoggedIn;
    }

    public void setGoogleLoggedIn(boolean googleLoggedIn) {
        isGoogleLoggedIn = googleLoggedIn;
    }

    public boolean isTwitterLoggedIn() {
        return isTwitterLoggedIn;
    }

    public void setTwitterLoggedIn(boolean twitterLoggedIn) {
        isTwitterLoggedIn = twitterLoggedIn;
    }
}
