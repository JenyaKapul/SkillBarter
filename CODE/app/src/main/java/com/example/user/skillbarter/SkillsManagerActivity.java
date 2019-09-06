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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class SkillsManagerActivity extends ActionBarMenuActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference skillsRef = db.collection("User Skills");

    private SkillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_manager);

        FloatingActionButton buttonAdd = findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SkillsManagerActivity.this, NewSkillActivity.class));
            }
        });

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        String uID = FirebaseAuth.getInstance().getUid();
        Query query = skillsRef.whereEqualTo("userID", uID);

        FirestoreRecyclerOptions<UserSkill> options = new FirestoreRecyclerOptions.Builder<UserSkill>()
                .setQuery(query, UserSkill.class)
                .build();

        adapter = new SkillAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SkillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String buttonClicked) {
//
                if (buttonClicked.equals("edit")) {
                    String skillPath = documentSnapshot.getReference().getPath();
                    Intent intent = new Intent(SkillsManagerActivity.this, NewSkillActivity.class);
                    intent.putExtra(EditSkillActivity.KEY_SKILL_PATH, skillPath);
                    startActivity(intent);
                }
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