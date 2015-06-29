package com.lipengwei.myalarm;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.lipengwei.myalarm.provider.Alarm;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    private Alarm mAlarm;
    private OnTimeSetListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int hour, minute;
        if (mAlarm == null) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } else {
            hour = mAlarm.hour;
            minute = mAlarm.minutes;
        }

        return new TimePickerDialog(getActivity(), mListener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getTargetFragment() instanceof OnTimeSetListener) {
            setOnTimeSetListener((OnTimeSetListener) getTargetFragment());
        }
    }

    public void setOnTimeSetListener(OnTimeSetListener listener) {
        mListener = listener;
    }

    public void setAlarm(Alarm alarm) {
        mAlarm = alarm;
    }
}

