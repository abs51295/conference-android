package com.systers.conference.model;


import com.google.gson.annotations.SerializedName;

public class Question {
    @SerializedName("questionid")
    private String questionId;

    @SerializedName("fieldname")
    private String fieldName;

    @SerializedName("inputtype")
    private String inputType;

    @SerializedName("name")
    private String displayName;

    @SerializedName("pageid")
    private String pageId;

    @SerializedName("page")
    private String pageName;

    public String getQuestionId() {
        return questionId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getInputType() {
        return Integer.valueOf(inputType);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPageId() {
        return pageId;
    }

    public String getPageName() {
        return pageName;
    }
}
