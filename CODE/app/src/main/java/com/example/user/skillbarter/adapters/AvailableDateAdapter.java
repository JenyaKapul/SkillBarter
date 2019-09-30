package com.example.user.skillbarter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.skillbarter.R;

import com.example.user.skillbarter.models.AvailableDate;
import com.example.user.skillbarter.screens.AvailableDatesManagerActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class AvailableDateAdapter extends FirestoreRecyclerAdapter<AvailableDate, AvailableDateAdapter.AvailableDateHolder> {
//    private OnItemClickListener listener;

    public AvailableDateAdapter(@NonNull FirestoreRecyclerOptions<AvailableDate> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AvailableDateHolder holder, int position, @NonNull AvailableDate model) {
        holder.swAvailability.setChecked(model.isValid());
        holder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(model.getDate()));
        holder.tvTime.setText(new SimpleDateFormat("hh:mm").format(model.getDate()));
        if(model.isBooked()) {
            holder.swAvailability.setClickable(false);
        }
        else {
            final String uID = FirebaseAuth.getInstance().getUid();
            final String docID = new SimpleDateFormat("dd.MM.yy hh:mm").format(model.getDate());
            holder.swAvailability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //TODO: implemet transaction to update availability correctly
                    FirebaseFirestore.getInstance().collection("User Data")
                            .document(uID).collection("Available Dates")
                            .document(docID).update("valid", isChecked);
                }
            });
        }

    }

    @NonNull
    @Override
    public AvailableDateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_date_item,
                parent, false);
        return new AvailableDateHolder(v);
    }

    public boolean deleteItem(int position) {
        AvailableDate date = getSnapshots().getSnapshot(position).toObject(AvailableDate.class);
        if (!date.isBooked()){
            getSnapshots().getSnapshot(position).getReference().delete();
            return true;
        }
        return false;
    }

    class AvailableDateHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime;
        Switch swAvailability;
//        ImageView deleteImageView;

        public AvailableDateHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.available_date_text_view);
            tvTime = itemView.findViewById(R.id.available_time_text_view);
            swAvailability = itemView.findViewById(R.id.free_time_availability);

//            deleteImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION && listener != null) {
//                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
//                    }
//                }
//            });
        }
    }

//    public interface OnItemClickListener {
//        void onItemClick(DocumentSnapshot documentSnapshot, int position);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }

}
