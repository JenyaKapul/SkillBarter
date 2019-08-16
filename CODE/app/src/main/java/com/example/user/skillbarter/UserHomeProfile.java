package com.example.user.skillbarter;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHomeProfile extends ActionBarMenuActivity
        implements EventListener<DocumentSnapshot> {

    private static final String TAG = "UserHomeProfile";

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    private DocumentReference mUserRef;

    private ListenerRegistration mListener;

    @BindView(R.id.profile_picture_holder)
    ImageView profilePictureView;

    @BindView(R.id.profile_name)
    TextView nameView;

    @BindView(R.id.balance)
    TextView balanceView;

    @BindView(R.id.ratingBar)
    RatingBar ratingBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "***** onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_profile);
        ButterKnife.bind(this);

        mUserRef = mFirestore.collection(getString(R.string.collection_user_data))
                .document(mUser.getUid());
    }

    @Override
    public void onStart() {
        super.onStart();
        hideProgressDialog();
        mListener = mUserRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.remove();
            mListener = null;
        }
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
