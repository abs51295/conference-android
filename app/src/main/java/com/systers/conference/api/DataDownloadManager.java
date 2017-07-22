package com.systers.conference.api;


import com.systers.conference.ConferenceApplication;
import com.systers.conference.callback.ListResponseCallback;
import com.systers.conference.callback.ObjectResponseCallback;
import com.systers.conference.model.AccessToken;
import com.systers.conference.model.AttendeeId;
import com.systers.conference.model.Question;
import com.systers.conference.model.SessionList;
import com.systers.conference.model.Speaker;
import com.systers.conference.util.APIUtils;
import com.systers.conference.util.AccountUtils;
import com.systers.conference.util.LogUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class DataDownloadManager {
    private static final String LOG_TAG = LogUtils.makeLogTag(DataDownloadManager.class);
    private static DataDownloadManager instance;
    private APIClient client = new APIClient();


    private DataDownloadManager() {
    }

    public static DataDownloadManager getInstance() {
        if (instance == null) {
            instance = new DataDownloadManager();
        }
        return instance;
    }

    public void downloadToken() {
        try {
            AccessToken accessToken = client.geteTouchesAPI().getAccessToken(APIUtils.ACCOUNT_ID, APIUtils.ACCOUNT_KEY)
                    .execute().body();
            AccountUtils.setAccessToken(ConferenceApplication.getAppContext(), accessToken.getToken());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadQuestions(final ListResponseCallback<Question> questionListResponseCallback, Integer pageId) {
        client.geteTouchesAPI().getQuestions(AccountUtils.getAccessToken(ConferenceApplication.getAppContext()), APIUtils.EVENT_ID, pageId).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                questionListResponseCallback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                LogUtils.LOGE(LOG_TAG, t.toString());
            }
        });
    }

    public void createAttendee(final ObjectResponseCallback<AttendeeId> attendeeIdObjectResponseCallback, Map<String, String> responses) {
        client.geteTouchesAPI().postResponses(AccountUtils.getAccessToken(ConferenceApplication.getAppContext()), APIUtils.EVENT_ID, responses)
                .enqueue(new Callback<AttendeeId>() {
                    @Override
                    public void onResponse(Call<AttendeeId> call, Response<AttendeeId> response) {
                        attendeeIdObjectResponseCallback.OnSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<AttendeeId> call, Throwable t) {
                        attendeeIdObjectResponseCallback.OnFailure(t);
                    }
                });
    }

    public void downloadSessionList() {
        client.geteTouchesAPI().getSessionList(AccountUtils.getAccessToken(ConferenceApplication.getAppContext()), APIUtils.EVENT_ID).enqueue(new Callback<List<SessionList>>() {
            @Override
            public void onResponse(Call<List<SessionList>> call, Response<List<SessionList>> response) {
                if (response.isSuccessful()) {
                    for (SessionList sessionList : response.body()) {
                        LogUtils.LOGE(LOG_TAG, sessionList.getSessionId());
                        LogUtils.LOGE(LOG_TAG, sessionList.getSessionKey());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SessionList>> call, Throwable t) {
                LogUtils.LOGE(LOG_TAG, t.toString());
            }
        });
    }

    public void getSpeaker(final ObjectResponseCallback<Speaker> speakerObjectResponseCallback, String speakerId) {
        client.geteTouchesAPI().getSpeaker(AccountUtils.getAccessToken(ConferenceApplication.getAppContext()), APIUtils.EVENT_ID, speakerId).enqueue(new Callback<Speaker>() {
            @Override
            public void onResponse(Call<Speaker> call, Response<Speaker> response) {
                speakerObjectResponseCallback.OnSuccess(response.body());
            }

            @Override
            public void onFailure(Call<Speaker> call, Throwable t) {
                speakerObjectResponseCallback.OnFailure(t);
            }
        });
    }
}
