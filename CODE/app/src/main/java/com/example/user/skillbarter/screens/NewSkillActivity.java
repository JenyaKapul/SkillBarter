package com.example.user.skillbarter.screens;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.skillbarter.BaseActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.adapters.HintAdapter;
import com.example.user.skillbarter.models.UserSkill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;

public class NewSkillActivity extends BaseActivity {

    private Spinner categorySpinner, skillSpinner;
    private SeekBar mLevelSeekBar;
    private EditText mPointsView, mDetailsView;

    // args for creating a new UserSkill object.
    private String mCategory, mSkill, mUserID, mDetails;
    private int mPointsValue, mLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_skill);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Skill");

        categorySpinner = findViewById(R.id.category_spinner);
        skillSpinner = findViewById(R.id.skills_spinner);
        mLevelSeekBar = findViewById(R.id.skillLevelSeekBar);
        mDetailsView = findViewById(R.id.skill_details);
        mPointsView = findViewById(R.id.points);

        List<CharSequence> categories = Arrays.asList(this.getResources().getTextArray(R.array.skills_categories));

        // adapter for displaying hint 'Choose Category' in the first spinner.
        HintAdapter adapter = new HintAdapter(this, categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // show hint.
        categorySpinner.setSelection(adapter.getCount());

        // add listener to the first spinner in order to load the correct data
        // for secondary spinner.
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySpinnerChooser(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        skillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                skillSpinnerChooser(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item:
                saveUserSkill();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * add skill to database. Each skill is added to the child collection 'User Skills'
     * under collection 'User Data'
     */
    private void saveUserSkill() {
        mUserID = FirebaseAuth.getInstance().getUid();

        if (TextUtils.isEmpty(mPointsView.getText().toString())) {
            // user did not specify points value for his skill.
            mPointsView.setError("Required.");
            return;
        }

        mPointsValue = Integer.parseInt(mPointsView.getText().toString());
        mLevel = mLevelSeekBar.getProgress();
        mDetails = mDetailsView.getText().toString();
        final UserSkill userSkill = new UserSkill(mUserID, mCategory, mSkill, mPointsValue, mLevel +1 , mDetails);
        final String docID = userSkill.getSkillId();

        DocumentReference skillRef = skillsCollectionRef.document(docID);

        skillRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Toast.makeText(NewSkillActivity.this, "This skill already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // add user's skill to database.
                        skillsCollectionRef.document(docID).set(userSkill);

                        finish();
                    }
                }
            }
        });

    }

    private void categorySpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String categoryLabel = parent.getItemAtPosition(position).toString();

        int skillArrayID = getSkillArrayID(categoryLabel);

        if (skillArrayID != R.array.Empty) {
            skillSpinner.setEnabled(true);
            mCategory = categoryLabel;
        } else {
            skillSpinner.setEnabled(false);
        }

        List<CharSequence> skillsList = Arrays.asList(this.getResources().getTextArray(skillArrayID));

        HintAdapter adapter = new HintAdapter(this, skillsList,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        skillSpinner.setAdapter(adapter);

        // show hint.
        skillSpinner.setSelection(adapter.getCount());
    }

    private void skillSpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        String skillLabel = parent.getItemAtPosition(position).toString();
        if (!skillLabel.startsWith("Choose")) {
            mSkill = skillLabel;
        }
    }
}
