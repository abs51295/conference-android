package com.systers.conference.event;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ShareCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;
import com.systers.conference.BaseActivity;
import com.systers.conference.R;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Session;
import com.systers.conference.model.Speaker;
import com.systers.conference.schedule.DayWiseScheduleViewHolder;
import com.systers.conference.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.ObjectChangeSet;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;

public class EventDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.event_title)
    TextView mEventTitle;
    @BindView(R.id.time)
    TextView mTime;
    @BindView(R.id.event_description)
    TextView mEventDescription;
    @BindView(R.id.fab_menu)
    FloatingActionsMenu mFloatingActionsMenu;
    @BindView(R.id.room)
    TextView mRoom;
    @BindView(R.id.audience_level)
    TextView mAudience;
    @BindView(R.id.calendar_fab)
    FloatingActionButton mCal;
    @BindView(R.id.share_fab)
    FloatingActionButton mShare;
    @BindView(R.id.speakers_container)
    ViewGroup mSpeakers;
    @BindView(R.id.speakers_header)
    TextView mSpeakerListHeader;
    private Session mSession;
    private List<Speaker> mSpeakerList = new ArrayList<>();
    private RealmList<Speaker> mRealmSpeakers;
    private RealmDataRepository mRealmRepo = RealmDataRepository.getDefaultInstance();

    @OnClick(R.id.calendar_fab)
    public void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, mEventTitle.getText().toString());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, mEventDescription.getText().toString());
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.calendar_not_found, Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.share_fab)
    public void share() {
        startActivity(getShareChooserIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setDrawables();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSession = mRealmRepo.getSessionById(getIntent().getStringExtra(DayWiseScheduleViewHolder.SESSION_ID));
        updateSession();
        updateSpeakers();
        mSession.addChangeListener(new RealmObjectChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel realmModel, ObjectChangeSet changeSet) {
                updateSession();
            }
        });
        mRealmSpeakers = mSession.getSpeakers();
        mRealmSpeakers.addChangeListener(new OrderedRealmCollectionChangeListener<RealmList<Speaker>>() {
            @Override
            public void onChange(RealmList<Speaker> speakers, OrderedCollectionChangeSet changeSet) {
                updateSpeakers();
            }
        });
    }

    private Intent getShareChooserIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setSubject(String.format("%1$s (GHC)", mEventTitle.getText().toString()))
                .setType("text/plain")
                .setText(String.format("%1$s %2$s #GHC", mEventTitle.getText().toString(), mTime.getText().toString()))
                .setChooserTitle(R.string.share)
                .createChooserIntent();
    }

    private void setDrawables() {
        Drawable iconDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_place_grey600_24dp);
        mRoom.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null);
        iconDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_access_time_grey600_24dp);
        mTime.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null);
        iconDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_people_grey600_24dp);
        mAudience.setCompoundDrawablesWithIntrinsicBounds(iconDrawable, null, null, null);
        iconDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_calendar_plus);
        mCal.setIconDrawable(iconDrawable);
        iconDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_share_grey600_24dp);
        mShare.setIconDrawable(iconDrawable);
    }

    private void updateSession() {
        mEventTitle.setText(mSession.getName());
        mEventDescription.setText(mSession.getDescription());
        mAudience.setText(mSession.getSessionType());
        mRoom.setText(mSession.getLocation());
        String startTime = DateTimeUtil.getTimeFromTimeStamp(DateTimeUtil.FORMAT_24H, Long.valueOf(mSession.getStartTime()));
        String endTime = DateTimeUtil.getTimeFromTimeStamp(DateTimeUtil.FORMAT_24H, Long.valueOf(mSession.getEndTime()));
        Date date = DateTimeUtil.getDate(mSession.getSessionDate());
        String descriptiveDate = "";
        if (date != null) {
            descriptiveDate = DateTimeUtil.getDateDescriptive(date);
        }
        mTime.setText(descriptiveDate + ", " + startTime + " - " + endTime);
    }

    private void updateSpeakers() {
        mSpeakers.removeAllViews();
        mSpeakerList.clear();
        mSpeakerList.addAll(mSession.getSpeakers());
        if (mSpeakerList != null && mSpeakerList.size() > 0) {
            mSpeakerListHeader.setVisibility(View.VISIBLE);
            mSpeakers.setVisibility(View.VISIBLE);
            for (Speaker speaker : mSpeakerList) {
                View view = LayoutInflater.from(this).inflate(R.layout.speaker_list_item, mSpeakers, false);
                CircleImageView avatar = (CircleImageView) view.findViewById(R.id.speaker_avatar_icon);
                if (!TextUtils.isEmpty(speaker.getAvatarUrl())) {
                    Picasso.with(this).load(speaker.getAvatarUrl())
                            .resize(80, 80)
                            .centerCrop()
                            .placeholder(R.drawable.anita_borg_logo)
                            .error(R.drawable.anita_borg_logo)
                            .into(avatar);
                }
                TextView speakerName = (TextView) view.findViewById(R.id.speaker_name);
                speakerName.setText(speaker.getName());
                TextView speakerRole = (TextView) view.findViewById(R.id.speaker_role);
                speakerRole.setText(speaker.getRole() + ", " + speaker.getCompany());
                mSpeakers.addView(view);
            }
        } else {
            mSpeakerListHeader.setVisibility(View.GONE);
            mSpeakers.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFloatingActionsMenu.isExpanded()) {
            mFloatingActionsMenu.collapse();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSession.removeAllChangeListeners();
        mRealmSpeakers.removeAllChangeListeners();
    }
}
