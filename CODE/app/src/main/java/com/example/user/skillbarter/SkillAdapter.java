package com.example.user.skillbarter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class SkillAdapter extends FirestoreRecyclerAdapter<UserSkills, SkillAdapter.SkillHolder> {

    public SkillAdapter(@NonNull FirestoreRecyclerOptions<UserSkills> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SkillHolder holder, int position, @NonNull UserSkills model) {
        holder.textViewSkill.setText(model.getSkill());
    }

    @NonNull
    @Override
    public SkillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_item,
                parent, false);
        return new SkillHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class SkillHolder extends RecyclerView.ViewHolder {
        TextView textViewSkill;

        public SkillHolder(View itemView) {
            super(itemView);
            textViewSkill = itemView.findViewById(R.id.textView);
        }
    }
}