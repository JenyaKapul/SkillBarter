package com.example.user.skillbarter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.skillbarter.R;

import com.example.user.skillbarter.models.AvailableDate;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;



public class AvailableDateAdapter extends FirestoreRecyclerAdapter<AvailableDate, AvailableDateAdapter.AvailableDateHolder> {


    class AvailableDateHolder extends RecyclerView.ViewHolder {

        TextView dateTextView, timeTextView, bookedTextView;

        public AvailableDateHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            bookedTextView = itemView.findViewById(R.id.booked_text_view);
        }
    }


    public AvailableDateAdapter(@NonNull FirestoreRecyclerOptions<AvailableDate> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull AvailableDateHolder holder, int position, @NonNull AvailableDate availableDate) {

        if (!availableDate.isBooked()) {
            holder.bookedTextView.setVisibility(View.INVISIBLE);
        }

        // Set date and time.
        holder.dateTextView.setText(new SimpleDateFormat("E dd/MM/yyyy").format(availableDate.getDate()));
        holder.timeTextView.setText(new SimpleDateFormat("HH:mm").format(availableDate.getDate()));
    }


    @NonNull
    @Override
    public AvailableDateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_date_item,
                parent, false);
        return new AvailableDateHolder(v);
    }
}