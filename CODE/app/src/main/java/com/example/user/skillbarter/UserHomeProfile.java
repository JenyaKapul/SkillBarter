package com.example.user.skillbarter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.skillbarter.adapters.AppointmentAdapter;
import com.example.user.skillbarter.adapters.ServiceDetailsAdapter;
import com.example.user.skillbarter.models.Appointment;
import com.example.user.skillbarter.models.AvailableDate;
import com.example.user.skillbarter.models.UserData;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.polyak.iconswitch.IconSwitch;

import java.util.Date;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHomeProfile extends ActionBarMenuActivity implements EventListener<DocumentSnapshot> {

    @BindView(R.id.profile_image_view)
    ImageView profileImageView;

    @BindView(R.id.user_name_text_view)
    TextView nameTextView;

    @BindView(R.id.points_text_view)
    TextView pointsTextView;

    @BindView(R.id.stars_rating_bar)
    RatingBar ratingBarView;

    @BindView(R.id.appointments_type_switch)
    IconSwitch iconSwitch;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.view_empty_appointments)
    ViewGroup emptyView;

    private AppointmentAdapter adapter;
    private  Query query;
    private ListenerRegistration currentUserListener;
    private DocumentReference currentUserRef;
    private boolean isClient = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_profile);
        ButterKnife.bind(this);

        setTitle(R.string.user_home_profile_title);

        currentUserRef = usersCollection.document(currentUser.getUid());

        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                switch (current) {
                    case LEFT:
                        isClient = true;
                        break;
                    case RIGHT:
                        isClient = false;
                        break;
                }
                initRecyclerView();

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
        if (isClient) {
            query = appointmentsCollection.whereEqualTo("clientUID", currentUser.getUid());
        } else {
            query = appointmentsCollection.whereEqualTo("providerUID", currentUser.getUid());
        }

        //TODO: decide if the appointments are fetched by date (greater than or equal to today. see ref in ServiceDetailsActivity)
        // or by providerPaid field
//        query = query.whereEqualTo("providerPaid", false);
        query = query.orderBy("date", Query.Direction.ASCENDING);

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

        //TODO: remove this if no functionality is available for clicking an appointment item
        adapter.setOnItemClickListener(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
            }
        });
    }


    private void onUserLoaded(UserData userData) {

        String name = userData.getFirstName() + " " + userData.getLastName();
        String points = Integer.toString(userData.getPointsBalance());
        Float rating = userData.getPersonalRating();

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



//
//
//
//    private void completeTransactions() {
//        Timestamp nowDate = new Timestamp(new Date());
//        appointmentsCollection
////                .whereEqualTo("providerUID", this.currentUser.getUid())
////                .whereEqualTo("providerPaid", false)
////                .whereLessThan("date", nowDate)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
//                        mFirestore.runTransaction(new Transaction.Function<Void>() {
//                            @android.support.annotation.Nullable
//                            @Override
//                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
//                                long sumToTransfer = 0;
//                                //all the reads of the transaction
//                                DocumentReference currUserRef = usersCollection.document(currentUser.getUid());
//                                long currentUserPoints = transaction.get(currUserRef).getLong("pointsBalance");
//                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                    String skillID = documentSnapshot.getString("skillID");
//                                    DocumentSnapshot skillSnapshot = transaction.get(skillsCollection.document(skillID));
//                                    sumToTransfer += skillSnapshot.getLong("pointsValue");
//                                }
//                                //all the writes of the transaction
//                                transaction.update(currUserRef, "pointsBalance", currentUserPoints + sumToTransfer);
//                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                    String appointmentID = documentSnapshot.getId();
//                                    transaction.update(appointmentsCollection.document(appointmentID), "providerPaid", true);
//                                }
//                                return null;
//                            }
//                        });
//                    }
//                });
//    }
}
