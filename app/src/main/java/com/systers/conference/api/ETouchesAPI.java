package com.systers.conference.api;

import com.systers.conference.model.AccessToken;
import com.systers.conference.model.AttendeeId;
import com.systers.conference.model.Page;
import com.systers.conference.model.Question;
import com.systers.conference.model.Session;
import com.systers.conference.model.SessionList;
import com.systers.conference.model.Speaker;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ETouchesAPI {
    @GET("global/authorize.json")
    Call<AccessToken> getAccessToken(@Query("accountid") int accountId, @Query("key") String accountKey);

    @GET("ereg/listSessions.json")
    Call<List<SessionList>> getSessionList(@Query("accesstoken") String accessToken, @Query("eventid") int eventId);

    @GET("ereg/getSession.json")
    Call<Session> getSession(@Query("accesstoken") String accessToken, @Query("eventid") int eventId, @Query("sessionid") String sessionId, @Query("sessionkey") String sessionKey);

    @GET("ereg/listPages.json")
    Call<List<Page>> getPages(@Query("accesstoken") String accessToken, @Query("eventid") int eventId);

    @GET("ereg/listQuestions.json")
    Call<List<Question>> getQuestions(@Query("accesstoken") String accessToken, @Query("eventid") int eventId, @Query("pageid") Integer pageId);

    @GET("ereg/getSpeaker.json")
    Call<Speaker> getSpeaker(@Query("accesstoken") String accessToken, @Query("eventid") int eventId, @Query("speakerid") String speakerId);

    @FormUrlEncoded
    @POST("ereg/createAttendee.json")
    Call<AttendeeId> postResponses(@Field("accesstoken") String accesstoken, @Query("eventid") int eventId, @FieldMap(encoded = true) Map<String, String> responses);
}
