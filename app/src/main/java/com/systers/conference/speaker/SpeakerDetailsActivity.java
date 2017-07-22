package com.systers.conference.speaker;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.systers.conference.R;
import com.systers.conference.model.Speaker;
import com.systers.conference.util.APIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpeakerDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.toolbar_speaker_details)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.speaker_image)
    ImageView image;
    @BindView(R.id.speakers_details_title)
    TextView title;
    @BindView(R.id.speakers_details_designation)
    TextView designation;
    @BindView(R.id.speakers_details_description)
    TextView description;
    @BindView(R.id.speakers_details_header)
    LinearLayout header;
    @BindView(R.id.speakers_details_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private Speaker speaker;
    private boolean isHideToolbarView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        speaker = new Gson().fromJson(intent.getStringExtra(getString(R.string.speaker_data)), new TypeToken<Speaker>() {
        }.getType());
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.material_black_54), PorterDuff.Mode.SRC_IN);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appBarLayout.addOnOffsetChangedListener(this);
        setUpViews();
    }

    private void setUpViews() {
        String imageUrl = "https://www.eiseverywhere.com/image.php?acc=" + APIUtils.ACCOUNT_ID + "&id=" + speaker.getImageId();
        Picasso.with(this)
                .load(Uri.parse(imageUrl))
                .error(R.drawable.male_icon_9_glasses)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
        title.setText(speaker.getFirstName() + " " + speaker.getLastName());
        designation.setText(speaker.getTitle() + ", " + speaker.getCompany());
        description.setText(speaker.getBio());
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
        if (percentage == 1f && isHideToolbarView) {
            if (TextUtils.isEmpty(speaker.getTitle()) && TextUtils.isEmpty(speaker.getCompany())) {
                header.setVisibility(View.GONE);
                collapsingToolbarLayout.setTitle(speaker.getFirstName() + " " + speaker.getLastName());
                isHideToolbarView = !isHideToolbarView;
            } else {
                header.setVisibility(View.VISIBLE);
                collapsingToolbarLayout.setTitle(" ");
                designation.setMaxLines(1);
                designation.setEllipsize(TextUtils.TruncateAt.END);
                isHideToolbarView = !isHideToolbarView;
            }
        } else if (percentage < 1f && !isHideToolbarView) {
            header.setVisibility(View.VISIBLE);
            collapsingToolbarLayout.setTitle(" ");
            designation.setMaxLines(3);
            isHideToolbarView = !isHideToolbarView;
        }
    }
}
