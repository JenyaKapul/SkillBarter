package com.example.user.skillbarter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchItemDetailsActivity extends AppCompatActivity {
    private static final String TAG = "SearchItemDetailsAct";
    public static final String KEY_SKILL_ID = "key_skill_id";
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity thisActivity;
    private List<String> datesList;
    private ArrayAdapter<String> spinnerArrayAdapter;

    @BindView(R.id.profile_picture_holder)
    ImageView profilePictureView;

    @BindView(R.id.profile_name)
    TextView profileNameView;

    @BindView(R.id.rating_bar)
    RatingBar ratingBarView;

    @BindView(R.id.rating_value)
    TextView ratingValueView;

    @BindView(R.id.skill_value)
    TextView skillValueView;

    @BindView(R.id.skill_name)
    TextView skillNameView;

    @BindView(R.id.skill_level)
    TextView skillLevelView;

    @BindView(R.id.details_content)
    TextView skillDetailsView;

    @BindView(R.id.date_picker_spinner)
    Spinner availableDatesSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        thisActivity = this;
        setContentView(R.layout.activity_search_item_details);
        ButterKnife.bind(this);
        skillDetailsView.setMovementMethod(new ScrollingMovementMethod());
        setTitle("Details");

        // Initializing an ArrayAdapter
        datesList = new ArrayList<>();
        spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, datesList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        availableDatesSpinner.setAdapter(spinnerArrayAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.setDataFromSkillData(extras.getString(KEY_SKILL_ID));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.back_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_navigation:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDataFromUserData(String uID) {
        Log.v(TAG, "setDataFromUserData");
        DocumentReference mUserRef = mFirestore.collection("User Data")
                .document(uID);
        mUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    UserData ud = documentSnapshot.toObject(UserData.class);
                    profileNameView.setText(ud.getFullName());
                    ratingValueView.setText(String.valueOf(ud.getPersonalRating()));
                    ratingBarView.setRating(ud.getPersonalRating());

                    if(ud.getProfilePictureURL() != null){
                        profilePictureView.setBackground(null);
                    }
                    Glide.with(thisActivity).load(ud.getProfilePictureURL()).apply(new RequestOptions().centerCrop()
                            .circleCrop().placeholder(R.drawable.incognito)).into(profilePictureView);
                } else {
                    Log.e(TAG, "Document does not exist");
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    private void setDataFromSkillData(String sID) {
        Log.v(TAG, "setDataFromSkillData");
        DocumentReference mSkillRef = mFirestore.collection("User Skills")
                .document(sID);
        mSkillRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    UserSkill us = documentSnapshot.toObject(UserSkill.class);
                    skillValueView.setText(String.valueOf(us.getPointsValue()));
                    skillNameView.setText(us.getSkillWithCategory());
                    skillLevelView.setText(String.valueOf(us.getLevel()));
                    skillDetailsView.setText(us.getDetails());
                    setDataFromUserData(us.getUserID());
                    setDataFromUserAvailableDates(us.getUserID());
                } else {
                    Log.e(TAG, "Document does not exist");
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    private void setDataFromUserAvailableDates(String uID) {
        mFirestore.collection("User Data").document(uID).collection("Dates")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                for (QueryDocumentSnapshot doc: snapshots) {
                    if (doc.getBoolean("isAvailable")) {
                        Date date = doc.getDate("timestamp");
                        String dateFormatted = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
                        datesList.add(dateFormatted);
                    }

                }
                /* Single refresh for the adapter! */
                spinnerArrayAdapter.notifyDataSetChanged();
            }
        });
    }

}
