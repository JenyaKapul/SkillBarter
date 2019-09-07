package com.example.user.skillbarter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchResultAdapter extends FirestoreRecyclerAdapter<SearchResult, SearchResultAdapter.SearchResultHolder> {

    public SearchResultAdapter(@NonNull FirestoreRecyclerOptions<SearchResult> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchResultHolder holder, int position, @NonNull SearchResult model) {
        holder.tvProviderName.setText(model.getProviderName());
        holder.tvSkill.setText(model.getSkillName());
        holder.tvCategory.setText(model.getCategoryName());
        holder.tvValue.setText(model.getValue());
        holder.tvRanking.setText(String.valueOf(model.getRanking()));
        holder.ivProviderPicture.setBackground(null); //TODO - set actual picture
        holder.rbRating.setRating(model.getRanking());
    }

    @NonNull
    @Override
    public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result_item,
                viewGroup, false);
        return new SearchResultHolder(v);
    }

    class SearchResultHolder extends RecyclerView.ViewHolder {
        TextView tvProviderName, tvRanking, tvSkill, tvCategory, tvValue;
        ImageView  ivProviderPicture;
        RatingBar rbRating;

        public SearchResultHolder(@NonNull View itemView) {
            super(itemView);
            tvProviderName = itemView.findViewById(R.id.provider_profile_name);
            ivProviderPicture = itemView.findViewById(R.id.provider_picture_holder);
            tvRanking = itemView.findViewById(R.id.rating_value);
            tvSkill = itemView.findViewById(R.id.skill_text_view);
            tvCategory = itemView.findViewById(R.id.category_text_view);
            tvValue = itemView.findViewById(R.id.value_text_view);
            rbRating = itemView.findViewById(R.id.ratingBar);
        }
    }
}
