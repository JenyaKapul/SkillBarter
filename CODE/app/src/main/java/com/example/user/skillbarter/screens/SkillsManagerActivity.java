package com.example.user.skillbarter.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.skillbarter.ActionBarMenuActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.adapters.EditableSkillAdapter;
import com.example.user.skillbarter.models.UserSkill;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SkillsManagerActivity extends ActionBarMenuActivity {

    @BindView(R.id.skills_manager_add_button)
    FloatingActionButton buttonAdd;

    @BindView(R.id.skills_manager_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.view_empty_skills_manager)
    ViewGroup emptyView;

    private EditableSkillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_manager);
        ButterKnife.bind(this);

        setTitle(R.string.skills_manager_title);


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
        Query query = skillsCollection.whereEqualTo("userID", uID);

        FirestoreRecyclerOptions<UserSkill> options = new FirestoreRecyclerOptions.Builder<UserSkill>()
                .setQuery(query, UserSkill.class)
                .build();

        adapter = new EditableSkillAdapter(options) {
            @Override
            public void onDataChanged() {
                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new EditableSkillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String buttonClicked) {
                if (buttonClicked.equals("edit")) {
                    String skillPath = documentSnapshot.getReference().getPath();
                    Intent intent = new Intent(SkillsManagerActivity.this, EditSkillActivity.class);
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