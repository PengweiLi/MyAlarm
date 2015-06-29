package com.lipengwei.myalarm.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.widget.Toast;
import android.app.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import com.lipengwei.myalarm.MyAlarmReceiver;
import com.lipengwei.myalarm.R;
import com.lipengwei.myalarm.provider.Alarm;
import com.lipengwei.myalarm.provider.AlarmInstance;
import com.lipengwei.myalarm.TimePickerFragment;;

/**
 * Static utility methods for Alarms.
 */
public class AlarmUtils {
    public static final String FRAG_TAG_TIME_PICKER = "time_dialog";
    private static PendingIntent mPendingIntent;

    @SuppressLint("NewApi")
	public static String getFormattedTime(Context context, Calendar time) {
        String skeleton = DateFormat.is24HourFormat(context) ? "EHm" : "Ehma";
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
        return (String) DateFormat.format(pattern, time);
    }

    public static final String[] BACKGROUND_SPECTRUM = { "#212121", "#27232e", "#2d253a",
        "#332847", "#382a53", "#3e2c5f", "#442e6c", "#393a7a", "#2e4687", "#235395", "#185fa2",
        "#0d6baf", "#0277bd", "#0d6cb1", "#1861a6", "#23569b", "#2d4a8f", "#383f84", "#433478",
        "#3d3169", "#382e5b", "#322b4d", "#2c273e", "#272430" };

    public static String getAlarmText(Context context, AlarmInstance instance) {
        String alarmTimeStr = getFormattedTime(context, instance.getAlarmTime());
        return !instance.mLabel.isEmpty() ? alarmTimeStr + " - " + instance.mLabel
                : alarmTimeStr;
    }

    public static void showTimeEditDialog(Activity activity, final Alarm alarm) {
        final FragmentManager manager = activity.getFragmentManager();
        final TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setOnTimeSetListener((TimePickerDialog.OnTimeSetListener) activity);
        timePickerFragment.setAlarm(alarm);
        timePickerFragment.show(manager, FRAG_TAG_TIME_PICKER);
    }
    
    public static void deleteAllInstances(Context context, long alarmId) {
        ContentResolver cr = context.getContentResolver();
        List<AlarmInstance> instances = AlarmInstance.getInstancesByAlarmId(cr, alarmId);
        for (AlarmInstance instance : instances) {
            cancelAlarmOnQuarterHour(context,instance);
            AlarmInstance.deleteInstance(context.getContentResolver(), instance.mId);
        }
    }

    /**
     * format "Alarm set for 2 days 7 hours and 53 minutes from
     * now"
     */
    private static String formatToast(Context context, long timeInMillis) {
        long delta = timeInMillis - System.currentTimeMillis();
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String daySeq = (days == 0) ? "" :
                (days == 1) ? context.getString(R.string.day) :
                        context.getString(R.string.days, Long.toString(days));

        String minSeq = (minutes == 0) ? "" :
                (minutes == 1) ? context.getString(R.string.minute) :
                        context.getString(R.string.minutes, Long.toString(minutes));

        String hourSeq = (hours == 0) ? "" :
                (hours == 1) ? context.getString(R.string.hour) :
                        context.getString(R.string.hours, Long.toString(hours));

        boolean dispDays = days > 0;
        boolean dispHour = hours > 0;
        boolean dispMinute = minutes > 0;

        int index = (dispDays ? 1 : 0) |
                (dispHour ? 2 : 0) |
                (dispMinute ? 4 : 0);

        String[] formats = context.getResources().getStringArray(R.array.alarm_set);
        return String.format(formats[index], daySeq, hourSeq, minSeq);
    }

    @SuppressLint("NewApi")
    public static CharSequence get12ModeFormat(int amPmFontSize) {
        String skeleton = "hma";
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
        // Remove the am/pm
        if (amPmFontSize <= 0) {
            pattern.replaceAll("a", "").trim();
        }
        // Replace spaces with "Hair Space"
        pattern = pattern.replaceAll(" ", "\u200A");
        // Build a spannable so that the am/pm will be formatted
        int amPmPos = pattern.indexOf('a');
        if (amPmPos == -1) {
            return pattern;
        }
        Spannable sp = new SpannableString(pattern);
        sp.setSpan(new StyleSpan(Typeface.NORMAL), amPmPos, amPmPos + 1,
                Spannable.SPAN_POINT_MARK);
        sp.setSpan(new AbsoluteSizeSpan(amPmFontSize), amPmPos, amPmPos + 1,
                Spannable.SPAN_POINT_MARK);
        sp.setSpan(new TypefaceSpan("sans-serif"), amPmPos, amPmPos + 1,
                Spannable.SPAN_POINT_MARK);
        return sp;
    }

    @SuppressLint("NewApi")
    public static CharSequence get24ModeFormat() {
        String skeleton = "Hm";
        return DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
    }
    
    public static int getCurrentHourColor() {
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return Color.parseColor(BACKGROUND_SPECTRUM[hourOfDay]);
    }

    @SuppressLint("SimpleDateFormat")
    public static String[] getShortWeekdays() {
          final String[] shortWeekdays = new String[7];
          final SimpleDateFormat format = new SimpleDateFormat("EEEEE");
          long aSunday = new GregorianCalendar(2014, Calendar.JULY, 20).getTimeInMillis();
          for (int day = 0; day < 7; day++) {
              shortWeekdays[day] = format.format(new Date(aSunday + day * DateUtils.DAY_IN_MILLIS));
          }
          String[] sShortWeekdays = shortWeekdays;
//      }
      return sShortWeekdays;
  }
    
    public static void popAlarmSetToast(Context context, long timeInMillis) {
        String toastText = formatToast(context, timeInMillis);
        Log.i("Lipengwei","toastText =" + toastText);
        Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
        toast.show();
    }
    
    public static void startAlarmOnQuarterHour(Context context,AlarmInstance instance) {
        if (context != null) {
            long onQuarterHour = getAlarmOnQuarterHour(instance);
            PendingIntent quarterlyIntent = getOnQuarterHourPendingIntent(context, instance);
            AlarmManager alarmManager = ((AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE));
            alarmManager.set(AlarmManager.RTC_WAKEUP, onQuarterHour, quarterlyIntent);
        }
    }

    private static long getAlarmOnQuarterHour(AlarmInstance instance) {
        Calendar nextQuarter = Calendar.getInstance();
        nextQuarter.set(Calendar.SECOND, 0);
        nextQuarter.set(Calendar.MILLISECOND, 0);
        nextQuarter.set(Calendar.HOUR_OF_DAY, instance.mHour);
        nextQuarter.set(Calendar.MINUTE, instance.mMinute);
        nextQuarter.set(Calendar.DAY_OF_MONTH, instance.mDay);
        long alarmOnQuarterHour = nextQuarter.getTimeInMillis();
        long now = System.currentTimeMillis();
        Log.i("Lipengwei","now =" + now);
        long delta = alarmOnQuarterHour - now;
        Log.i("Lipengwei","delta =" + delta);
        if (0 >= delta) {
            alarmOnQuarterHour = alarmOnQuarterHour + 24*3600*1000;
        }
        return alarmOnQuarterHour;
    }

    /**
     * Remove the alarm for the quarter hour update.
     *
     * @param context The context in which the PendingIntent was started to perform the broadcast.
     */
    public static void cancelAlarmOnQuarterHour(Context context,AlarmInstance instance) {
        if (context != null) {
            PendingIntent quarterlyIntent = getOnQuarterHourPendingIntent(context, instance);
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(
                    quarterlyIntent);
        }
    }

    /**
     * Create the pending intent that is broadcast on the quarter hour.
     *
     * @param context The Context in which this PendingIntent should perform the broadcast.
     * @return a pending intent with an intent unique to DigitalAppWidgetProvider
     */
    private static PendingIntent getOnQuarterHourPendingIntent(Context context,AlarmInstance instance) {
    	Intent intent = AlarmInstance.createIntent(context, MyAlarmReceiver.class, instance.mId);
    	intent.setAction("com.lipengwei.myalarm.alarmon");
        mPendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return mPendingIntent;
    }
}

