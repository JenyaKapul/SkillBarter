package com.example.user.skillbarter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ApointmentAdapter extends FirestoreRecyclerAdapter<Appointment, ApointmentAdapter.ApointmentHolder> {

    public ApointmentAdapter(@NonNull FirestoreRecyclerOptions<Appointment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ApointmentHolder holder, int position, @NonNull Appointment model) {
    //TODO: need to think how to implement the class Appointment
    }

    @NonNull
    @Override
    public ApointmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    class ApointmentHolder extends RecyclerView.ViewHolder {
        TextView tvProviderName, tvDate, tvSkill, tvCategory, tvValue;
        ImageView  ivProviderPicture;

        public ApointmentHolder(@NonNull View itemView) {
            super(itemView);
            tvProviderName = itemView.findViewById(R.id.provider_profile_name);
            ivProviderPicture = itemView.findViewById(R.id.provider_picture_holder);
            tvDate = itemView.findViewById(R.id.date_text_view);
            tvSkill = itemView.findViewById(R.id.skill_text_view);
            tvCategory = itemView.findViewById(R.id.category_text_view);
            tvValue = itemView.findViewById(R.id.value_text_view);
        }
    }
}
