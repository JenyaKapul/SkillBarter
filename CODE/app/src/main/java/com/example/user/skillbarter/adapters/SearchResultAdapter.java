package com.example.user.skillbarter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.user.skillbarter.R;
import com.example.user.skillbarter.models.UserData;
import com.example.user.skillbarter.models.UserSkill;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.user.skillbarter.BaseActivity.getSkillImageID;
import static com.example.user.skillbarter.Constants.USERS_COLLECTION;

public class SearchResultAdapter extends FirestoreRecyclerAdapter<UserSkill, SearchResultAdapter.SearchResultHolder> {

    private OnItemClickListener clickListener;

    //TODO: remove hideMySkills if it is not in use
    /* Set to true if the adapter does not display current user's skills. */
    private boolean hideMySkills;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SearchResultAdapter(@NonNull FirestoreRecyclerOptions<UserSkill> options, boolean hideMySkills) {
        super(options);
        this.hideMySkills = hideMySkills;
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchResultHolder holder, int position, @NonNull UserSkill userSkill) {

        String category = userSkill.getCategory();
        String skill = userSkill.getSkill();

        holder.textViewCategory.setText("(" + category + ")");
        holder.textViewSkill.setText(skill);
        holder.textViewPointsValue.setText(String.valueOf(userSkill.getPointsValue()));
        holder.textViewLevel.setText("Level " + String.valueOf(userSkill.getLevel()));
        holder.imageViewProvider.setImageResource(getSkillImageID(category, skill));

        String providerUserID = userSkill.getUserID();
        loadProviderUserData(providerUserID, holder);
    }


    @NonNull
    @Override
    public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item,
                parent, false);
        return new SearchResultHolder(v);
    }

    class SearchResultHolder extends RecyclerView.ViewHolder {

        TextView textViewProviderName, textViewRating, textViewCategory, textViewSkill,
                textViewPointsValue, textViewLevel;
        ImageView imageViewProvider;
        RatingBar ratingBar;


        public SearchResultHolder(View itemView) {
            super(itemView);
            textViewProviderName = itemView.findViewById(R.id.search_result_user_name_text_view);
            textViewRating = itemView.findViewById(R.id.search_result_rating_text_view);
            textViewCategory = itemView.findViewById(R.id.search_result_category_text_view);
            textViewSkill = itemView.findViewById(R.id.search_result_skill_text_view);
            textViewPointsValue = itemView.findViewById(R.id.search_result_points_text_view);
            textViewLevel = itemView.findViewById(R.id.search_result_level_text_view);
            imageViewProvider = itemView.findViewById(R.id.search_result_image_view);
            ratingBar = itemView.findViewById(R.id.search_result_rating_bar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);

                        /* Prevent current user's skills being clickable. */
                        String currentID = FirebaseAuth.getInstance().getUid();
                        if (!snapshot.getString("userID").equals(currentID)) {
                            clickListener.onItemClick(snapshot, position);
                        }
                    }
                }
            });
        }
    }


    private void loadProviderUserData(String uID, @NonNull final SearchResultHolder holder) {
        DocumentReference userDocRef = db.collection(USERS_COLLECTION).document(uID);

        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    UserData user = documentSnapshot.toObject(UserData.class);
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    holder.textViewProviderName.setText(fullName);
                    holder.textViewRating.setText(String.format("%.1f", user.getPersonalRating()));
                    holder.ratingBar.setRating(user.getPersonalRating());
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
}
