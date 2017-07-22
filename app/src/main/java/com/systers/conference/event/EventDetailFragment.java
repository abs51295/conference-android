package com.systers.conference.event;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.systers.conference.R;
import com.systers.conference.api.DataDownloadManager;
import com.systers.conference.callback.ObjectResponseCallback;
import com.systers.conference.model.Speaker;
import com.systers.conference.speaker.SpeakerDetailsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventDetailFragment extends Fragment implements ObjectResponseCallback<Speaker> {

    @BindView(R.id.speakers_container)
    ViewGroup mSpeakers;

    private Unbinder unbinder;
    private ProgressDialog progressDialog;

    public EventDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(getActivity());
        addSpeakers(inflater);
        return view;
    }

    private void addSpeakers(LayoutInflater inflater) {
        for (int i = 0; i < 3; i++) {
            View speakers = inflater.inflate(R.layout.speaker_list_item, mSpeakers, false);
            mSpeakers.addView(speakers);
            speakers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage(getString(R.string.progressdialog_message));
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    //TODO: Change speaker id dynamically once database is set up.
                    DataDownloadManager.getInstance().getSpeaker(EventDetailFragment.this, "731188");
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void OnSuccess(Speaker response) {
        progressDialog.dismiss();
        Intent intent = new Intent(getActivity(), SpeakerDetailsActivity.class);
        intent.putExtra(getString(R.string.speaker_data), new Gson().toJson(response));
        startActivity(intent);
    }

    @Override
    public void OnFailure(Throwable error) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), R.string.speaker_download_failed, Toast.LENGTH_SHORT).show();
    }
}
