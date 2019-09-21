package com.example.user.skillbarter.screens;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.user.skillbarter.ActionBarMenuActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.adapters.AppointmentAdapter;
import com.example.user.skillbarter.models.Appointment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.polyak.iconswitch.IconSwitch;

import java.util.Date;

public class HistoryActivity extends ActionBarMenuActivity {
    private static final String TAG = "HistoryActivity";

    private AppointmentAdapter adapter;

    private String currUID = FirebaseAuth.getInstance().getUid();
    private IconSwitch iconSwitch;
    private boolean currIsProvider = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        iconSwitch = findViewById(R.id.history_appointments_type_switch);
        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                adapter.stopListening();
                if(current == IconSwitch.Checked.LEFT){
                    currIsProvider = false;
                }
                else {
                    currIsProvider = true;
                }
                setUpRecyclerView();
                adapter.startListening();
            }
        });
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Log.v(TAG, "setUpRecyclerView: setUpRecyclerView");
        Timestamp nowDate = new Timestamp(new Date());
//        Query query = appointmentRef.orderBy("date", Query.Direction.DESCENDING);
        Query query;
        if (this.currIsProvider) {
            query = appointmentsCollectionRef.whereEqualTo("providerUID", this.currUID);
        }
        else {
            query = appointmentsCollectionRef.whereEqualTo("clientUID", this.currUID);
        }
        query = query.whereLessThan("date", nowDate).orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class).build();

        adapter = new AppointmentAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Log.v(TAG, "onItemClick: setOnItemClickListener");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
