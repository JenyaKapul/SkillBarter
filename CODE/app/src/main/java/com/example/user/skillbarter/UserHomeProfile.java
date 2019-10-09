package com.example.user.skillbarter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.FirebaseFirestore;
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

import static com.example.user.skillbarter.Constants.DATES_COLLECTION;
import static com.example.user.skillbarter.Constants.USERS_COLLECTION;

public class UserHomeProfile extends ActionBarMenuActivity
        implements EventListener<DocumentSnapshot> {

    @BindView(R.id.user_profile_image_view)
    ImageView profileImageView;

    @BindView(R.id.user_name_text_view)
    TextView nameTextView;

    @BindView(R.id.balance)
    TextView balanceTextView;

    @BindView(R.id.ratingBar)
    RatingBar ratingBarView;

    @BindView(R.id.home_appointments_type_switch)
    IconSwitch iconSwitch;


    private static final String TAG = "UserHomeProfile";

    private DocumentReference currentUserRef;
    private ListenerRegistration currentUserListener;

    private AppointmentAdapter adapter;
    private boolean currIsProvider = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_profile);
        ButterKnife.bind(this);

        setTitle(R.string.user_home_profile_title);

        currentUserRef = usersCollection.document(currentUser.getUid());

        initRecyclerView();

//        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
//            @Override
//            public void onCheckChanged(IconSwitch.Checked current) {
//                //TODO: remove for production
//                completeTransactions();
//                adapter.stopListening();
//                if(current == IconSwitch.Checked.LEFT){
//                    currIsProvider = false;
//                }
//                else {
//                    currIsProvider = true;
//                }
//                setUpRecyclerView();
//                adapter.startListening();
//            }
//        });
//        completeTransactions();
    }

    //        Query query;
//        if (this.currIsProvider) {
//            query = appointmentsCollection.whereEqualTo("providerUID", this.currentUser.getUid());
//        }
//        else {
//            query = appointmentsCollection.whereEqualTo("clientUID", this.currentUser.getUid());
//        }
//        query = query.whereEqualTo("isProviderPaid", false);
//        query = query.orderBy("date", Query.Direction.ASCENDING);
//
//        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
//                .setQuery(query, Appointment.class).build();
//


//        adapter = new AppointmentAdapter(options);
//        RecyclerView recyclerView = findViewById(R.id.home_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
//
//        adapter.setOnItemClickListener(new AppointmentAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
//                Log.v(TAG, "onItemClick: setOnItemClickListener");
//            }
//        });

    private void initRecyclerView() {
        Query query = appointmentsCollection.whereEqualTo("providerUID", this.currentUser.getUid());
        query = query.whereEqualTo("providerPaid", false);
//        query = query.orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class).build();

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

    @Override
    public void onStart() {
        super.onStart();
        hideProgressDialog();
        currentUserListener = currentUserRef.addSnapshotListener(this);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (currentUserListener != null) {
            currentUserListener.remove();
            currentUserListener = null;
        }
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }

        if (documentSnapshot.getReference().equals(currentUserRef)) {
            onUserLoaded(documentSnapshot.toObject(UserData.class));
        }
    }


    private void setProfilePictureBackgroundInvisible(){
        ImageView imageView = findViewById(R.id.user_profile_image_view);
        imageView.setBackground(null);
    }


    private void onUserLoaded(UserData userData) {
        String userName, userPoints, profilePictureURL;

        userName = userData.getFirstName() + " " + userData.getLastName();
        userPoints = Integer.toString(userData.getPointsBalance());
        profilePictureURL = userData.getProfilePictureURL();
        if(profilePictureURL != null){
            setProfilePictureBackgroundInvisible();
        }

        Glide.with(this).load(profilePictureURL).apply(new RequestOptions().centerCrop()
                .circleCrop().placeholder(R.drawable.incognito)).into(profileImageView);

        nameTextView.setText(userName);

        balanceTextView.setText(userPoints);

        ratingBarView.setRating(userData.getPersonalRating());
    }

    private void completeTransactions() {
        Timestamp nowDate = new Timestamp(new Date());
        appointmentsCollection
//                .whereEqualTo("providerUID", this.currentUser.getUid())
//                .whereEqualTo("isProviderPaid", false)
//                .whereLessThan("date", nowDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                        mFirestore.runTransaction(new Transaction.Function<Void>() {
                            @android.support.annotation.Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                long sumToTransfer = 0;
                                //all the reads of the transaction
                                DocumentReference currUserRef = usersCollection.document(currentUser.getUid());
                                long currentUserPoints = transaction.get(currUserRef).getLong("pointsBalance");
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String skillID = documentSnapshot.getString("skillID");
                                    DocumentSnapshot skillSnapshot = transaction.get(skillsCollection.document(skillID));
                                    sumToTransfer += skillSnapshot.getLong("pointsValue");
                                }
                                //all the writes of the transaction
                                transaction.update(currUserRef, "pointsBalance", currentUserPoints + sumToTransfer);
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String appointmentID = documentSnapshot.getId();
                                    transaction.update(appointmentsCollection.document(appointmentID), "isProviderPaid", true);
                                }
                                return null;
                            }
                        });
                    }
                });
    }
}
