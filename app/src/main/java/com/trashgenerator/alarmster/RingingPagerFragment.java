package com.trashgenerator.alarmster;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trashgenerator.alarmster.events.AlarmRinging;
import com.trashgenerator.alarmster.events.AlarmSnoozed;

import java.util.Locale;

import de.greenrobot.event.EventBus;


/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class RingingPagerFragment extends Fragment implements ViewPager.OnPageChangeListener {


    RingingSectionsPagerAdapter mRingingSectionsPagerAdapter;
    ViewPager mViewPager;
    AlarmStore alarmStore;
    Boolean snoozingStarted = false;

    public RingingPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.logD("RingingPagerFragment", "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ringing_pager, container, false);

        alarmStore = new AlarmStore(getActivity().getApplicationContext());


        mRingingSectionsPagerAdapter = new RingingSectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.ringingPager);
        mViewPager.setAdapter(mRingingSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(1);



        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {
        Fragment ringingFragment = mRingingSectionsPagerAdapter.getItem(1);

        View view = ringingFragment.getView();
        float alpha;
        if (i == 0) {
            alpha = v;
            if (snoozingStarted)
                alarmStore.cancelSnoozing();
                snoozingStarted = false;
        } else {
            alpha = 1 - v;
            if (v == 0 && snoozingStarted) {
                alarmStore.cancelSnoozing();
                snoozingStarted = false;
            } else if (v != 0 && !snoozingStarted) {
                alarmStore.startSnoozing();
                snoozingStarted = true;
            }
        }
        if (view != null) {
            view.setAlpha(alpha);
        }
    }

    @Override
    public void onPageSelected(int i) {
        Utils.logE("onPageSelected", "");
        if (i == 0) {
            alarmStore.disable();
        } else if (i == 1) {
            alarmStore.cancelSnoozing();
            snoozingStarted = false;
        } else if (i == 2) {
            alarmStore.snooze();
            alarmStore.visualizeSnooze();
            getActivity().finish();
        }

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void onEvent(AlarmRinging e) {
        Utils.logE("RingingPagerAdapter", e.toString());
        mViewPager.setCurrentItem(1);
    }
    public void onEvent(AlarmSnoozed e) {
        Utils.logE("RingingPagerAdapter", e.toString());
//        mViewPager.setOnPageChangeListener(null);
//        mViewPager.setCurrentItem(0);
//        mViewPager.setOnPageChangeListener(this);
    }

    public class RingingSectionsPagerAdapter extends FragmentStatePagerAdapter {

        public RingingSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 1) {
                return new RingingFragment();
            } else {
                return new TransparentFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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
