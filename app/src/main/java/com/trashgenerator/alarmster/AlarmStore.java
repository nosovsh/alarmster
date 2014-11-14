package com.trashgenerator.alarmster;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.trashgenerator.alarmster.events.AlarmDisabled;
import com.trashgenerator.alarmster.events.AlarmEnabled;
import com.trashgenerator.alarmster.events.AlarmRinging;
import com.trashgenerator.alarmster.events.AlarmSnoozed;
import com.trashgenerator.alarmster.events.CancelSnoozing;
import com.trashgenerator.alarmster.events.StartSnoozing;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;

/**
 * Created by trashgenerator on 29.10.14.
 */
public class AlarmStore {


    public static int STATE_DISABLED = 1;
    public static int STATE_ENABLED = 2;
    public static int STATE_RINGING = 3;
    public static int STATE_SNOOZED = 4;

    public static String SHARED_PREFERENCES_NAME = "Malar";

    SharedPreferences settings;
    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    Context context;


    public AlarmStore(Context vContext) {
        super();
        context = vContext;

        settings = context.getSharedPreferences(AlarmStore.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmsterActivity.class);
//        intent.setAction(AlarmsterActivity.ACTION_RING);
//        alarmIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent intent = new Intent(AlarmsterActivity.ACTION_RING);
        alarmIntent = PendingIntent.getBroadcast(
                context, 0, intent, 0);




    }

    public int getState() {
        return settings.getInt("state", STATE_DISABLED);
    }

    public void enable() {
        if (this.getState() != STATE_ENABLED) {
            settings
                    .edit()
                    .putInt("state", STATE_ENABLED)
                    .commit();
            setAlarm(getAlarmHour(), getAlarmMinute());
            Utils.logE("Event sent", "AlarmEnabled");
            EventBus.getDefault().post(new AlarmEnabled());
        }
    }

    public void disable() {
        if (this.getState() != STATE_DISABLED) {
            settings
                    .edit()
                    .putInt("state", STATE_DISABLED)
                    .commit();
            cancelAlarm();
            releaseCpuLock();
            Log.e("Event sent", "AlarmDisabled");
            EventBus.getDefault().post(new AlarmDisabled());
        }
    }

    public void ring() {
        if (this.getState() != STATE_RINGING) {
            settings
                    .edit()
                    .putInt("state", STATE_RINGING)
                    .commit();
            acquireCpuWakeLock();
            Log.e("Event sent", "AlarmRinging");
            EventBus.getDefault().post(new AlarmRinging());
        }
    }

    public void snooze() {
        if (this.getState() != STATE_SNOOZED) {
            settings
                    .edit()
                    .putInt("state", STATE_SNOOZED)
                    .commit();
            snoozeAlarm();
            Log.e("Event sent", "AlarmSnoozed");
            EventBus.getDefault().post(new AlarmSnoozed());
        }
    }

    public void visualizeSnooze() {
        Log.e("AlarmStore", "Visualizing snooze");
        releaseCpuLock();
        showSnoozeToast();
        showSnoozeNotification();
    }

    public int getAlarmHour() {
        return settings.getInt("time_h", 0);
    }

    public int getAlarmMinute() {
        return settings.getInt("time_m", 0);
    }

    public void setAlarmTime(int h, int m) {
        settings
                .edit()
                .putInt("time_h", h)
                .putInt("time_m", m)
                .commit();
        setAlarm(h, m);
    }

    public void startSnoozing() {
        Log.e("Event sent", "StartSnoozing");
        EventBus.getDefault().post(new StartSnoozing());
    }

    public void cancelSnoozing() {
        Log.e("Event sent", "CancelSnoozing");
        EventBus.getDefault().post(new CancelSnoozing());
    }



    private void setAlarm(int h, int m) {
        DateTime alarmDateTime = Utils.convertHoursAndMinutesToDateTime(h, m);
        setAlarm(alarmDateTime);
    }

    private void setAlarm(DateTime alarmDateTime) {
        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmDateTime.getMillis(), alarmIntent);
        Log.e("Alarm set to", alarmDateTime.toString());
    }

    public void reSetAlarm() {
        setAlarm(getAlarmHour(), getAlarmMinute());
    }


    private void cancelAlarm() {
        alarmMgr.cancel(alarmIntent);
        Log.e("Alarm canceled", "");
    }

    private void snoozeAlarm() {
        setAlarm(DateTime.now().plusSeconds(60 * 5));
    }

    private void showSnoozeToast () {
        String text = context.getResources().getString(R.string.alarm_snoozed);
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    private void showSnoozeNotification () {

        Notification.Builder mBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(context.getResources().getString(R.string.notification_alarm_is_snoozed))
                        .setContentText(context.getResources().getString(R.string.notification_tap_to_cancel))
                        .setAutoCancel(true)
                        .setOngoing(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, AlarmsterActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(AlarmsterActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(AlarmsterActivity.NOTIFICATION_ID, mBuilder.build());

    }

    public void hideSnoozeNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmsterActivity.NOTIFICATION_ID);
    }

    private static PowerManager.WakeLock sCpuWakeLock;

    public void acquireCpuWakeLock() {
        Log.v("Acquiring cpu wake lock", "");
        if (sCpuWakeLock != null) {
            return;
        }

        PowerManager pm =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "wakelock");
        sCpuWakeLock.acquire();
    }

    static void releaseCpuLock() {
        Log.v("Releasing cpu wake lock", "");
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }

}
