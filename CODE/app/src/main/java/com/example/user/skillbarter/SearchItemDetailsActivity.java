package com.example.user.skillbarter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import javax.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchItemDetailsActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot> {
//    private RecyclerView mRecyclerView;
    private static final String TAG = "SearchItemDetailsAct";
    public static final String KEY_SKILL_ID = "key_skill_id";
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private DocumentReference mUserDataRef, mSkillDataRef;
    private CollectionReference mReviewsRef;
    private String mUserID;
    private ListenerRegistration mListener;

    private ReviewAdapter adapter;


    @BindView(R.id.profile_picture_holder)
    ImageView profilePictureView;

    @BindView(R.id.profile_name)
    TextView profileNameView;

    @BindView(R.id.rating_bar)
    RatingBar ratingBarView;

    @BindView(R.id.ratingBar)
    TextView ratingValueView;

    @BindView(R.id.skill_value)
    TextView skillValueView;

    @BindView(R.id.skill_name)
    TextView skillNameView;

    @BindView(R.id.skill_level)
    TextView skillLevelView;

    @BindView(R.id.details_content)
    TextView skillDetailsView;

    @BindView(R.id.recycler_view)
    RecyclerView reviewsRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item_details);
        ButterKnife.bind(this);
        setTitle("Details");

        mSkillDataRef = mFirestore.collection(getString(R.string.collection_user_skills))
                .document(getIntent().getExtras().getString(KEY_SKILL_ID));
        mReviewsRef = mSkillDataRef.collection(getString(R.string.collection_user_skill_reviews));
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query querry = mReviewsRef.orderBy(getString(R.string.review_dateOfReview_key), Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Review> options = new FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(querry, Review.class).build();
        adapter = new ReviewAdapter(options);
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mListener = mSkillDataRef.addSnapshotListener(this);
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
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "onEvent: ", e);
            return;
        }
        if (documentSnapshot.getReference().equals(mSkillDataRef)) {
            onSkillDataLoaded(documentSnapshot.toObject(UserSkill.class));
            mReviewsRef = documentSnapshot.getReference().collection(getString(R.string.collection_user_skill_reviews));
            this.setUpRecyclerView();
        }
    }

    private void setProfilePictureBackgroundInvisible(){
        ImageView imageView = findViewById(R.id.profile_picture_holder);
        imageView.setBackground(null);
    }

    private Activity getSearchItemDetailsActivity() {
        return this;
    }

    private float calcRating() {
        return 5;
    }

    private void onSkillDataLoaded(UserSkill userSkill){
        //Update all views related to UserData
        mUserDataRef = mFirestore.collection(getString(R.string.collection_user_data))
                .document(userSkill.getUserID());
        mUserDataRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userData = task.getResult();
                    if (userData.exists()) {
                        String firstName, lastName, profilePictureURL;
                        firstName = userData.get(getString(R.string.userData_firstName_key)).toString();
                        lastName = userData.get(getString(R.string.userData_lastName_key)).toString();
                        profileNameView.setText(firstName + " " + lastName);
                        profilePictureURL = userData.get(getString(R.string.userData_profilePictureURL_key)).toString();
                        if(profilePictureURL != null){
                            setProfilePictureBackgroundInvisible();
                        }
                        Glide.with(getSearchItemDetailsActivity()).load(profilePictureURL).apply(new RequestOptions().centerCrop()
                                .circleCrop().placeholder(R.drawable.incognito)).into(profilePictureView);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //Update all views related to Skill
        skillValueView.setText(String.format("%d", userSkill.getPointsValue()));
        skillNameView.setText(userSkill.getSkill());
        skillLevelView.setText(String.format("%d", userSkill.getLevel()));
        skillDetailsView.setText(userSkill.getDetails());

        float avgRating = this.calcRating();
        ratingValueView.setText(String.format("%.1f", avgRating) + "/5");
        ratingBarView.setRating(avgRating);

        //TODO: get all reviews
        //TODO: calc avg rating
    }
}
