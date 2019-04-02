package com.example.user.skillbarter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {
    private ArrayList<SkillItem> mSkillList;

    public static class SkillViewHolder extends RecyclerView.ViewHolder {
        public TextView mSkillView;

        public SkillViewHolder(View itemView) {
            super(itemView);
            mSkillView = itemView.findViewById(R.id.textView);
        }
    }

    public SkillAdapter(ArrayList<SkillItem> skillList) {
        mSkillList = skillList;
    }

    @Override
    public SkillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_item, parent, false);
        SkillViewHolder evh = new SkillViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(SkillViewHolder holder, int position) {
        SkillItem currentItem = mSkillList.get(position);

//        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mSkillView.setText(currentItem.getSkill());
//        holder.mTextView2.setText(currentItem.getText2());
    }

    @Override
    public int getItemCount() {
        return mSkillList.size();
    }
}