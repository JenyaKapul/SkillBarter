package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class SkillsManager extends ActionBarMenuActivity {
    private ArrayList<SkillItem> mSkillList;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference skillsRef = db.collection("User Skills");


//    private Button buttonRemove;
//    private EditText editTextInsert;
//    private EditText editTextRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_manager);
        ButterKnife.bind(this);

        loadUserSkills();

//        buildRecyclerView();

        buttonAdd = findViewById(R.id.button_add);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SkillsManager.this, AddSkillActivity.class);
                startActivity(intent);
            }
        });

        //TODO: implement remove and edit buttons per SkillItem
//        buttonRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = Integer.parseInt(editTextRemove.getText().toString());
//                removeItem(position);
//            }
//        });
    }

//    public void insertItem(int position) {
//        mSkillList.add(position, new ExampleItem(R.drawable.ic_android, "New Item At Position" + position, "This is Line 2"));
//        mAdapter.notifyItemInserted(position);
//    }

//    @OnClick(R.id.clearView)
//    public void onClearClicked() {
//
//    }

    public void removeItem(int position) {
        mSkillList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    // load user's skills from database.
    public void loadUserSkills() {
        mSkillList = new ArrayList<>();
        final String uID = FirebaseAuth.getInstance().getUid();

        skillsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //TODO: consider for optimizing this with querying the documents' key
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.getString("userID").equals(uID)) {
                        String skill = documentSnapshot.getString("skill");
                        mSkillList.add(new SkillItem(skill));
                    }
                }
                buildRecyclerView();
            }
        });

//        // **** get currently signed in user's id from sub-collection
//        String uID = FirebaseAuth.getInstance().getUid();
//        userRef.document(uID).collection("User Skills").get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            String skill = documentSnapshot.getString("skill");
////                            UserSkills userSkill = documentSnapshot.toObject(UserSkills.class);
//                            mSkillList.add(new SkillItem(skill));
//                        }
//                    }
//                });
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SkillAdapter(mSkillList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}