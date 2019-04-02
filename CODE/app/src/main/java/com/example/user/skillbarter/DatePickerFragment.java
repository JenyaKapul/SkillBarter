package com.example.user.skillbarter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private static Calendar calendar = null;

    public static void setCalendar(Calendar calendar) {
        DatePickerFragment.calendar = calendar;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (DatePickerFragment.calendar == null){
            DatePickerFragment.calendar = Calendar.getInstance();
        }

        int year = DatePickerFragment.calendar.get(Calendar.YEAR);
        int month = DatePickerFragment.calendar.get(Calendar.MONTH);
        int day = DatePickerFragment.calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),
                (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
    }
}
