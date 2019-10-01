package com.example.user.skillbarter.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.example.user.skillbarter.ActionBarMenuActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.adapters.AvailableDateAdapter;
import com.example.user.skillbarter.models.AvailableDate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class AvailableDatesManagerActivity extends ActionBarMenuActivity {
    private AvailableDateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_time_manager);

        FloatingActionButton buttonAdd = findViewById(R.id.free_time_button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AvailableDatesManagerActivity.this, NewAvailableDateActivity.class));
            }
        });

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        String uid = FirebaseAuth.getInstance().getUid();
        Query query = usersCollection.document(uid).collection("Available Dates")
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<AvailableDate> options = new FirestoreRecyclerOptions.Builder<AvailableDate>()
                .setQuery(query, AvailableDate.class)
                .build();

        adapter = new AvailableDateAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.free_time_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if(!adapter.deleteItem(viewHolder.getAdapterPosition())) {
                    Toast.makeText(AvailableDatesManagerActivity.this,
                            "Cannot delete already booked date", Toast.LENGTH_SHORT).show();
                    //ToDO: think of a better way
                    adapter.stopListening();
                    adapter.startListening();
                }
            }
        }).attachToRecyclerView(recyclerView);
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
