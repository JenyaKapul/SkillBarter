package com.example.user.skillbarter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import com.example.user.skillbarter.R;

import com.example.user.skillbarter.models.FreeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;

public class FreeTimeAdapter extends FirestoreRecyclerAdapter<FreeTime, FreeTimeAdapter.FreeTimeHolder> {

    public FreeTimeAdapter(@NonNull FirestoreRecyclerOptions<FreeTime> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FreeTimeHolder holder, int position, @NonNull FreeTime model) {
        holder.tvAvailability.setChecked(model.isAvailable());
        holder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy     HH:mm").format(model.getDate()));
    }

    @NonNull
    @Override
    public FreeTimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.free_time_item,
                parent, false);
        return new FreeTimeHolder(v);
    }

    class FreeTimeHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        Switch tvAvailability;

        public FreeTimeHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.free_time_date);
            tvAvailability = itemView.findViewById(R.id.free_time_availability);
            tvAvailability.setClickable(false);
        }
    }

}
