package com.trashgenerator.alarmster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBootReceiver extends BroadcastReceiver {
    public AlarmBootReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.logD("AlarmBootReceiver", intent.getAction().toString());

        AlarmStore alarmStore = new AlarmStore(context);
        int state = alarmStore.getState();
        if (state == AlarmStore.STATE_ENABLED) {
            alarmStore.reSetAlarm();
        } else if (state == AlarmStore.STATE_RINGING || state == AlarmStore.STATE_SNOOZED) {
            alarmStore.disable();
        }

    }
}
