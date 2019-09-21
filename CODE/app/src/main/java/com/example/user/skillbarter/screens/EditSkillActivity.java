package com.example.user.skillbarter.screens;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.user.skillbarter.BaseActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.models.UserSkill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;


import javax.annotation.Nullable;

import butterknife.ButterKnife;



public class EditSkillActivity extends BaseActivity implements EventListener<DocumentSnapshot> {

    public static final String KEY_SKILL_PATH = "key_skill_path";

    private static final String TAG = "EditSkillActivity";

    private SeekBar mLevelSeekBar;
    private EditText mPointsView, mDetailsView;
    private TextView categoryTextView, skillTextView;
    private CheckBox isEnabledCheckbox;

    // args for creating a new UserSkill object.
    private String mCategory, mSkill, mUserID, mDetails;
    private int mPointsValue, mLevel;
    private boolean mIsEnabled;

    private ListenerRegistration mListener;
    private DocumentReference mSkillRef;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_skill);
        ButterKnife.bind(this);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Edit Skill");

        categoryTextView = findViewById(R.id.category_text_view);
        skillTextView = findViewById(R.id.skill_text_view);
        mLevelSeekBar = findViewById(R.id.skillLevelSeekBar);
        mDetailsView = findViewById(R.id.skill_details);
        mPointsView = findViewById(R.id.points);
        isEnabledCheckbox = findViewById(R.id.is_enabled_checkbox);

        String path = getIntent().getExtras().getString(KEY_SKILL_PATH);
        showProgressDialog();
        mSkillRef = mFirestore.document(path);

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
            case R.id.save_skill:
                saveUserSkill();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * edit skill in database.
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
        mIsEnabled = !isEnabledCheckbox.isChecked();
        UserSkill userSkill = new UserSkill(mUserID, mCategory, mSkill, mPointsValue, mLevel +1 , mDetails, mIsEnabled);
        String docID = userSkill.getSkillId();

        // add user's skill to database.
        skillsCollectionRef.document(docID).set(userSkill);

        finish();
    }


    @Override
    public void onStart() {
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
            loadUserSkill(snapshot.toObject(UserSkill.class));
            hideProgressDialog();
        }
    }

    private void loadUserSkill(UserSkill userSkill) {
        mCategory = userSkill.getCategory();
        categoryTextView.setText(mCategory);
        mSkill = userSkill.getSkill();
        skillTextView.setText(mSkill);
        mPointsView.setText(String.valueOf(userSkill.getPointsValue()));
        mLevelSeekBar.setProgress(userSkill.getLevel() - 1);
        mDetailsView.setText(userSkill.getDetails());
        isEnabledCheckbox.setChecked(!userSkill.isEnabled());
    }
}
