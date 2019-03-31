package com.example.user.skillbarter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }

        if (documentSnapshot.getReference().equals(mUserRef)) {
            onUserLoaded(documentSnapshot.toObject(UserData.class));
        }
    }


    private void onUserLoaded(UserData userData) {
        String userName, userPoints, profilePictureURL;


        userName = userData.getFirstName() + " " + userData.getLastName();
        userPoints = Integer.toString(userData.getPointsBalance());
        profilePictureURL = userData.getProfilePictureURL();

        nameView.setText(userName);

        balanceView.setText(userPoints);

        Glide.with(profilePictureView.getContext())
                .load(profilePictureURL)
                .into(profilePictureView);

        ratingBarView.setRating(userData.getPersonalRating());



    }
}
