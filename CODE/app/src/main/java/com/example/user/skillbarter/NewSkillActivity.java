package com.example.user.skillbarter;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.ButterKnife;

//TODO: add validate function
//TODO: set save button disabled until second spinner is picked


public class NewSkillActivity extends BaseActivity implements EventListener<DocumentSnapshot> {

    public static final String KEY_SKILL_PATH = "key_skill_path";

    private static final String TAG = "NewSkillActivity";

    private Spinner mMainSpinner, mSecondarySpinner;

    private EditText mPointsView;

    // args for creating a new UserSkills object.
    private String mCategory, mSkill, mUserID;
    private int mPointsValue;

    private ListenerRegistration mListener;
    private DocumentReference mSkillRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "***** onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_skill);
        ButterKnife.bind(this);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Skill");

        mSecondarySpinner = findViewById(R.id.skills_spinner);


        // Get skill path from extras
        if (getIntent().getExtras() != null) {
             // user reached this intent by clicking on edit skill option.
            String path = getIntent().getExtras().getString(KEY_SKILL_PATH);
            showProgressDialog();
            mSkillRef = FirebaseFirestore.getInstance().document(path);
            mSecondarySpinner.setEnabled(true);

        } else {
            // user reached this intent by clicking the '+' sign in order to add a new skill.
        }

        mPointsView = findViewById(R.id.points);


        List<CharSequence> categories = Arrays.asList(this.getResources().getTextArray(R.array.skills_categories));

        // adapter for displaying hint 'Choose Category' in the first spinner.
        HintAdapter adapter = new HintAdapter(this, categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMainSpinner = findViewById(R.id.category_spinner);
        mMainSpinner.setAdapter(adapter);

        // show hint.
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

//        mSecondarySpinner = findViewById(R.id.skills_spinner);
        mSecondarySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondSpinnerChooser(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_skill_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_skill:
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
        Log.d(TAG, "***** saveUserSkill: ");
        mUserID = FirebaseAuth.getInstance().getUid();

        if (TextUtils.isEmpty(mPointsView.getText().toString())) {
            // user did not specify points value for his skill.
            mPointsView.setError("Required.");
            return;
        }

        mPointsValue = Integer.parseInt(mPointsView.getText().toString());
        UserSkills userSkill = new UserSkills(mUserID, mCategory, mSkill, mPointsValue);
        String docID = userSkill.getSkillId();

        // add user's skill to database.
        CollectionReference skillsCollection = FirebaseFirestore.getInstance()
                .collection("User Skills");
        skillsCollection.document(docID).set(userSkill);

        Toast.makeText(this, "Skill added", Toast.LENGTH_SHORT).show();

        finish();
    }

    private int getSkillArrayID(String categoryLabel) {
        int skillArrayID;
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
                skillArrayID = R.array.Empty;
        }
        return skillArrayID;

    }

    private void firstSpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String categoryLabel = parent.getItemAtPosition(position).toString();

        int skillArrayID = getSkillArrayID(categoryLabel);

        if (skillArrayID != R.array.Empty) {
            mSecondarySpinner.setEnabled(true);
            mCategory = categoryLabel;
        } else {
            mSecondarySpinner.setEnabled(false);
        }

//        setSecondSpinnerAdapter(skillArrayID);
        ///////////////////SECOND
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
            //TODO: set save button enabled here (it should be disabled by default)
//            findViewById(R.id.button_add).setEnabled(true);
//            findViewById(R.id.button_add).setBackgroundColor(0xFF039BE5); // primary background color

            mSkill = skillLabel;
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "***** onStart");
        super.onStart();
        if (mSkillRef != null) {
            mListener = mSkillRef.addSnapshotListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.remove();
            mListener = null;
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }

        if (snapshot.getReference().equals(mSkillRef)) {
            loadUserSkill(snapshot.toObject(UserSkills.class));
            hideProgressDialog();
        }
    }

    private void loadUserSkill(UserSkills userSkill) {

        // load category from database to main spinner.
        ArrayAdapter adapter = (ArrayAdapter) mMainSpinner.getAdapter();
        int spinnerPosition = adapter.getPosition(userSkill.getCategory());
        mMainSpinner.setSelection(spinnerPosition);

        // load skill from database to secondary spinner.

        mPointsView.setText(String.valueOf(userSkill.getPointsValue()));


        List<CharSequence> skillsList = Arrays.asList(this.getResources().getTextArray(getSkillArrayID(userSkill.getCategory())));


        HintAdapter adapter2 = new HintAdapter(this, skillsList,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSecondarySpinner.setAdapter(adapter2);
        spinnerPosition = adapter2.getPosition(userSkill.getSkill());
        mSecondarySpinner.setSelection(spinnerPosition);
    }
}
