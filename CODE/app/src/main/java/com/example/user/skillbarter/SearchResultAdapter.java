package com.example.user.skillbarter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchResultAdapter extends FirestoreRecyclerAdapter<UserSkill, SearchResultAdapter.SearchResultHolder> {
    private static final String TAG = "SearchResultAdapter";

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private OnItemClickListener listener;

    public SearchResultAdapter(@NonNull FirestoreRecyclerOptions<UserSkill> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchResultHolder holder, int position, @NonNull UserSkill model) {
        Log.v(TAG, "onBindViewHolder: SearchResultHolder");
        String category = model.getCategory();
        String skill = model.getSkill();
        holder.tvSkill.setText(skill);
        holder.tvCategory.setText("(" + model.getCategory() + ")");
        holder.tvValue.setText(String.valueOf(model.getPointsValue()));
        holder.tvLevel.setText(String.valueOf(model.getLevel()));
        holder.ivProviderPicture.setImageResource(getSkillImageID(category, skill));
        this.setDataFromUserData(model.getUserID(), holder);
    }

    private void setDataFromUserData(String uID, @NonNull final SearchResultHolder holder) {
        Log.v(TAG, "onBindViewHolder: setDataFromUserData");
        DocumentReference mUserRef = mFirestore.collection("User Data")
                .document(uID);
        mUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    UserData ud = documentSnapshot.toObject(UserData.class);
                    holder.tvProviderName.setText(ud.getFullName());
                    holder.tvRating.setText(String.valueOf(ud.getPersonalRating()));
                    holder.rbRating.setRating(ud.getPersonalRating());
                } else {
                    Log.e(TAG, "Document does not exist");
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.toString());
            }
        });
    }

    private int getSkillImageID(String category, String skill) {
        switch (category) {
            case "Tutoring":
                return R.drawable.skill_icon_tutoring;
            case "Music":
                return R.drawable.skill_icon_music;
            case "Dance":
                return R.drawable.skill_icon_dance;
            case "Arts and Crafts":
                return R.drawable.skill_icon_arts;
            case "Sport":
                return R.drawable.skill_icon_sport;
            case "Household Services":
                if (skill.equals("Handyman")) {
                    return R.drawable.skill_icon_handyman;
                } else {
                    return R.drawable.skill_icon_household_services;
                }
            case "Beauty Care":
                return R.drawable.skill_icon_beauty_care;
            case "Culinary":
                return R.drawable.skill_icon_culinary;
            default:
                return 0;
        }
    }

    @NonNull
    @Override
    public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result_item,
                viewGroup, false);
        return new SearchResultHolder(v);
    }

    class SearchResultHolder extends RecyclerView.ViewHolder {
        TextView tvProviderName, tvRating, tvSkill, tvCategory, tvValue, tvLevel;
        ImageView  ivProviderPicture;
        RatingBar rbRating;

        public SearchResultHolder(@NonNull View itemView) {
            super(itemView);
            tvProviderName = itemView.findViewById(R.id.other_profile_name);
            ivProviderPicture = itemView.findViewById(R.id.provider_picture_holder);
            tvRating = itemView.findViewById(R.id.rating_value);
            tvSkill = itemView.findViewById(R.id.skill_text_view);
            tvCategory = itemView.findViewById(R.id.category_text_view);
            tvValue = itemView.findViewById(R.id.value_text_view);
            rbRating = itemView.findViewById(R.id.ratingBar);
            tvLevel = itemView.findViewById(R.id.level_value_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
