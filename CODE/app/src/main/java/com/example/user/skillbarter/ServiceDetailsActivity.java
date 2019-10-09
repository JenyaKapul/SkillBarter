package com.example.user.skillbarter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.skillbarter.adapters.ServiceDetailsAdapter;
import com.example.user.skillbarter.models.Appointment;
import com.example.user.skillbarter.models.AvailableDate;
import com.example.user.skillbarter.models.UserData;
import com.example.user.skillbarter.models.UserSkill;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.user.skillbarter.Constants.APPOINTMENTS_COLLECTION;
import static com.example.user.skillbarter.Constants.DATES_COLLECTION;
import static com.example.user.skillbarter.Constants.SKILLS_COLLECTION;
import static com.example.user.skillbarter.Constants.USERS_COLLECTION;


public class ServiceDetailsActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    @BindView(R.id.skill_image_view)
    ImageView userProfileImageView;

    @BindView(R.id.category_and_skill_text_view)
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

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.view_empty_dates)
    ViewGroup emptyView;


    private static final String TAG = "SearchItemDetailsAct";
    public static final String SKILL_ID = "key_skill_id";

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration currentUserListener;
    private DocumentReference currentUserRef;
    UserData currentUser;
    UserSkill userSkill;
    String skillID;

    private ServiceDetailsAdapter adapter;
    private int selectedPosition = -1;
    private String selectedDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);
        ButterKnife.bind(this);

        setTitle(R.string.service_result_title);

        detailsTextView.setMovementMethod(new ScrollingMovementMethod());

        currentUserRef = mFirestore.collection(USERS_COLLECTION).document(FirebaseAuth.getInstance().getUid());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            skillID = extras.getString(SKILL_ID);
            loadServiceDetails();
            String userID = skillID.split("\\.")[0];
            initRecyclerView(userID);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
        if (currentUserRef != null && currentUserListener == null) {
            currentUserListener = currentUserRef.addSnapshotListener(this);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
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


    private void initRecyclerView(String userID) {
        Date today = new Date();
        Query query = FirebaseFirestore.getInstance().collection(USERS_COLLECTION).document(userID)
                .collection(DATES_COLLECTION)
                .whereGreaterThanOrEqualTo("date", today);

        query = query.orderBy("date", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<AvailableDate> options = new FirestoreRecyclerOptions.Builder<AvailableDate>()
                .setQuery(query, AvailableDate.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new ServiceDetailsAdapter(options) {
            @Override
            public void onDataChanged() {
                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnItemClickListener(new ServiceDetailsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                RecyclerView.ViewHolder selectedView = recyclerView.findViewHolderForAdapterPosition(position);

                if (selectedPosition != -1) {
                    RecyclerView.ViewHolder prevSelectedView = recyclerView
                            .findViewHolderForAdapterPosition(selectedPosition);
                    prevSelectedView.itemView.setBackgroundResource(R.color.yellow_card);
                }
                selectedView.itemView.setBackgroundResource(R.color.colorPrimary);
                selectedPosition = position;
                selectedDate = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.timestamp_text_view)).getText().toString();

            }
        });
    }


    private void loadServiceDetails() {
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


    boolean hasEnoughPoints() {
        if (currentUser.getPointsBalance() >= userSkill.getPointsValue()) {
            return true;
        }
        return false;
    }


    @OnClick(R.id.booking_button)
    public void onBookNowClicked() {

        if (selectedPosition == -1) {
            Toast.makeText(this, R.string.date_not_chosen_message, Toast.LENGTH_SHORT).show();
        } else if (currentUser != null && userSkill != null) {

            /* Check that current user has enough points to ask for the service. */
            if (!hasEnoughPoints()) {
                Toast.makeText(this, R.string.missing_points_message, Toast.LENGTH_SHORT).show();
                onBackPressed();

            } else {
                /* Client user has enough points and date is selected. */
                Date date = new Date();
                try {
                    date = new SimpleDateFormat("E dd.MM.yyyy, HH:mm").parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String providerUID = userSkill.getUserID();
                String clientUID = currentUser.getUserID();
                int points = userSkill.getPointsValue();

                Appointment appointment = new Appointment(providerUID, clientUID,
                        skillID, date, 0, false, points);
                mFirestore.collection(APPOINTMENTS_COLLECTION).add(appointment);
                Toast.makeText(this, R.string.appointment_created_message, Toast.LENGTH_SHORT).show();
                decreaseClientPointsForService(userSkill.getPointsValue());
                setSelectedDateBooked(date);
                finish();
            }
        }
    }


    private void setSelectedDateBooked(Date date) {
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
