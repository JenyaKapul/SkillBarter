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
import com.google.firebase.firestore.DocumentSnapshot;


public class SkillAdapter extends FirestoreRecyclerAdapter<UserSkill, SkillAdapter.SkillHolder> {

    private OnItemClickListener listener;

    public SkillAdapter(@NonNull FirestoreRecyclerOptions<UserSkill> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SkillHolder holder, int position, @NonNull UserSkill model) {
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
        ImageView imageViewEdit;

        public SkillHolder(View itemView) {
            super(itemView);
            textViewSkill = itemView.findViewById(R.id.textView);
            imageViewEdit = itemView.findViewById(R.id.editView);

            imageViewEdit.setOnClickListener(new View.OnClickListener() {
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