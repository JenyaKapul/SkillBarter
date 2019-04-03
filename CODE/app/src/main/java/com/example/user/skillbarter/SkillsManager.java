package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


    // TODO: consider optimizing this with querying the documents' key

public class SkillsManager extends ActionBarMenuActivity {
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
                startActivity(new Intent(SkillsManager.this, NewSkillActivity.class));
            }
        });

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        String uID = FirebaseAuth.getInstance().getUid();
        Query query = skillsRef.whereEqualTo("userID", uID);

        FirestoreRecyclerOptions<UserSkills> options = new FirestoreRecyclerOptions.Builder<UserSkills>()
                .setQuery(query, UserSkills.class)
                .build();

        adapter = new SkillAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SkillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String buttonClicked) {

                if (buttonClicked.equals("delete")) {
                    documentSnapshot.getReference().delete();
                } else if (buttonClicked.equals("edit")) {
                    //TODO: edit the document
                    String skillPath = documentSnapshot.getReference().getPath();
                    Intent intent = new Intent(SkillsManager.this, NewSkillActivity.class);
                    intent.putExtra(NewSkillActivity.KEY_SKILL_PATH, skillPath);
                    startActivity(intent);

                }
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
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