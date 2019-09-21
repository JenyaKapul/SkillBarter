package com.example.user.skillbarter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.skillbarter.models.Appointment;
import com.example.user.skillbarter.models.UserData;
import com.example.user.skillbarter.models.UserSkill;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchItemDetailsActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {
    private static final String TAG = "SearchItemDetailsAct";
    public static final String KEY_SKILL_ID = "key_skill_id";
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity thisActivity;
    private List<String> datesList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private String spinnerDateSelection, spinnerDatesTitle = "Choose date and starting time";
    private ListenerRegistration currentUserListener;
    private DocumentReference currentUserRef;
    UserData currentUser;
    ProgressDialog mProgressDialog;
    UserSkill providingServiceUserSkill;

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
        showProgressDialog();
        ButterKnife.bind(this);
        skillDetailsView.setMovementMethod(new ScrollingMovementMethod());
        setTitle("Details");
        spinnerDateSelection = spinnerDatesTitle; // initialization
        currentUserRef = mFirestore.collection("User Data").document(FirebaseAuth.getInstance().getUid());

        // Initializing an ArrayAdapter
        datesList = new ArrayList<>();
        datesList.add(spinnerDatesTitle);
        spinnerArrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, datesList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        availableDatesSpinner.setAdapter(spinnerArrayAdapter);

        availableDatesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerDateSelection = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.setDataFromSkillData(extras.getString(KEY_SKILL_ID));
        }
//        showProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUserListener = currentUserRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (currentUserListener != null) {
            currentUserListener.remove();
            currentUserListener = null;
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
                    providingServiceUserSkill = documentSnapshot.toObject(UserSkill.class);
                    skillValueView.setText(String.valueOf(providingServiceUserSkill.getPointsValue()));
                    skillNameView.setText(providingServiceUserSkill.getSkillWithCategory());
                    skillLevelView.setText(String.valueOf(providingServiceUserSkill.getLevel()));
                    skillDetailsView.setText(providingServiceUserSkill.getDetails());
                    setDataFromUserData(providingServiceUserSkill.getUserID());
                    setDataFromUserAvailableDates(providingServiceUserSkill.getUserID());
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
                        if (isFutureDate(date)) {
                            String dateFormatted = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
                            datesList.add(dateFormatted);
                        }
                    }

                }
                /* Single refresh for the adapter! */
                spinnerArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    boolean isFutureDate(Date eventDate) {
        Date today = new Date();
        return today.before(eventDate);
    }

    boolean hasEnoughPoints() {
        if (currentUser.getPointsBalance() >= providingServiceUserSkill.getPointsValue()) {
            return true;
        }
        return false;
    }

    @OnClick(R.id.booking_button)
    public void onBookNowClicked() {
        Log.d(TAG, "****** onBookNowClicked: Booking button clicked");
        if (spinnerDateSelection.equals(spinnerDatesTitle)) {
            Toast.makeText(this, "You must choose date and starting time", Toast.LENGTH_SHORT).show();
        } else if (currentUser != null && providingServiceUserSkill != null) {
            /* Check that current user has enough points to ask for the service. */
            if (!hasEnoughPoints()) {
                Toast.makeText(this, "You don't have enough points", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                /* Client user has enough points and date is selected */
                Date date = new Date();
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(spinnerDateSelection);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String providerUID = providingServiceUserSkill.getUserID();
                String clientUID = currentUser.getUserID();
                Appointment appointment = new Appointment(providerUID, clientUID,
                        providingServiceUserSkill.getSkillId(), date, 0, false);
                mFirestore.collection("Appointments").add(appointment);
                Toast.makeText(this, "New appointment is set!", Toast.LENGTH_SHORT).show();
                decreaseClientPointsForService(providingServiceUserSkill.getPointsValue());
                setSelectedDateToUnavailable(date);
                finish();
            }
        }
    }

    private void setSelectedDateToUnavailable(Date date) {
        String uID = providingServiceUserSkill.getUserID();
        String dateDocID = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date);
        mFirestore.collection("User Data")
                .document(uID).collection("Dates").document(dateDocID)
                .update("isAvailable", false);
    }

    private void decreaseClientPointsForService(int pointsValue) {
        int points = currentUser.getPointsBalance() - pointsValue;
        currentUserRef.update("pointsBalance", points);
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (documentSnapshot.getReference().equals(currentUserRef)) {
            currentUser = documentSnapshot.toObject(UserData.class);
            hideProgressDialog();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
