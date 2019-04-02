package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class SkillsManager extends ActionBarMenuActivity {

    private static final String TAG = "SkillsManager";

    private ArrayList<SkillItem> mSkillList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference skillsRef = db.collection("User Skills");

    // keep the number of elements that are displayed on screen.
    private int size;


//    private Button buttonRemove;
//    private EditText editTextInsert;
//    private EditText editTextRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_manager);
        ButterKnife.bind(this);

        // initialize number of elements to display on screen.
        size = 0;

        loadUserSkills();

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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "***** onResume ");
        String str = "size " + mSkillList.size();
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

        if (mAdapter != null) {
            Log.d(TAG, String.valueOf(size) );
            mAdapter.notifyItemInserted(size);
        }

//        setupUI(findViewById(R.id.main_layout));
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
        Log.d(TAG, "***** loadUserSkills");

        final String uID = FirebaseAuth.getInstance().getUid();

        skillsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "***** loadUserSkills: onSuccess");
                //TODO: consider for optimizing this with querying the documents' key
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    if (documentSnapshot.getString("userID").equals(uID)) {
                        String skill = documentSnapshot.getString("skill");
                        mSkillList.add(new SkillItem(skill));
//                        size ++;
                    }
                }
                size = mSkillList.size();
                Log.d(TAG, "***** loadUserSkills: document at size " + size);
                buildRecyclerView();
            }
        });
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