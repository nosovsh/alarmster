package com.trashgenerator.alarmster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.logD("AlarmReceiver", "onReceive");
        Utils.logD("AlarmReceiver", intent.toString());

        Intent alarmAlert = new Intent(context, AlarmsterActivity.class);
        alarmAlert.setAction(AlarmsterActivity.ACTION_RING);
        alarmAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        context.startActivity(alarmAlert);
    }
}
