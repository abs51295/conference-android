package com.systers.conference.schedule;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.systers.conference.R;
import com.systers.conference.event.EventDetailActivity;
import com.systers.conference.model.Session;
import com.systers.conference.util.DateTimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DayWiseScheduleViewHolder extends ViewHolder {

    public static final String SESSION_ID = "mSession-id";
    @BindView(R.id.schedule_list_item)
    RelativeLayout listItem;
    @BindView(R.id.title)
    TextView sessionTitle;
    @BindView(R.id.subtitle)
    TextView sessionSubTitle;
    @BindView(R.id.add_to_calendar)
    ImageView calendarImage;
    private Session mSession;
    private Context mContext;

    DayWiseScheduleViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;
        ButterKnife.bind(this, itemView);
    }

    void bindSession() {
        sessionTitle.setText(mSession.getName());
        sessionSubTitle.setText(DateTimeUtil.getTimeFromTimeStamp(DateTimeUtil.FORMAT_24H, Long.valueOf(mSession.getStartTime())) + " - " + DateTimeUtil.getTimeFromTimeStamp(DateTimeUtil.FORMAT_24H, Long.valueOf(mSession.getEndTime())) + ", " + mSession.getLocation());
        calendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, sessionTitle.getText().toString());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, sessionSubTitle.getText().toString());
                try {
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, R.string.calendar_not_found, Toast.LENGTH_LONG).show();
                }
            }
        });
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventDetailActivity.class);
                intent.putExtra(SESSION_ID, mSession.getId());
                mContext.startActivity(intent);
            }
        });
    }

    public Session getSession() {
        return mSession;
    }

    public void setSession(Session session) {
        mSession = session;
    }
}
