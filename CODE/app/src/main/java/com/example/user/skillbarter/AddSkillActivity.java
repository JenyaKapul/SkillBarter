package com.example.user.skillbarter;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddSkillActivity extends ActionBarMenuActivity {

    //TODO: add validate function

    private static final String TAG = "AddSkillActivity";

    private Spinner mMainSpinner, mSecondarySpinner;

    private EditText mPointsView;

    // args for creating a new UserSkills object
    private String mCategory, mSkill, mUserID;
    private int mPointsValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_skill);
        ButterKnife.bind(this);

        mPointsView = findViewById(R.id.points);

        List<CharSequence> categories = Arrays.asList(this.getResources().getTextArray(R.array.skills_categories));

        HintAdapter adapter = new HintAdapter(this, categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMainSpinner = findViewById(R.id.category_spinner);
        mMainSpinner.setAdapter(adapter);

        // show hint
        mMainSpinner.setSelection(adapter.getCount());

        // add listener to the first spinner in order to load the correct data
        // for secondary spinner.
        mMainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstSpinnerChooser(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mSecondarySpinner = findViewById(R.id.skills_spinner);
        mSecondarySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondSpinnerChooser(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @OnClick(R.id.button_add)
    public void onAddClicked() {
        Log.d(TAG, "***** onAddClicked");
        saveUserSkill();
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
        UserSkills userSkill = new UserSkills(mUserID, mCategory, mSkill, mPointsValue);
        String docID = userSkill.getSkillId();

        // add sub-collection
//        CollectionReference userRef = FirebaseFirestore.getInstance().collection("User Data");
//        userRef.document(mUserID).collection("User Skills").document(docID).set(userSkill);

        // add user's skill to database.
        CollectionReference skillsCollection = FirebaseFirestore.getInstance().collection("User Skills");
        skillsCollection.document(docID).set(userSkill);

        finish();
    }

    private void firstSpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        int skillArrayID = R.array.Empty;
        String categoryLabel = parent.getItemAtPosition(position).toString();

        switch (categoryLabel) {
            case "Tutoring":
                skillArrayID = R.array.Tutoring;
                break;
            case "Music":
                skillArrayID = R.array.Music;
                break;
            case "Dance":
                skillArrayID = R.array.Dance;
                break;
            case "Arts and Crafts":
                skillArrayID = R.array.arts_and_crafts;
                break;
            case "Sport":
                skillArrayID = R.array.Sport;
                break;
            case "Household Services":
                skillArrayID = R.array.Household;
                break;
            case "Beauty Care":
                skillArrayID = R.array.Beauty;
                break;
            case "Culinary":
                skillArrayID = R.array.Culinary;
                break;
            default:
                mSecondarySpinner.setEnabled(false);
        }

        if (skillArrayID != R.array.Empty) {
            mSecondarySpinner.setEnabled(true);
            mCategory = categoryLabel;
        }
        List<CharSequence> skillsList = Arrays.asList(this.getResources().getTextArray(skillArrayID));

        HintAdapter adapter = new HintAdapter(this, skillsList,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSecondarySpinner.setAdapter(adapter);

        // show hint.
        mSecondarySpinner.setSelection(adapter.getCount());
    }

    private void secondSpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        String skillLabel = parent.getItemAtPosition(position).toString();
        if (!skillLabel.startsWith("Choose")) {
            findViewById(R.id.button_add).setEnabled(true);
            findViewById(R.id.button_add).setBackgroundColor(0xFF039BE5); // primary background color

            mSkill = skillLabel;
        }
    }
}
