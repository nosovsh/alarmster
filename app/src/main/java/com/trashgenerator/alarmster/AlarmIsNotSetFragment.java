package com.trashgenerator.alarmster;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * A simple {@link android.app.Fragment} subclass.
 *
 */
public class AlarmIsNotSetFragment extends Fragment {


    public AlarmIsNotSetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_is_not_set, container, false);
    }


}
