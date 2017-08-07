package com.systers.conference.schedule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systers.conference.R;
import com.systers.conference.adapter.BaseRecyclerViewAdapter;
import com.systers.conference.model.Session;
import com.systers.conference.util.DateTimeUtil;
import com.systers.conference.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

class DayWiseScheduleAdapter extends BaseRecyclerViewAdapter<Session, DayWiseScheduleViewHolder> implements StickyRecyclerHeadersAdapter {
    private List<Session> mSessions = new ArrayList<>();
    private Context mContext;

    DayWiseScheduleAdapter(List<Session> dataList, Context context) {
        super(dataList);
        mContext = context;
        mSessions = dataList;
    }

    void setSessions(List<Session> sessions) {
        mSessions = sessions;
    }

    @Override
    public DayWiseScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.schedule_list_item, parent, false);
        return new DayWiseScheduleViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(DayWiseScheduleViewHolder holder, int position) {
        Session currentSession = getItem(position);
        holder.setSession(currentSession);
        holder.bindSession();
    }

    @Override
    public long getHeaderId(int position) {
        return Long.valueOf(getItem(position).getStartTime());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView.findViewById(R.id.recycler_view_header);
        textView.setText(DateTimeUtil.getTimeFromTimeStamp(DateTimeUtil.FORMAT_24H, Long.valueOf(getItem(position).getStartTime())));
    }
}
