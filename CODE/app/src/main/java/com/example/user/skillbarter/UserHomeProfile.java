package com.example.user.skillbarter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class UserHomeProfile extends ActionBarMenuActivity
        implements EventListener<DocumentSnapshot> {
    private static final String TAG = "UserHomeProfile";

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private DocumentReference mUserRef;

    private AppointmentAdapter adapter;
    private ListenerRegistration mListener;
    private boolean currIsProvider = false;

    @BindView(R.id.user_profile_image_view)
    ImageView profilePictureView;

    @BindView(R.id.user_name_text_view)
    TextView nameView;

    @BindView(R.id.balance)
    TextView balanceView;

    @BindView(R.id.ratingBar)
    RatingBar ratingBarView;

    @BindView(R.id.home_appointments_type_switch)
    IconSwitch iconSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "***** onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_profile);
        ButterKnife.bind(this);
        mUserRef = usersCollection.document(mUser.getUid());

        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                //TODO: remove for production
                completeTransactions();
                adapter.stopListening();
                if(current == IconSwitch.Checked.LEFT){
                    currIsProvider = false;
                }
                else {
                    currIsProvider = true;
                }
                setUpRecyclerView();
                adapter.startListening();
            }
        });
        completeTransactions();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query;
        if (this.currIsProvider) {
            query = appointmentsCollection.whereEqualTo("providerUID", this.mUser.getUid());
        }
        else {
            query = appointmentsCollection.whereEqualTo("clientUID", this.mUser.getUid());
        }
        query = query.whereEqualTo("isProviderPaid", false);
        query = query.orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class).build();

        adapter = new AppointmentAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Log.v(TAG, "onItemClick: setOnItemClickListener");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        hideProgressDialog();
        mListener = mUserRef.addSnapshotListener(this);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.remove();
            mListener = null;
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

        if (documentSnapshot.getReference().equals(mUserRef)) {
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
                .circleCrop().placeholder(R.drawable.incognito)).into(profilePictureView);

        nameView.setText(userName);

        balanceView.setText(userPoints);

        ratingBarView.setRating(userData.getPersonalRating());
    }

    private void completeTransactions() {
        Timestamp nowDate = new Timestamp(new Date());
        appointmentsCollection
//                .whereEqualTo("providerUID", this.mUser.getUid())
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
                                DocumentReference currUserRef = usersCollection.document(mUser.getUid());
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
