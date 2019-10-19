package com.example.user.skillbarter.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.user.skillbarter.ActionBarMenuActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.RatingDialogFragment;
import com.example.user.skillbarter.adapters.AppointmentAdapter;
import com.example.user.skillbarter.models.Appointment;
import com.example.user.skillbarter.models.Rating;
import com.example.user.skillbarter.models.UserData;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.polyak.iconswitch.IconSwitch;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.skillbarter.Constants.APPOINTMENTS_COLLECTION;
import static com.example.user.skillbarter.Constants.USERS_COLLECTION;

public class HistoryActivity extends ActionBarMenuActivity implements RatingDialogFragment.RatingListener {

    @BindView(R.id.history_appointments_type_switch)
    IconSwitch iconSwitch;

    @BindView(R.id.history_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.view_empty_history)
    ViewGroup emptyView;
    
    private AppointmentAdapter adapter;
    String userType = "clientUID";

    private RatingDialogFragment ratingDialog;

    private DocumentReference appointmentRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        setTitle(R.string.history_title);

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

        ratingDialog = new RatingDialogFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }


    private void initRecyclerView() {
        Query query = mFirestore.collection(APPOINTMENTS_COLLECTION)
                .whereEqualTo(userType, FirebaseAuth.getInstance().getUid());
        query = query.whereEqualTo("providerPaid", true);


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

        adapter.setOnItemClickListener(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                switch (userType) {
                    case "clientUID":
                        appointmentRef = documentSnapshot.getReference();
                        // Show rating dialog
                        ratingDialog.show(getSupportFragmentManager(), RatingDialogFragment.TAG);
                        break;
                    case "providerUID":
                        break;
                }
            }
        });
    }

    @Override
    public void onRating(Rating rating) {
        addRating(appointmentRef, rating)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Hide keyboard and scroll to top
                hideKeyboard();
                recyclerView.smoothScrollToPosition(0);
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add rating",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private Task<Void> addRating(final DocumentReference appointmentRef, final Rating rating) {

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Appointment appointment = transaction.get(appointmentRef).toObject(Appointment.class);

                String providerUID = appointment.getProviderUID();
                DocumentReference userRef = mFirestore.collection(USERS_COLLECTION).document(providerUID);

                updateUserDataRating(userRef, appointmentRef.getId(), appointment.getRating(), rating);

                // Set new rating
                appointment.setRating(rating.getRating());

                // Commit to Firestore
                transaction.set(appointmentRef, appointment);

                return null;
            }
        });
    }

    private Task<Void> updateUserDataRating(final DocumentReference userRef, final String appointmentID,
                                            final float oldAppointmentRating, final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = userRef.collection("Ratings")
                .document(appointmentID);

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {

                UserData userData = transaction.get(userRef)
                        .toObject(UserData.class);

                // Compute new number of ratings
                int newNumRatings = userData.getNumOfRatings();
                float oldRatingTotal = userData.getAvgRating() * userData.getNumOfRatings();

                // In case he appointment was already rated, old rating data needs to be deleted
                oldRatingTotal -= oldAppointmentRating;

                if (oldAppointmentRating == 0) {
                    // First time this appointment is being rated, no need to override old rating data
                    newNumRatings++;
                }

                // Compute new average rating
                float newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;

                // Set new user info
                userData.setNumOfRatings(newNumRatings);
                userData.setAvgRating(newAvgRating);


                // Commit to Firestore
                transaction.set(userRef, userData);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }


    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
