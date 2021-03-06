package com.example.user.skillbarter.screens;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.user.skillbarter.BaseActivity;
import com.example.user.skillbarter.DatePickerFragment;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.models.AvailableDate;
import com.example.user.skillbarter.utils.TimePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.skillbarter.Constants.DATES_COLLECTION;


public class NewAvailableDateActivity extends BaseActivity
        implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private Date selectedDate;
    private boolean isHourSelected = false;

    @BindView(R.id.date_picker_text_view)
    TextView datePickerTextView;

    @BindView(R.id.time_picker_image)
    ImageButton timePickerImage;

    @BindView(R.id.time_picker_text_view)
    TextView timePickerTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_available_date);
        ButterKnife.bind(this);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle(R.string.new_date_title);
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
                saveDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        selectedDate = calendar.getTime();
        datePickerTextView.setText(new SimpleDateFormat("dd.MM.yyyy").format(selectedDate));
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        isHourSelected = true;
        selectedDate.setHours(hourOfDay);
        selectedDate.setMinutes(minute);
        String timeFormatted = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
        timePickerTextView.setText(timeFormatted);
    }


    public void OnDatePickerClicked(View v) {
        DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "date picker");
    }


    public void OnTimePickerClicked(View v) {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(), "time picker");
    }


    private boolean validateInput() {

        boolean isValid = true;

        if (selectedDate == null) {
            datePickerTextView.setError("Required!");
            isValid = false;
        }

        if (!isHourSelected) {
            timePickerTextView.setError("Required!");
            isValid = false;
        }

        Date today = new Date();
        if (selectedDate.before(today)) {
            Toast.makeText(NewAvailableDateActivity.this,
                    R.string.date_passed_message, Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }


    private void saveDate() {

        if (!validateInput()) {
            return;
        }

        final String docID = new SimpleDateFormat("dd.MM.yy HH:mm").format(selectedDate);

        final CollectionReference availableDates = usersCollection.document(currentUser.getUid()).
                collection(DATES_COLLECTION);

        availableDates.document(docID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Toast.makeText(NewAvailableDateActivity.this,
                                R.string.date_already_exists_message, Toast.LENGTH_SHORT).show();
                    } else {
                        // add time slot to database.
                        final AvailableDate availableDate = new AvailableDate(selectedDate);
                        availableDates.document(docID).set(availableDate);
                        finish();
                    }
                }
            }
        });
    }
}
