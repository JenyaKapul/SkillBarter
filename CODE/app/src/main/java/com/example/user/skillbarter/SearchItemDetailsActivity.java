package com.example.user.skillbarter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

public class SearchItemDetailsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item_details);
    }
}
