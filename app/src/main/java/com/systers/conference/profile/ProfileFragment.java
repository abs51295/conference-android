package com.systers.conference.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.systers.conference.R;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Attendee;
import com.systers.conference.util.FirebaseAuthUtil;
import com.systers.conference.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static String LOG_TAG = LogUtils.makeLogTag(ProfileFragment.class);
    @BindView(R.id.avatar)
    CircleImageView mAvatar;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.subhead)
    TextView mSubHead;
    @BindView(R.id.connected)
    TextView mConnected;
    @BindView(R.id.google_plus_box)
    ImageView mGooglePlus;
    @BindView(R.id.twitter_box)
    ImageView mTwitter;
    private Unbinder mUnbinder;
    private RealmDataRepository mRealmRepo = RealmDataRepository.getDefaultInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.edit_profile)
    public void startEditProfileActivity() {
        startActivity(new Intent(getActivity(), EditProfileActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        Attendee mAttendee = mRealmRepo.getAttendeeFromRealmSync(FirebaseAuthUtil.getFirebaseAuthInstance().getCurrentUser().getUid());
        if (mAttendee.getAvatarUrl() != null) {
            LogUtils.LOGE(LOG_TAG, mAttendee.getAvatarUrl());
            Picasso.with(getActivity()).load(Uri.parse(mAttendee.getAvatarUrl()))
                    .resize(getResources().getInteger(R.integer.avatar_dimen), getResources().getInteger(R.integer.avatar_dimen))
                    .placeholder(R.drawable.male_icon_9_glasses)
                    .error(R.drawable.male_icon_9_glasses)
                    .centerCrop()
                    .into(mAvatar);
        } else {
            mAvatar.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.male_icon_9_glasses));
        }
        mName.setText(mAttendee.getFirstName() + " " + mAttendee.getLastName());
        if (!TextUtils.isEmpty(mAttendee.getTitle())) {
            mSubHead.setText(mAttendee.getTitle());
        }
        if (!TextUtils.isEmpty(mAttendee.getCompany())) {
            String text;
            if (!TextUtils.isEmpty(mSubHead.getText().toString())) {
                text = mSubHead.getText().toString() + ", " + mAttendee.getCompany();
            } else {
                text = mAttendee.getCompany();
            }
            mSubHead.setText(text);
        }
        if (!TextUtils.isEmpty(mSubHead.getText().toString())) {
            mSubHead.setVisibility(View.VISIBLE);
        }
        if (mAttendee.isGoogleLoggedIn() || mAttendee.isTwitterLoggedIn()) {
            mConnected.setVisibility(View.VISIBLE);
        }
        if (mAttendee.isGoogleLoggedIn()) {
            mGooglePlus.setVisibility(View.VISIBLE);
        }
        if (mAttendee.isTwitterLoggedIn()) {
            mTwitter.setVisibility(View.VISIBLE);
            if (mGooglePlus.getVisibility() == View.GONE) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTwitter.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginStart(0);
                }
                mTwitter.setLayoutParams(params);
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
