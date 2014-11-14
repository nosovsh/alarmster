package com.trashgenerator.alarmster;

import android.util.Log;

import org.joda.time.DateTime;

/**
 * Created by trashgenerator on 25.09.14.
 */
public class Utils {
    public static DateTime convertHoursAndMinutesToDateTime(int h, int m) {

        DateTime now = DateTime.now();
        DateTime alarmDateTime = now.withHourOfDay(h).withMinuteOfHour(m).withSecondOfMinute(0).withMillisOfSecond(0);
        if (h < now.getHourOfDay() || (h == now.getHourOfDay() && m <= now.getMinuteOfHour())) {
            // tomorrow
            alarmDateTime = alarmDateTime.plusDays(1);
        }
        return alarmDateTime;
    }
    public static void logE(String tag, String msg) {
        if (tag != null && msg !=null)
            Log.e(tag, msg);
        else
            Log.e("WTF", "Something is null in logs.");
    }
    public static void logD(String tag, String msg) {
        if (tag != null && msg !=null)
            Log.d(tag, msg);
        else
            Log.e("WTF", "Something is null in logs.");
    }
}
