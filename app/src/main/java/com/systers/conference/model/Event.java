package com.systers.conference.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String startdate;
    private String enddate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startdate;
    }

    public void setStartDate(String startDate) {
        this.startdate = startDate;
    }

    public String getEndDate() {
        return enddate;
    }

    public void setEndDate(String endDate) {
        this.enddate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
