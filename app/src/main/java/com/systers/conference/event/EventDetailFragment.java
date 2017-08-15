package com.systers.conference.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.systers.conference.R;
import com.systers.conference.model.Speaker;
import com.systers.conference.speaker.SpeakerDetailsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventDetailFragment extends Fragment {

    @BindView(R.id.speakers_container)
    ViewGroup mSpeakers;
    private Unbinder unbinder;

    public EventDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
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
                    Intent intent = new Intent(getActivity(), SpeakerDetailsActivity.class);
                    Speaker response = new Speaker();
                    response.setFirstName("Marc");
                    response.setLastName("Benioff");
                    response.setCompany("Salesforce");
                    response.setTitle("Chairman and CEO");
                    response.setBio("Marc Russell Benioff is an American internet entrepreneur, author and philanthropist. He is the founder, chairman and CEO of Salesforce, a large enterprise cloud computing company.");
                    intent.putExtra(getString(R.string.speaker_data), new Gson().toJson(response));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
