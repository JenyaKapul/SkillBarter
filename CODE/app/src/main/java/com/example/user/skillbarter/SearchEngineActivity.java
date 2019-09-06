package com.example.user.skillbarter;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Calendar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchEngineActivity extends ActionBarMenuActivity implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "SearchEngineActivity";
	
	private static final int TIME_NONE = 0;
    private static final int TIME_FROM = 1;
    private static final int TIME_TO = 2;
	
	private Spinner categorySpinner, skillSpinner;
    private SeekBar mLevelSeekBar;

    private Calendar time_from = null;
    private Calendar time_to = null;
    private int currentTimePicker = SearchEngineActivity.TIME_NONE;


    // args for creating a new FilterSearchResult object.
    private String mCategory, mSkill;
    private int mPointsMin, mPointsMax;
    private int mMinLevel;



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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "***** onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_engine);
        ButterKnife.bind(this);

        categorySpinner = findViewById(R.id.category_spinner);
        skillSpinner = findViewById(R.id.skills_spinner);
        mLevelSeekBar = findViewById(R.id.skillLevelSeekBar);

        List<CharSequence> categories = Arrays.asList(this.getResources().getTextArray(R.array.skills_categories));

        // adapter for displaying hint 'Choose Category' in the first spinner.
        HintAdapter adapter = new HintAdapter(this, categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // show hint.
        categorySpinner.setSelection(adapter.getCount());

        // add listener to the category spinner in order to load the correct data for the skills spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySpinnerChooser(parent, view, position, id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        skillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                skillSpinnerChooser(parent, view, position, id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void categorySpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        String categoryLabel = parent.getItemAtPosition(position).toString();

        int skillArrayID = getSkillArrayID(categoryLabel);

        if (skillArrayID != R.array.Empty) {
            skillSpinner.setEnabled(true);
            mCategory = categoryLabel;
        } else {
            skillSpinner.setEnabled(false);
        }

        List<CharSequence> skillsList = Arrays.asList(this.getResources().getTextArray(skillArrayID));

        HintAdapter adapter = new HintAdapter(this, skillsList,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        skillSpinner.setAdapter(adapter);

        // show hint.
        skillSpinner.setSelection(adapter.getCount());
    }


    private void skillSpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        String skillLabel = parent.getItemAtPosition(position).toString();
        if (!skillLabel.startsWith("Choose")) {
            mSkill = skillLabel;
        }
    }


    @OnClick(R.id.time_from_picker)
    public void onTimeFromPickerClicked() {
        this.currentTimePicker = SearchEngineActivity.TIME_FROM;
        if (this.time_from != null){
            TimePickerFragment.setCalendar(this.time_from);
        }
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time from picker");
    }


    @OnClick(R.id.time_to_picker)
    public void onTimeToPickerClicked() {
        this.currentTimePicker = SearchEngineActivity.TIME_TO;
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
        if (this.currentTimePicker == SearchEngineActivity.TIME_FROM){
            this.time_from = c;
            timeFromView.setText(currTimeString);
        }
        else if (this.currentTimePicker == SearchEngineActivity.TIME_TO){
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

    @OnClick(R.id.search_button)
    public void onSearchClicked() {
        String minPointsStr = pointsMinView.getText().toString();
        mMinLevel = mLevelSeekBar.getProgress() + 1;
        if (TextUtils.isEmpty(minPointsStr)) {
            mPointsMin = 0;
        } else {
            mPointsMin = Integer.parseInt(minPointsStr);
        }

        String maxPointsStr = pointsMaxView.getText().toString();
        if (TextUtils.isEmpty(maxPointsStr)) {
            mPointsMax = Integer.MAX_VALUE;
        } else {
            mPointsMax = Integer.parseInt(maxPointsStr);
        }

        FilterSearchResult filterSearchResult = new FilterSearchResult(mCategory, mSkill, mPointsMin, mPointsMax, mMinLevel);

        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("parcel_data", filterSearchResult);
        startActivity(intent);
    }
}