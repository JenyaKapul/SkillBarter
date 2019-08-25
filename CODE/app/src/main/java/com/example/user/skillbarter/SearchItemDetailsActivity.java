package com.example.user.skillbarter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchItemDetailsActivity extends AppCompatActivity {
//    private RecyclerView mRecyclerView;
    private static final String TAG = "SearchItemDetailsAct";
    public static final String KEY_SKILL_ID = "key_skill_id";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference mUserDataRef, mSkillDataRef;
    private CollectionReference mReviewsRef;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item_details);
        ButterKnife.bind(this);

        setTitle("Details");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
//        Query querry =
    }
}
