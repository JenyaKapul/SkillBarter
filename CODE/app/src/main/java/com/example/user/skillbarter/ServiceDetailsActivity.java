package com.example.user.skillbarter;

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

import static com.example.user.skillbarter.Constants.APPOINTMENTS_COLLECTION;
import static com.example.user.skillbarter.Constants.DATES_COLLECTION;
import static com.example.user.skillbarter.Constants.SKILLS_COLLECTION;
import static com.example.user.skillbarter.Constants.USERS_COLLECTION;

/*
 * TODO:
 *  (1) Change dates spinner to adapter that listens to the Dates collection and prevent race condition by transferring syncing control to Firebase
 * (2) Add empty state for adapter indicating no dates are available
 *
 */
public class ServiceDetailsActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    @BindView(R.id.user_profile_image_view)
    ImageView userProfileImageView;

    @BindView(R.id.user_name_text_view)
    TextView userNameTextView;

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.rating_value_text_view)
    TextView ratingValueTextView;

    @BindView(R.id.points_text_view)
    TextView pointsTextView;

    @BindView(R.id.skill_text_view)
    TextView skillTextView;

    @BindView(R.id.level_text_view)
    TextView levelTextView;

    @BindView(R.id.details_content_text_view)
    TextView detailsTextView;

    @BindView(R.id.date_picker_spinner)
    Spinner datePickerSpinner;

    private static final String TAG = "SearchItemDetailsAct";
    public static final String SKILL_ID = "key_skill_id";

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private List<String> datesList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private String spinnerDateSelection;
    private ListenerRegistration currentUserListener;
    private DocumentReference currentUserRef;
    UserData currentUser;
    UserSkill userSkill;
    String spinnerDateFormatting = "E, dd-MM-yy, HH:mm";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);
        ButterKnife.bind(this);

        setTitle(R.string.service_result_title);

        detailsTextView.setMovementMethod(new ScrollingMovementMethod()); //TODO (NOA): check long text

        spinnerDateSelection = getString(R.string.dates_spinner_prompt); // initialization
        currentUserRef = mFirestore.collection(USERS_COLLECTION).document(FirebaseAuth.getInstance().getUid());

        // Initializing an ArrayAdapter
        datesList = new ArrayList<>();
        datesList.add(getString(R.string.dates_spinner_prompt));
        spinnerArrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, datesList);
        datePickerSpinner.setAdapter(spinnerArrayAdapter);

        datePickerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            loadServiceDetails(extras.getString(SKILL_ID));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (currentUserRef != null && currentUserListener == null) {
            currentUserListener = currentUserRef.addSnapshotListener(this);
        }
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void loadServiceDetails(String skillID) {
        mFirestore.collection(SKILLS_COLLECTION).document(skillID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userSkill = documentSnapshot.toObject(UserSkill.class);

                    pointsTextView.setText(String.valueOf(userSkill.getPointsValue()));

                    String skillWithCategory = userSkill.getSkill() + " (" + userSkill.getCategory()
                            + ")";
                    skillTextView.setText(skillWithCategory);

                    levelTextView.setText(String.valueOf(userSkill.getLevel()));
                    detailsTextView.setText(userSkill.getDetails());

                    loadProviderUserData(userSkill.getUserID());
                    loadAvailableDates(userSkill.getUserID());
                } else {
                    Log.e(TAG, "Document does not exist");
                }
            }
        });
    }


    private void loadProviderUserData(String uID) {
        mFirestore.collection(USERS_COLLECTION).document(uID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    UserData user = documentSnapshot.toObject(UserData.class);

                    String fullName = user.getFirstName() + " " + user.getLastName();
                    userNameTextView.setText(fullName);

                    ratingValueTextView.setText(String.format("%.1f", user.getPersonalRating()));
                    ratingBar.setRating(user.getPersonalRating());

                    if(user.getProfilePictureURL() != null) {
                        Glide.with(userProfileImageView.getContext()).load(user.getProfilePictureURL())
                                .apply(new RequestOptions().centerCrop()
                                        .circleCrop()).into(userProfileImageView);
                    }
                } else {
                    Log.e(TAG, "Document does not exist");
                }
            }
        });
    }


    private void loadAvailableDates(String uID) {
        mFirestore.collection(USERS_COLLECTION).document(uID).collection(DATES_COLLECTION)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                for (QueryDocumentSnapshot availableDate: snapshots) {
                    boolean isBooked = availableDate.getBoolean("booked");
                    if (!isBooked) {
                        Date date = availableDate.getDate("date");
                        if (isFutureDate(date)) {
                            String dateFormatted = new SimpleDateFormat(spinnerDateFormatting).format(date);
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
        if (currentUser.getPointsBalance() >= userSkill.getPointsValue()) {
            return true;
        }
        return false;
    }


    @OnClick(R.id.booking_button)
    public void onBookNowClicked() {

        if (spinnerDateSelection.equals(getString(R.string.dates_spinner_prompt))) {
            Toast.makeText(this, R.string.date_not_chosen_message, Toast.LENGTH_SHORT).show();
        }
        else if (currentUser != null && userSkill != null) {

            /* Check that current user has enough points to ask for the service. */
            if (!hasEnoughPoints()) {
                Toast.makeText(this, R.string.missing_points_message, Toast.LENGTH_SHORT).show();
                onBackPressed();

            } else {
                /* Client user has enough points and date is selected. */
                Date date = new Date();
                try {
                    date = new SimpleDateFormat(spinnerDateFormatting).parse(spinnerDateSelection);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String providerUID = userSkill.getUserID();
                String clientUID = currentUser.getUserID();
                Appointment appointment = new Appointment(providerUID, clientUID,
                        userSkill.getSkillId(), date, 0, false);
                mFirestore.collection(APPOINTMENTS_COLLECTION).add(appointment);
                Toast.makeText(this, R.string.appointment_created_message, Toast.LENGTH_SHORT).show();
                decreaseClientPointsForService(userSkill.getPointsValue());
                setSelectedDateToBooked(date);
                finish();
            }
        }
    }


    private void setSelectedDateToBooked(Date date) {
        String uID = userSkill.getUserID();
        String dateDocID = new SimpleDateFormat("dd.MM.yy HH:mm").format(date);
        mFirestore.collection(USERS_COLLECTION)
                .document(uID).collection(DATES_COLLECTION).document(dateDocID)
                .update("booked", true);
    }


    private void decreaseClientPointsForService(int pointsValue) {
        int points = currentUser.getPointsBalance() - pointsValue;
        currentUserRef.update("pointsBalance", points);
    }
    

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (documentSnapshot.getReference().equals(currentUserRef)) {
            currentUser = documentSnapshot.toObject(UserData.class);
        }
    }
}
