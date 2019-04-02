package com.example.user.skillbarter;

import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchEngine extends ActionBarMenuActivity implements TimePickerDialog.OnTimeSetListener {
    private static final int TIME_NONE = 0;
    private static final int TIME_FROM = 1;
    private static final int TIME_TO = 2;

    private Calendar time_from = null;
    private Calendar time_to = null;
    private int currentTimePicker = SearchEngine.TIME_NONE;


    @BindView(R.id.max_distance)
    EditText maxDistance;

    @BindView(R.id.sunday)
    CheckBox sunday;

    @BindView(R.id.monday)
    CheckBox monday;

    @BindView(R.id.tuesday)
    CheckBox tuesday;

    @BindView(R.id.wednesday)
    CheckBox wednesday;

    @BindView(R.id.thursday)
    CheckBox thursday;

    @BindView(R.id.friday)
    CheckBox friday;

    @BindView(R.id.saturday)
    CheckBox saturday;

    @BindView(R.id.time_from)
    EditText timeFromView;

    @BindView(R.id.time_to)
    EditText timeToView;

    @BindView(R.id.points_min)
    EditText pointsMinView;

    @BindView(R.id.points_max)
    EditText pointsMaxView;

    @BindView(R.id.search_button)
    Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_engine);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.time_from_picker)
    public void onTimeFromPickerClicked() {
        this.currentTimePicker = SearchEngine.TIME_FROM;
        if (this.time_from != null){
            TimePickerFragment.setCalendar(this.time_from);
        }
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time from picker");
    }

    @OnClick(R.id.time_to_picker)
    public void onTimeToPickerClicked() {
        this.currentTimePicker = SearchEngine.TIME_TO;
        if (this.time_to != null){
            TimePickerFragment.setCalendar(this.time_to);
        }
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time to picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        String currTimeString = formatTime(hourOfDay, minute);
        if (this.currentTimePicker == SearchEngine.TIME_FROM){
            this.time_from = c;
            timeFromView.setText(currTimeString);
        }
        else if (this.currentTimePicker == SearchEngine.TIME_TO){
            this.time_to = c;
            timeToView.setText(currTimeString);
        }
        validateTimeRange(this.time_from, this.time_to);
    }

    private boolean validateTimeRange(Calendar from, Calendar to){
        if ((from != null) && (to != null) ){
            int hoursDiff = to.get(Calendar.HOUR_OF_DAY) - from.get(Calendar.HOUR_OF_DAY);
            int minutesDiff = to.get(Calendar.MINUTE) - from.get(Calendar.MINUTE);
            if (!((hoursDiff > 0) || ((hoursDiff == 0) && (minutesDiff > 0)))){
                Toast.makeText(this, "INVALID TIME RANGE", Toast.LENGTH_SHORT).show();
                timeFromView.setError("Time must be earlier than upper range limit");
                timeToView.setError("Time must be later than bottom range limit");
                return false;
            }
        }
        return true;
    }
}
