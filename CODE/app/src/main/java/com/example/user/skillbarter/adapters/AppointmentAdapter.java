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
import com.example.user.skillbarter.models.Appointment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;

import static com.example.user.skillbarter.BaseActivity.getSkillImageID;


public class AppointmentAdapter extends FirestoreRecyclerAdapter<Appointment, AppointmentAdapter.AppointmentHolder> {

    private OnItemClickListener clickListener;

    class AppointmentHolder extends RecyclerView.ViewHolder {

        TextView skillCategoryTextView, timestampTextView, pointsTextView, ratingTextView;
        ImageView skillImageView;
        RatingBar ratingBar;

        public AppointmentHolder(View itemView) {
            super(itemView);
            skillCategoryTextView = itemView.findViewById(R.id.appointment_category_and_skill_text_view);
            skillImageView = itemView.findViewById(R.id.appointment_skill_image_view);
            timestampTextView = itemView.findViewById(R.id.appointment_timestamp_text_view);
            pointsTextView = itemView.findViewById(R.id.appointment_points_text_view);
            ratingTextView = itemView.findViewById(R.id.appointment_rating_text_view);
            ratingBar = itemView.findViewById(R.id.appointment_rating_bar);


            // Optional for adding clicks functionality on appointment items
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {

                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                        clickListener.onItemClick(snapshot, position);
                    }
                }
            });
        }
    }


    public AppointmentAdapter(@NonNull FirestoreRecyclerOptions<Appointment> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull AppointmentHolder holder, int position, @NonNull Appointment appointment) {

        String timestampFormatted = new SimpleDateFormat("E dd.MM.yyyy, HH:mm").format(appointment.getDate());
        holder.timestampTextView.setText(timestampFormatted);

        holder.pointsTextView.setText(String.valueOf(appointment.getPoints()));

        String[] skillID = appointment.getSkillID().split("\\.");
        String category = skillID[1], skill = skillID[2]; //providerID = skillID[0]
        String categorySkill = skill + " (" + category + ")";

        holder.skillCategoryTextView.setText(categorySkill);
        holder.skillImageView.setImageResource(getSkillImageID(category, skill));

        // Adjust or hide rating bar
        if (appointment.getRating() > 0) {
            holder.ratingTextView.setText(String.format("%.1f", appointment.getRating()));
            holder.ratingTextView.setVisibility(View.VISIBLE);

            holder.ratingBar.setRating(appointment.getRating());
            holder.ratingBar.setVisibility(View.VISIBLE);
        } else {
            holder.ratingTextView.setVisibility(View.GONE);
            holder.ratingBar.setVisibility(View.GONE);
        }
    }


    @NonNull
    @Override
    public AppointmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item,
                parent, false);
        return new AppointmentHolder(v);
    }


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }
}
