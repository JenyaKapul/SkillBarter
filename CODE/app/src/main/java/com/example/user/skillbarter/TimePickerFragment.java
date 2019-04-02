package com.example.user.skillbarter;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    private static Calendar calendar = null;

    public static void setCalendar(Calendar calendar) {
        TimePickerFragment.calendar = calendar;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (TimePickerFragment.calendar == null){
            TimePickerFragment.calendar = Calendar.getInstance();
        }
        int hour = TimePickerFragment.calendar.get(Calendar.HOUR_OF_DAY);
        int minute = TimePickerFragment.calendar.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
        return tpd;
    }
}
