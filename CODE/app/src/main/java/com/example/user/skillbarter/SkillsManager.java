package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


    // TODO: consider optimizing this with querying the documents' key
    // TODO: change the action bar menu activity to save and close: https://www.youtube.com/watch?v=1YMK2SatG8o&list=PLrnPJCHvNZuBf5KH4XXOthtgo6E4Epjl8&index=31


public class SkillsManager extends ActionBarMenuActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference skillsRef = db.collection("User Skills");
    private String uID = FirebaseAuth.getInstance().getUid();

    private SkillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_manager);

        FloatingActionButton buttonAddSkill = findViewById(R.id.button_add);
        buttonAddSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SkillsManager.this, NewSkillActivity.class));
            }
        });

        setUpRecyclerView();
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

    private void setUpRecyclerView() {
        Query query = skillsRef.whereEqualTo("userID", uID)
                .orderBy("skill", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<UserSkills> options = new FirestoreRecyclerOptions
                .Builder<UserSkills>().setQuery(query, UserSkills.class)
                .build();

        adapter = new SkillAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}