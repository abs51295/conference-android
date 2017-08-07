package com.systers.conference.model;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Speaker extends RealmObject {
    @LinkingObjects("speakers")
    private final RealmResults<Session> sessions = null;
    @PrimaryKey
    private String id;
    private String name;
    private String role;
    private String company;
    private String avatar_url;
    private String description;
    private String email;
    private String google_plus_url;
    private String fb_url;
    private String twitter_url;
    private String linkedin_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGooglePlusUrl() {
        return google_plus_url;
    }

    public void setGooglePlusUrl(String google_plus_url) {
        this.google_plus_url = google_plus_url;
    }

    public String getFbUrl() {
        return fb_url;
    }

    public void setFbUrl(String fb_url) {
        this.fb_url = fb_url;
    }

    public String getTwitterUrl() {
        return twitter_url;
    }

    public void setTwitterUrl(String twitter_url) {
        this.twitter_url = twitter_url;
    }

    public String getLinkedInUrl() {
        return linkedin_url;
    }

    public void setLinkedInUrl(String linkedin_url) {
        this.linkedin_url = linkedin_url;
    }

    public RealmResults<Session> getSessions() {
        return sessions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
