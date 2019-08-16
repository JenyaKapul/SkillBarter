package com.example.user.skillbarter;

import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.applandeo.materialcalendarview.CalendarView;
//import com.applandeo.materialcalendarview.EventDay;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;

public class CalendarView_A extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CalendarView_A";
    Button button;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

//        List<EventDay> events = new ArrayList<>();
//
//        Calendar calendar = Calendar.getInstance();
//        events.add(new EventDay(calendar, R.drawable.calendar_icon));
//
//        CalendarView cv = (CalendarView) findViewById(R.id.calendarView);
////        cv.setEvents(events);

        button = findViewById(R.id.button);
        button.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI,null,null,null, null);
                while (cursor.moveToNext()){
                    if(cursor!=null){
                        int id_1 = cursor.getColumnIndex(CalendarContract.Events._ID);
                        int id_2 = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                        int id_3 = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
                        int id_4 = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);

                        String idValue = cursor.getColumnName(id_1);
                        String titleValue = cursor.getString(id_2);
                        String descriptionValue = cursor.getString(id_3);
                        String locationValue = cursor.getString(id_4);

                        Toast.makeText(this, idValue + " " + titleValue + " " + descriptionValue + " " + locationValue, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, "NO EVENT", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
