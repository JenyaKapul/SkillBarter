package com.example.user.skillbarter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HistoryActivity extends ActionBarMenuActivity {
    private static final String TAG = "HistoryActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference appointmentRef = db.collection("Appointments");

    private AppointmentAdapter adapter;
    private String currUID = FirebaseAuth.getInstance().getUid();
    private boolean currIsProvider = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Log.v(TAG, "setUpRecyclerView: setUpRecyclerView");
        Timestamp nowTimestamp = Timestamp.now();
        Query query = appointmentRef.orderBy("date", Query.Direction.ASCENDING);
//        if (this.currIsProvider) {
//            query = appointmentRef.whereEqualTo("providerUID", this.currUID);
//        }
//        else {
//            query = appointmentRef.whereEqualTo("clientUID", this.currUID);
//        }
//        query.whereLessThan("date", nowTimestamp).orderBy("date", Query.Direction.ASCENDING);

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
