package com.example.user.skillbarter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.polyak.iconswitch.IconSwitch;

import java.util.Date;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHomeProfile extends ActionBarMenuActivity
        implements EventListener<DocumentSnapshot> {
    private static final String TAG = "UserHomeProfile";

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference appointmentRef = mFirestore.collection("Appointments");
    private DocumentReference mUserRef;

    private AppointmentAdapter adapter;
    private ListenerRegistration mListener;
    private boolean currIsProvider = false;

    @BindView(R.id.profile_picture_holder)
    ImageView profilePictureView;

    @BindView(R.id.profile_name)
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

        mUserRef = mFirestore.collection(getString(R.string.collection_user_data))
                .document(mUser.getUid());

        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
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
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Log.v(TAG, "setUpRecyclerView: setUpRecyclerView");
        Timestamp nowDate = new Timestamp(new Date());
        Query query;
        if (this.currIsProvider) {
            query = appointmentRef.whereEqualTo("providerUID", this.mUser.getUid());
        }
        else {
            query = appointmentRef.whereEqualTo("clientUID", this.mUser.getUid());
        }
        query = query.whereGreaterThanOrEqualTo("date", nowDate).orderBy("date", Query.Direction.ASCENDING);

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
        ImageView imageView = findViewById(R.id.profile_picture_holder);
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
}
