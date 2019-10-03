package com.example.user.skillbarter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.user.skillbarter.R;

import com.example.user.skillbarter.models.AvailableDate;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

import static com.example.user.skillbarter.Constants.DATES_COLLECTION;
import static com.example.user.skillbarter.Constants.USERS_COLLECTION;

public class AvailableDateAdapter extends FirestoreRecyclerAdapter<AvailableDate, AvailableDateAdapter.AvailableDateHolder> {

    public AvailableDateAdapter(@NonNull FirestoreRecyclerOptions<AvailableDate> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AvailableDateHolder holder, int position, @NonNull AvailableDate availableDate) {

        boolean isChecked = availableDate.isValid(); //TODO (NOA): Consider adding !isBooked constraint
        holder.availabilitySwitch.setChecked(isChecked);

        // Set date and time.
        holder.dateTextView.setText(new SimpleDateFormat("dd/MM/yyyy").format(availableDate.getDate()));
        holder.timeTextView.setText(new SimpleDateFormat("hh:mm").format(availableDate.getDate()));

        // Prevent switching the availability switch in case the date is booked
        if(availableDate.isBooked()) {
            holder.availabilitySwitch.setClickable(false);
        }
        else {
            final String uID = FirebaseAuth.getInstance().getUid();
            final String docID = new SimpleDateFormat("dd.MM.yy hh:mm").format(availableDate.getDate());
            holder.availabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //TODO: implemet transaction to update availability correctly
                    FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                            .document(uID).collection(DATES_COLLECTION)
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

    class AvailableDateHolder extends RecyclerView.ViewHolder {

        TextView dateTextView, timeTextView;
        Switch availabilitySwitch;

        public AvailableDateHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            availabilitySwitch = itemView.findViewById(R.id.availability_switch);
        }
    }

    public boolean deleteItem(int position) {
        AvailableDate date = getSnapshots().getSnapshot(position).toObject(AvailableDate.class);
        if (!date.isBooked()){
            getSnapshots().getSnapshot(position).getReference().delete();
            return true;
        }
        return false;
    }
}
