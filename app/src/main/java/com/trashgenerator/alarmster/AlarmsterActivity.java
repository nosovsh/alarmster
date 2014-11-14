package com.trashgenerator.alarmster;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import com.trashgenerator.alarmster.events.AlarmDisabled;
import com.trashgenerator.alarmster.events.AlarmRinging;
import com.trashgenerator.alarmster.events.AlarmSnoozed;
import com.trashgenerator.alarmster.events.CancelSnoozing;
import com.trashgenerator.alarmster.events.StartSnoozing;

import de.greenrobot.event.EventBus;


public class AlarmsterActivity extends Activity {

    AlarmStore alarmStore;
    static final String ACTION_RING = "com.trashgenerator.alarmster.RING";
    static final int NOTIFICATION_ID = 1;
    Ringtone ringtone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ala);

        if (savedInstanceState == null) {
        }

        MainFragment mainFragment = new MainFragment();
        RingingPagerFragment ringingPagerFragment = new RingingPagerFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.container, mainFragment, "mainFragment")
                .add(R.id.container, ringingPagerFragment, "ringingPagerFragment")
                .hide(ringingPagerFragment)
                .commit();

        alarmStore = new AlarmStore(getApplicationContext());
        EventBus.getDefault().register(this);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
    }



    @Override
    protected void onPause() {
        super.onPause();
        Utils.logE("onPause", "");
        Utils.logE("intent", getIntent().getAction());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Utils.logE("onNewIntent", "");
        Utils.logE("intent", intent.getAction());
//        setIntent(intent);

        if (intent.getAction() == ACTION_RING) {
            alarmStore.ring();
        } else
        if (alarmStore.getState() == AlarmStore.STATE_SNOOZED) {
            alarmStore.disable();
        }
        alarmStore.hideSnoozeNotification();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.logE("onResume", "");
        Utils.logE("intent", getIntent().getAction());

        if (getIntent().getAction() == ACTION_RING) {
            alarmStore.ring();
        } else
        if (alarmStore.getState() == AlarmStore.STATE_SNOOZED) {
            alarmStore.disable();
        }
        alarmStore.hideSnoozeNotification();
//        alarmStore.acquireCpuWakeLock();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Utils.logE("onStart", "");
//        Utils.logE("intent", getIntent().getAction());


    }

    @Override
    protected void onStop() {
        super.onStop();

        Utils.logE("onStop", "");
        if(alarmStore.getState() == AlarmStore.STATE_RINGING) {
//            alarmStore.snooze();
//            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//            boolean isScreenOn = powerManager.isScreenOn();
//            Utils.logD("isScreenOn", String.valueOf(isScreenOn));
//            if (!isScreenOn) {
//                finish();
//            }
//            if (!isFinishing()) {
//                // Don't hang around.
//                finish();
//            }

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.logE("onDestroy", "");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Utils.logE("onConfigurationChanged", newConfig.toString());

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Utils.logE("onUserLeaveHint", "");
        if(alarmStore.getState() == AlarmStore.STATE_RINGING) {
            alarmStore.snooze();
            alarmStore.visualizeSnooze();
        }

    }

    public void onEvent(AlarmDisabled e) {
        Utils.logE("AlarmsterActivity", e.toString());
        stop();
        setScreenOff();
        Fragment ringingPagerFragment = getFragmentManager().findFragmentByTag("ringingPagerFragment");
        getFragmentManager().beginTransaction()
                .hide(ringingPagerFragment)
                .commit();

        setIntent(getIntent().setAction(Intent.ACTION_MAIN));
    }

    public void onEvent(AlarmRinging e) {
        Utils.logE("AlarmsterActivity", e.toString());
        play();
        setScreenOn();
        Fragment ringingPagerFragment = getFragmentManager().findFragmentByTag("ringingPagerFragment");
        getFragmentManager().beginTransaction()
                .show(ringingPagerFragment)
                .commit();
    }

    public void onEvent(AlarmSnoozed e) {
        Utils.logE("AlarmsterActivity", e.toString());
        stop();
        setScreenOff();
        setIntent(getIntent().setAction(Intent.ACTION_MAIN));

//        finish();
    }

    public void onEvent(StartSnoozing e) {
        Utils.logE("AlarmsterActivity", e.toString());
        Fragment mainFragment = getFragmentManager().findFragmentByTag("mainFragment");
        getFragmentManager().beginTransaction()
                .hide(mainFragment)
                .commit();
    }

    public void onEvent(CancelSnoozing e) {
        Utils.logE("AlarmsterActivity", e.toString());
        Fragment mainFragment = getFragmentManager().findFragmentByTag("mainFragment");
        getFragmentManager().beginTransaction()
                .show(mainFragment)
                .commit();
    }


    private void play() {
        if (ringtone != null && !ringtone.isPlaying()) {
            ringtone.play();
        }
    }

    private void stop () {
        if (ringtone != null)
            ringtone.stop();
    }

    public void setScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public void setScreenOff() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }




}