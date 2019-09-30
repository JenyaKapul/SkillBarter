package com.example.user.skillbarter.screens;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.user.skillbarter.BaseActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.models.AvailableDate;
import com.example.user.skillbarter.utils.TimePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.timessquare.CalendarPickerView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewAvailableDateActivity extends BaseActivity implements TimePickerDialog.OnTimeSetListener{

    private Date chosenDate = null;
    private int chosenHour = -1;
    private int chosenMinute = -1;

    @BindView(R.id.free_time_calendar)
    CalendarPickerView datePicker;

    @BindView(R.id.date_picker_text_view)
    TextView tvDate;

    @BindView(R.id.time_picker_image)
    ImageButton timePicker;

    @BindView(R.id.time_picker_text_view)
    TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_free_time);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Available Time");

        ButterKnife.bind(this);
        this.initDatePicker();
        this.initTimePicker();
    }

    private void initDatePicker() {
        Date today = new Date();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        datePicker.init(today, nextYear.getTime()).withSelectedDate(today);
        datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                chosenDate = date;
                tvDate.setError(null);
                String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
                datePicker.setVisibility(View.INVISIBLE);
                Toast.makeText(NewAvailableDateActivity.this, selectedDate, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDateUnselected(Date date) {}
        });
    }

    private void initTimePicker() {
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                saveFreeTime();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateInput() {
        boolean status = true;
        if (chosenDate == null) {
            tvDate.setError("Required!");
            status = false;
        }
        if ((chosenHour == -1) || (chosenMinute == -1)) {
            tvTime.setError("Required!");
            status = false;
        }
        return status;
    }

    private void saveFreeTime(){
        String mUserID = FirebaseAuth.getInstance().getUid();

        if (!validateInput()){
            return;
        }
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(chosenDate);
        selectedDate.set(Calendar.HOUR_OF_DAY, chosenHour);
        selectedDate.set(Calendar.MINUTE, chosenMinute);
        final Date date = selectedDate.getTime();

        final AvailableDate availableDate = new AvailableDate(date);
        final String docID = new SimpleDateFormat("dd.MM.yy hh:mm").format(date);

        final CollectionReference userFreeTimeRef = usersCollectionRef.document(mUserID).
                collection("Available Dates");

        userFreeTimeRef.document(docID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Toast.makeText(NewAvailableDateActivity.this,
                                "This time slot already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // add time slot to database.
                        userFreeTimeRef.document(docID).set(availableDate);
                        finish();
                    }
                }
            }
        });
    }

    public void OnDatePickerClicked(View v) {
        this.datePicker.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        tvTime.setError(null);
        this.chosenHour = hourOfDay;
        this.chosenMinute = minute;
        String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
        tvTime.setText(time);
    }
}
