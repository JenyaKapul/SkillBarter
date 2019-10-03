package com.example.user.skillbarter.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.user.skillbarter.ActionBarMenuActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.adapters.AvailableDateAdapter;
import com.example.user.skillbarter.models.AvailableDate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.skillbarter.Constants.DATES_COLLECTION;

/*
 * TODO (NOA)
 *  (1) Add option to delete an item with swipe if the date is not booked!
 */


public class AvailableDatesManagerActivity extends ActionBarMenuActivity {

    @BindView(R.id.add_button)
    FloatingActionButton addButton;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private AvailableDateAdapter adapter;
    private Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_dates_manager);
        ButterKnife.bind(this);
        setTitle(R.string.available_dates_manager_title);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AvailableDatesManagerActivity.this, NewAvailableDateActivity.class));
            }
        });

        initRecyclerView();
    }


    private void initRecyclerView() {
        query = usersCollection.document(currentUser.getUid()).collection(DATES_COLLECTION)
                .orderBy(DATE, Query.Direction.ASCENDING).limit(LIMIT);

        FirestoreRecyclerOptions<AvailableDate> options = new FirestoreRecyclerOptions.Builder<AvailableDate>()
                .setQuery(query, AvailableDate.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new AvailableDateAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    protected void onStart() {
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
}
