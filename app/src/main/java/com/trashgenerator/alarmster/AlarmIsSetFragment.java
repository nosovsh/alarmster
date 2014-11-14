package com.trashgenerator.alarmster;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;


/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class AlarmIsSetFragment extends Fragment
        implements TimePicker.OnTimeChangedListener {


    private TextView remainingView;
    private TimePicker timePicker;
    AlarmStore alarmStore;


    public AlarmIsSetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm_is_set, container, false);

        alarmStore = new AlarmStore(getActivity().getApplicationContext());

        remainingView = (TextView) view.findViewById(R.id.remainingView);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        timePicker.setIs24HourView(true);

        int h = alarmStore.getAlarmHour();
        int m = alarmStore.getAlarmMinute();
        timePicker.setCurrentHour(h);
        timePicker.setCurrentMinute(m);
        calculateAndShowRemaining(h, m);

        // should be after setting h and m
        timePicker.setOnTimeChangedListener(this);


        return view;
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int h, int m) {
        calculateAndShowRemaining(h, m);
        alarmStore.setAlarmTime(h, m);
    }

    private void calculateAndShowRemaining(int h, int m) {
        DateTime alarmDateTime = Utils.convertHoursAndMinutesToDateTime(h, m);
        DateTime now = DateTime.now();
        int hRemaining = Hours.hoursBetween(now.toLocalDateTime(), alarmDateTime.toLocalDateTime()).getHours();
        int mRemaining = Minutes.minutesBetween(now.toLocalDateTime(), alarmDateTime.toLocalDateTime()).getMinutes() % 60;
        showRemaining(hRemaining, mRemaining);
    }

    private void showRemaining(int h, int m) {
        String s = getString(R.string.remaining, h, m);
        if (remainingView != null)
            remainingView.setText(s);
    }
}
