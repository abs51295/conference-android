package com.systers.conference.schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systers.conference.R;
import com.systers.conference.db.RealmDataRepository;
import com.systers.conference.model.Session;
import com.systers.conference.views.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

/**
 * A fragment representing a list of Items.
 */
public class DayWiseScheduleFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_SESSION_DATE = "session-date";
    private static final String ACTIVITY_TITLE = "activity-title";
    @BindView(R.id.list)
    RecyclerView mRecyclerView;
    private int mColumnCount = 1;
    private String sessionDate;
    private Unbinder unbinder;
    private RealmResults<Session> sessions;
    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private List<Session> mSessions = new ArrayList<>();
    private String activityTitle;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DayWiseScheduleFragment() {
    }

    @SuppressWarnings("unused")
    public static DayWiseScheduleFragment newInstance(int columnCount, String sessionDate) {
        DayWiseScheduleFragment fragment = new DayWiseScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_SESSION_DATE, sessionDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            sessionDate = getArguments().getString(ARG_SESSION_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        final DayWiseScheduleAdapter scheduleAdapter = new DayWiseScheduleAdapter(mSessions, getActivity());
        if (savedInstanceState == null) {
            activityTitle = getActivity().getTitle().toString();
        } else {
            activityTitle = savedInstanceState.getString(ACTIVITY_TITLE);
        }
        if (activityTitle.equals(getString(R.string.schedule_title))) {
            sessions = realmRepo.getSessionsByDay(sessionDate);
        } else if (activityTitle.equals(getString(R.string.myschedule_title))) {
            sessions = realmRepo.getBookmarkedSessions(sessionDate);
        }
        sessions.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessions, OrderedCollectionChangeSet changeSet) {
                mSessions.clear();
                mSessions.addAll(sessions);
                scheduleAdapter.setSessions(mSessions);
                scheduleAdapter.notifyDataSetChanged();
            }
        });
        // Set the adapter
        if (mRecyclerView != null) {
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
            }
            mRecyclerView.setAdapter(scheduleAdapter);
            final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(scheduleAdapter);
            mRecyclerView.addItemDecoration(headersDecoration);
            scheduleAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    headersDecoration.invalidateHeaders();
                }
            });
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTIVITY_TITLE, activityTitle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        sessions.removeAllChangeListeners();
    }
}
