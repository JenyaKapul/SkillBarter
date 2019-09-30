package com.example.user.skillbarter.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.user.skillbarter.ActionBarMenuActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.adapters.FreeTimeAdapter;
import com.example.user.skillbarter.models.FreeTime;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class FreeTimeManagerActivity extends ActionBarMenuActivity {
    private FreeTimeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_time_manager);

        FloatingActionButton buttonAdd = findViewById(R.id.free_time_button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FreeTimeManagerActivity.this, NewFreeTimeActivity.class));
            }
        });

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        String uid = FirebaseAuth.getInstance().getUid();
        Query query = usersCollectionRef.document(uid).collection("Free Time")
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FreeTime> options = new FirestoreRecyclerOptions.Builder<FreeTime>()
                .setQuery(query, FreeTime.class)
                .build();

        adapter = new FreeTimeAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.free_time_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
