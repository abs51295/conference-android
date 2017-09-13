package com.systers.conference.maps;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systers.conference.R;
import com.systers.conference.widgets.viewpager.CircleViewPagerIndicator;
import com.systers.conference.widgets.viewpager.VerticalViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    @BindView(R.id.map_view_pager)
    VerticalViewPager mViewPager;
    @BindView(R.id.map_circle_indicator)
    CircleViewPagerIndicator mIndicator;
    private Unbinder mUnbinder;
    private MapViewPagerAdapter mAdapter;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        setUpViewPager();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void setUpViewPager() {
        mAdapter = new MapViewPagerAdapter(getChildFragmentManager());
        mAdapter.addFragment(MapImageViewFragment.newInstance(R.drawable.level_1));
        mAdapter.addFragment(MapImageViewFragment.newInstance(R.drawable.level_2));
        mAdapter.addFragment(MapImageViewFragment.newInstance(R.drawable.level_3));
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager);
    }
}
