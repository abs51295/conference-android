package com.systers.conference;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.systers.conference.profile.ProfileFragment;
import com.systers.conference.schedule.ScheduleFragment;
import com.systers.conference.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static final String STATE_CURRENT_SECTION = "current_section";
    private static final String LOG_TAG = LogUtils.makeLogTag(MainActivity.class);
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    private ImageView mProfileImage;
    private int mPendingMenuSection = -1;
    private final NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mPendingMenuSection = item.getItemId();
            mDrawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };
    private int mCurrentMenuSection = -1;
    private Section mCurrentSection;
    private ActionBarDrawerToggle mDrawerToogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        View headerView = mNavigationView.getHeaderView(0);
        mProfileImage = (ImageView) headerView.findViewById(R.id.imageView);
        setSupportActionBar(mToolbar);
        mDrawerToogle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    mPendingMenuSection = -1;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (mPendingMenuSection != -1) {
                    selectNavigationItem(mPendingMenuSection);
                    mPendingMenuSection = -1;
                }
            }
        };
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPendingMenuSection = v.getId();
                int size = mNavigationView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    mNavigationView.getMenu().getItem(i).setChecked(false);
                }
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });
        mDrawer.addDrawerListener(mDrawerToogle);
        mDrawerToogle.syncState();
        if (savedInstanceState == null) {
            if (getIntent().getBooleanExtra(getString(R.string.edit_profile), false)) {
                selectNavigationItem(R.id.imageView);
            } else {
                selectNavigationItem(R.id.nav_camera);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mNavigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mPendingMenuSection = -1;
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_SECTION, mCurrentMenuSection);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogUtils.LOGE(LOG_TAG, "savedInstance");
        mCurrentMenuSection = savedInstanceState.getInt(STATE_CURRENT_SECTION);
        LogUtils.LOGE(LOG_TAG, mCurrentMenuSection + " ");
        if (mCurrentMenuSection != R.id.imageView) {
            LogUtils.LOGE(LOG_TAG, "true");
            mNavigationView.getMenu().findItem(mCurrentMenuSection).setChecked(true);
        } else {
            LogUtils.LOGE(LOG_TAG, "false");
            int size = mNavigationView.getMenu().size();
            LogUtils.LOGE(LOG_TAG, size + " ");
            for (int i = 0; i < size; i++) {
                mNavigationView.getMenu().getItem(i).setChecked(false);
            }
        }
        mapIdToSection(mCurrentMenuSection);
        updateActionBar();
    }

    private void selectNavigationItem(int itemId) {
        LogUtils.LOGE(LOG_TAG, mCurrentMenuSection + " " + itemId);
        if (mCurrentMenuSection != itemId) {
            mCurrentMenuSection = itemId;
            switch (itemId) {
                case R.id.nav_camera:
                    mNavigationView.getMenu().findItem(itemId).setChecked(true);
                    updateUI(Section.SCHEDULE);
                    break;
                case R.id.imageView:
                    updateUI(Section.PROFILE);
                    break;
            }
        }
    }

    private void mapIdToSection(int itemId) {
        switch (itemId) {
            case R.id.nav_camera:
                mCurrentSection = Section.SCHEDULE;
                break;
            case R.id.imageView:
                mCurrentSection = Section.PROFILE;
                break;
        }
    }

    private void updateActionBar() {
        setTitle(mCurrentSection.getTitleResId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            if (mCurrentSection.extendsAppBar()) {
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(mAppBar, "elevation", 0));
            } else {
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(mAppBar, "elevation", getResources().getDimension(R.dimen.toolbar_elevation)));
            }
            mAppBar.setStateListAnimator(stateListAnimator);
        }
    }

    private void updateUI(Section section) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        Fragment fragment = supportFragmentManager.findFragmentById(R.id.content);
        if (fragment != null) {
            if (mCurrentSection.shouldKeep()) {
                fragmentTransaction.detach(fragment);
            } else {
                fragmentTransaction.remove(fragment);
            }
        }
        String fragmentClassName = section.getFragmentClassName();
        if (section.shouldKeep() && ((fragment = supportFragmentManager.findFragmentByTag(fragmentClassName)) != null)) {
            fragmentTransaction.attach(fragment);
        } else {
            fragment = Fragment.instantiate(MainActivity.this, fragmentClassName);
            fragmentTransaction.add(R.id.content, fragment, fragmentClassName);
        }
        fragmentTransaction.commit();
        mCurrentSection = section;
        updateActionBar();
    }

    private enum Section {
        SCHEDULE(ScheduleFragment.class, R.string.schedule_title, true, true),
        PROFILE(ProfileFragment.class, R.string.profile_title, false, false);

        private final String fragmentClassName;
        private final int titleResId;
        private final boolean extendsAppBar;
        private final boolean keep;

        Section(Class<? extends Fragment> fragmentClass, int titleResId,
                boolean extendsAppBar, boolean keep) {
            this.fragmentClassName = fragmentClass.getName();
            this.titleResId = titleResId;
            this.extendsAppBar = extendsAppBar;
            this.keep = keep;
        }

        public String getFragmentClassName() {
            return fragmentClassName;
        }

        @StringRes
        public int getTitleResId() {
            return titleResId;
        }

        public boolean extendsAppBar() {
            return extendsAppBar;
        }

        public boolean shouldKeep() {
            return keep;
        }
    }
}
