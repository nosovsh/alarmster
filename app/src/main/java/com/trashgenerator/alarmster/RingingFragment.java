package com.trashgenerator.alarmster;



import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.trashgenerator.alarmster.events.AlarmRinging;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;


/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class RingingFragment extends Fragment {

    TextView timeView;

    public RingingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ringing, container, false);

        timeView = (TextView) view.findViewById(R.id.time);

        setCurrentTime();

        blink(view);

        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(AlarmRinging e) {
        Utils.logE("RingingFragment", e.toString());
        setCurrentTime();
    }

    public void setCurrentTime() {
        timeView.setText(DateTime.now().toString("H:mm"));
    }

    public void blink(View view) {
        int fromColor = Color.parseColor("#e74c3c");
        int toColor = Color.parseColor("#c0392b");

        ObjectAnimator anim = ObjectAnimator.ofInt(view, "backgroundColor", fromColor, toColor);
        anim.setDuration(300);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.start();

    }


}
