package com.systers.conference.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Session {
    @SerializedName("sessiondate")
    private String sessionDate;

    @SerializedName("starttime")
    private String startTime;

    @SerializedName("endtime")
    private String endTime;

    @SerializedName("reportname")
    private String sessionName;

    @SerializedName("descriptions")
    private Description description;

    @SerializedName("session_tracks")
    private List<String> tracks;

    @SerializedName("sessiontype")
    private String sessionType;

    @SerializedName("location")
    private String location;

    public String getSessionDate() {
        return sessionDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSessionName() {
        return sessionName;
    }

    public Description getDescription() {
        return description;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public String getSessionType() {
        return sessionType;
    }

    public String getLocation() {
        return location;
    }
}

class Description {
    @SerializedName("eng")
    private String englishDesc;
}
