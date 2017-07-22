package com.systers.conference.model;


import com.google.gson.annotations.SerializedName;

public class Speaker {
    @SerializedName("speakerid")
    private String id;

    @SerializedName("fname")
    private String firstName;

    @SerializedName("lname")
    private String lastName;

    @SerializedName("image")
    private String imageId;

    private String email;
    private String title;
    private String company;
    private String bio;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getBio() {
        return bio;
    }

    public String getImageId() {
        return imageId;
    }
}
