package com.systers.conference.maps;


import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.systers.conference.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapImageViewFragment extends Fragment {

    private static final String ARG_IMAGE_ID = "image-id";
    @BindView(R.id.map_image_view)
    PhotoView mImage;
    private Unbinder mUnbinder;
    @DrawableRes
    private int mapImageResId;

    public MapImageViewFragment() {
        // Required empty public constructor
    }

    public static MapImageViewFragment newInstance(@DrawableRes int mapImageResId) {
        MapImageViewFragment fragment = new MapImageViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_ID, mapImageResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mapImageResId = getArguments().getInt(ARG_IMAGE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_image_view, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mImage.setImageResource(mapImageResId);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
