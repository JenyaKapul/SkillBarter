package com.example.user.skillbarter.screens;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.user.skillbarter.ActionBarMenuActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.adapters.AppointmentAdapter;
import com.example.user.skillbarter.models.Appointment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.polyak.iconswitch.IconSwitch;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.skillbarter.Constants.APPOINTMENTS_COLLECTION;

public class HistoryActivity extends ActionBarMenuActivity {

    @BindView(R.id.history_appointments_type_switch)
    IconSwitch iconSwitch;

    @BindView(R.id.history_recycler_view)
    RecyclerView recyclerView;
    
    private AppointmentAdapter adapter;
    String userType = "clientUID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        setTitle(R.string.history_title);

        initRecyclerView();

        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                switch (current) {
                    case LEFT:
                        userType = "clientUID";
                        break;
                    case RIGHT:
                        userType = "providerUID";
                        break;
                }
                initRecyclerView();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }


    private void initRecyclerView() {
        Query query = FirebaseFirestore.getInstance().collection(APPOINTMENTS_COLLECTION)
                .whereEqualTo(userType, FirebaseAuth.getInstance().getUid());
        query = query.whereEqualTo("providerPaid", true);


        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new AppointmentAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        // Optional for adding clicks functionality on appointment items
        adapter.setOnItemClickListener(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
            }
        });
    }
}
