package com.example.user.skillbarter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.skillbarter.R;
import com.example.user.skillbarter.models.UserSkill;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;


public class EditableSkillAdapter extends FirestoreRecyclerAdapter<UserSkill, EditableSkillAdapter.SkillHolder> {

    private OnItemClickListener listener;

    public EditableSkillAdapter(@NonNull FirestoreRecyclerOptions<UserSkill> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SkillHolder holder, int position, @NonNull UserSkill model) {
        String category = model.getCategory();
        String skill = model.getSkill();
        holder.categoryTextView.setText(category);
        holder.skillTextView.setText(skill);
        holder.levelTextView.setText("Level " + String.valueOf(model.getLevel()));
        holder.pointsTextView.setText(String.valueOf(model.getPointsValue()));
        holder.iconImageView.setImageResource(getSkillImageID(category, skill));
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
    public SkillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.editable_skill_item,
                parent, false);
        return new SkillHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class SkillHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        TextView skillTextView;
        TextView levelTextView;
        TextView pointsTextView;
        ImageView editImageView;
        ImageView iconImageView;

        public SkillHolder(View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryView);
            skillTextView = itemView.findViewById(R.id.skillView);
            levelTextView = itemView.findViewById(R.id.levelView);
            pointsTextView = itemView.findViewById(R.id.pointsView);
            editImageView = itemView.findViewById(R.id.editView);
            iconImageView = itemView.findViewById(R.id.imageView);

            editImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position, "edit");
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, String buttonClicked);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}