package com.trashgenerator.alarmster;



import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v13.app.FragmentStatePagerAdapter;


import com.trashgenerator.alarmster.events.AlarmDisabled;
import com.trashgenerator.alarmster.events.AlarmEnabled;
import com.trashgenerator.alarmster.events.AlarmRinging;

import java.util.Locale;

import de.greenrobot.event.EventBus;


/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class MainFragment extends Fragment implements
        ViewPager.OnPageChangeListener  {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    View view;
    AlarmStore alarmStore;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.logE("MainFragment", "onCreateView");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(this);

        EventBus.getDefault().register(this);

        alarmStore = new AlarmStore(getActivity().getApplicationContext());


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (alarmStore.getState() == AlarmStore.STATE_ENABLED) {
            if (mViewPager.getCurrentItem() != 1) {
                mViewPager.setCurrentItem(1);
            }
        } else {
            if (mViewPager.getCurrentItem() != 0) {
                mViewPager.setCurrentItem(0);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    public void onEvent(AlarmDisabled e) {
        Utils.logE("MainFragment", e.toString());
        if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0);
        }
    }

    public void onEvent(AlarmEnabled e) {
        Utils.logE("MainFragment", e.toString());
        if (mViewPager.getCurrentItem() != 1) {
            mViewPager.setCurrentItem(1);
        }
    }

    public void onEvent(AlarmRinging e) {
        Utils.logE("MainFragment", e.toString());
        if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0);
        }
    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        if (alarmStore.getState() != AlarmStore.STATE_ENABLED && i == 1) {
            // enable if it is not already enabled
            alarmStore.enable();
        } else if (alarmStore.getState() == AlarmStore.STATE_ENABLED && i == 0) {
            // disable only if it is enabled. Do not disable if it is ringing or snoozed
            alarmStore.disable();
        }


        View layout = (View) view.findViewById(R.id.mainLayout);
        int setColor = Color.parseColor("#5AAD6A"); //""7F2D5A 820ecf 3498db
        int notSetColor = Color.parseColor("#34495e");
        int color = i == 0 ? notSetColor : setColor;
        int fromColor;
        int toColor;
        if (i == 0) {
            fromColor = setColor;
            toColor = notSetColor;
        } else {
            fromColor = notSetColor;
            toColor = setColor;
        }

        ObjectAnimator anim = ObjectAnimator.ofInt(layout, "backgroundColor", fromColor, toColor);
        anim.setDuration(300);
        anim.setEvaluator(new ArgbEvaluator());
        anim.start();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return new AlarmIsNotSetFragment();
            } else {
                return new AlarmIsSetFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public Parcelable saveState() {
            Utils.logE("MainFragment", "saveState");
            return super.saveState();
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            Utils.logE("MainFragment", "restoreState");
            super.restoreState(state, loader);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}
