package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchResultActivity extends ActionBarMenuActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference skillsRef = db.collection("User Skills");

    private SkillAdapter adapter;
    private  FilterSearchResult filterOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_search_result);

        filterOptions = getIntent().getParcelableExtra("parcel_data");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = skillsRef.whereGreaterThanOrEqualTo("pointsValue", filterOptions.getMinPoints());
        query = query.whereLessThanOrEqualTo("pointsValue", filterOptions.getMaxPoints());

        if (filterOptions.getCategory() != null) {
            query = query.whereEqualTo("category", filterOptions.getCategory());
        }

        if (filterOptions.getSkill() != null) {
            query = query.whereEqualTo("skill", filterOptions.getSkill());
        }

        FirestoreRecyclerOptions<UserSkills> options = new FirestoreRecyclerOptions.Builder<UserSkills>()
                .setQuery(query, UserSkills.class)
                .build();

        adapter = new SkillAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SkillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String buttonClicked) {
                //TODO: move to intent presenting the skill
                Intent intent = new Intent(SearchResultActivity.this, SearchItemDetailsActivity.class);
                intent.putExtra(SearchItemDetailsActivity.KEY_SKILL_ID, documentSnapshot.getId());
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
