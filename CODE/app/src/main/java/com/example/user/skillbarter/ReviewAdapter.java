package com.example.user.skillbarter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;

public class ReviewAdapter extends FirestoreRecyclerAdapter<Review, ReviewAdapter.ReviewHolder> {
    private static final String TAG = "ReviewAdapter";

    public ReviewAdapter(@NonNull FirestoreRecyclerOptions<Review> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewHolder holder, int position, @NonNull Review model) {
        holder.userNameView.setText(model.getReviewerName());
//        holder.userPictureView.setImageBitmap();
        holder.rankValueView.setText(model.getRank());
        holder.dateView.setText(DateFormat.getDateInstance().format(model.getDateOfReview()));
        holder.detailsView.setText(model.getDetails());
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_item,
                viewGroup, false);
        return new ReviewHolder(v);
    }

    class ReviewHolder extends RecyclerView.ViewHolder {
        TextView userNameView, rankValueView, dateView, detailsView;
        ImageView userPictureView;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            userNameView = itemView.findViewById(R.id.reviewer_profile_name);
            rankValueView = itemView.findViewById(R.id.rank);
            dateView = itemView.findViewById(R.id.date);
            detailsView = itemView.findViewById(R.id.review_content);
            userPictureView = itemView.findViewById(R.id.reviewer_profile_picture_holder);

                    }
    }
}

