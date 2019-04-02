package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


public class DisplaySkillsActivity extends ActionBarMenuActivity {
    private ArrayList<SkillItem> mSkillList;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonAdd;
//    private Button buttonRemove;
//    private EditText editTextInsert;
//    private EditText editTextRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_skills);

        createSkillList();
        buildRecyclerView();

        buttonAdd = findViewById(R.id.button_add);
//        buttonInsert = findViewById(R.id.button_insert);
//        buttonRemove = findViewById(R.id.button_remove);
//        editTextInsert = findViewById(R.id.edittext_insert);
//        editTextRemove = findViewById(R.id.edittext_remove);

//        buttonAdd.setnocli
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int position = Integer.parseInt(editTextInsert.getText().toString());
//                insertItem(position);
                Intent intent = new Intent(DisplaySkillsActivity.this, SkillsManager.class);
                startActivity(intent);
            }
        });

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

    public void removeItem(int position) {
        mSkillList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void createSkillList() {
        mSkillList = new ArrayList<>();
        mSkillList.add(new SkillItem("Piano"));
        mSkillList.add(new SkillItem("Cooking"));
        mSkillList.add(new SkillItem("Cooking"));
        mSkillList.add(new SkillItem("Cooking"));
        mSkillList.add(new SkillItem("Cooking"));
        mSkillList.add(new SkillItem("Cooking"));
        mSkillList.add(new SkillItem("Cooking"));
        mSkillList.add(new SkillItem("Cooking"));
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