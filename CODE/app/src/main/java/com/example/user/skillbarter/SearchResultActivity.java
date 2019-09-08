package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchResultActivity extends ActionBarMenuActivity {
    private static final String TAG = "SearchResultActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference skillsRef = db.collection("User Skills");

    private SearchResultAdapter adapter;
    private  FilterSearchResult filterOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_search_result);

        filterOptions = getIntent().getParcelableExtra("parcel_data");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Log.v(TAG, "setUpRecyclerView: entry setUpRecyclerView");
        Query query = skillsRef.whereGreaterThanOrEqualTo("pointsValue", filterOptions.getMinPoints());
        query = query.whereLessThanOrEqualTo("pointsValue", filterOptions.getMaxPoints());

        if (filterOptions.getCategory() != null) {
            query = query.whereEqualTo("category", filterOptions.getCategory());
        }

        if (filterOptions.getSkill() != null) {
            query = query.whereEqualTo("skill", filterOptions.getSkill());
        }

        // filter only enabled skills
        query = query.whereEqualTo("enabled", true);

        FirestoreRecyclerOptions<UserSkill> options = new FirestoreRecyclerOptions.Builder<UserSkill>()
                .setQuery(query, UserSkill.class)
                .build();

        adapter = new SearchResultAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Log.v(TAG, "setUpRecyclerView: before setLayoutManager");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(SearchResultActivity.this, SearchItemDetailsActivity.class);
                intent.putExtra(SearchItemDetailsActivity.KEY_SKILL_ID, documentSnapshot.getId());
                Log.d(TAG, "onItemClick:");
                startActivity(intent);
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
