package com.example.user.skillbarter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.skillbarter.adapters.AppointmentAdapter;
import com.example.user.skillbarter.models.Appointment;
import com.example.user.skillbarter.models.UserData;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.polyak.iconswitch.IconSwitch;

import java.util.Date;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.skillbarter.Constants.APPOINTMENTS_COLLECTION;
import static com.example.user.skillbarter.Constants.USERS_COLLECTION;

public class UserHomeProfile extends ActionBarMenuActivity implements EventListener<DocumentSnapshot> {

    @BindView(R.id.home_profile_image_view)
    ImageView profileImageView;

    @BindView(R.id.home_profile_name_text_view)
    TextView nameTextView;

    @BindView(R.id.home_profile_points_text_view)
    TextView pointsTextView;

    @BindView(R.id.home_profile_rating_bar)
    RatingBar ratingBarView;

    @BindView(R.id.appointments_type_switch)
    IconSwitch iconSwitch;

    @BindView(R.id.home_profile_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.view_empty_appointments_left)
    ViewGroup emptyViewLeft;

    @BindView(R.id.view_empty_appointments_right)
    ViewGroup emptyViewRight;

    private AppointmentAdapter adapter;
    private ListenerRegistration currentUserListener;
    private DocumentReference currentUserRef;
    String userType = "clientUID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_profile);
        ButterKnife.bind(this);

        setTitle(R.string.user_home_profile_title);

        currentUserRef = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(FirebaseAuth.getInstance().getUid());

        updateAppointmentsHistory();
        initRecyclerView();

        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                switch (current) {
                    case LEFT:
                        userType = "clientUID";
                        break;
                    case RIGHT:
                        userType = "providerUID";
                        break;
                }
                initRecyclerView();
            }
        });
    }


    private void updateAppointmentsHistory() {
        Date today = new Date();

        FirebaseFirestore.getInstance().collection(APPOINTMENTS_COLLECTION).whereLessThanOrEqualTo("date", today)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                for (QueryDocumentSnapshot doc: snapshots) {
                    boolean isPaid = doc.getBoolean("providerPaid");
                    if (!isPaid) {
                        doc.getReference().update("providerPaid", true);
                        long points = doc.getLong("points");
                        int pointsInt = (int) points;

                        String providerUID = (String) doc.get("providerUID");
                        transferProviderUserPoints(providerUID, pointsInt);
                    }
                }
                /* Single refresh for the adapter! */
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void transferProviderUserPoints(String providerUID, final int points) {

        DocumentReference userDocRef = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(providerUID);

        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    long balance = documentSnapshot.getLong("pointsBalance");
                    int newBalance = (int) balance + points;
                    documentSnapshot.getReference().update("pointsBalance", newBalance);
                }
            }
        });
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


    private void initRecyclerView() {
        Query query = FirebaseFirestore.getInstance().collection(APPOINTMENTS_COLLECTION)
                .whereEqualTo(userType, FirebaseAuth.getInstance().getUid());
        query = query.whereEqualTo("providerPaid", false);


        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new AppointmentAdapter(options) {
            @Override
            public void onDataChanged() {
                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    setEmptyView();
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyViewLeft.setVisibility(View.GONE);
                    emptyViewRight.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        // Optional for adding clicks functionality on appointment items
        adapter.setOnItemClickListener(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
            }
        });
    }

    private void setEmptyView() {
        if (userType.equals("clientUID")) {
            emptyViewLeft.setVisibility(View.VISIBLE);
            emptyViewRight.setVisibility(View.GONE);
        } else {
            emptyViewLeft.setVisibility(View.GONE);
            emptyViewRight.setVisibility(View.VISIBLE);
        }
    }


    private void onUserLoaded(UserData userData) {

        String name = userData.getFirstName() + " " + userData.getLastName();
        String points = Integer.toString(userData.getPointsBalance());
        Float rating = userData.getAvgRating();

        String profilePictureURL = userData.getProfilePictureURL();
        if(profilePictureURL != null) {
            Glide.with(this).load(profilePictureURL).apply(new RequestOptions()
                    .centerCrop().circleCrop()).into(profileImageView);
        } else {
            profileImageView.setBackground(null);
        }

        nameTextView.setText(name);
        pointsTextView.setText(points);
        ratingBarView.setRating(rating);
    }


    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (documentSnapshot.getReference().equals(currentUserRef)) {
            UserData userData = documentSnapshot.toObject(UserData.class);

            onUserLoaded(userData);
        }
    }
}
