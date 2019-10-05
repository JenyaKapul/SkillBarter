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
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;


public class ServiceDetailsAdapter extends FirestoreRecyclerAdapter<AvailableDate, ServiceDetailsAdapter.ServiceDetailsHolder> {

    private OnItemClickListener clickListener;

    class ServiceDetailsHolder extends RecyclerView.ViewHolder {

        TextView timestampTextView, bookedTextView;

        public ServiceDetailsHolder(View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.timestamp_text_view);
            bookedTextView = itemView.findViewById(R.id.booked_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {

                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);

                        boolean isBooked = snapshot.getBoolean("booked");
                        if (!isBooked) {
                            clickListener.onItemClick(snapshot, position);
                        }

                    }
                }
            });
        }
    }


    public ServiceDetailsAdapter(@NonNull FirestoreRecyclerOptions<AvailableDate> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull ServiceDetailsHolder holder, int position, @NonNull AvailableDate availableDate) {

        String timestampFormatted = new SimpleDateFormat("E dd.MM.yy, HH:mm").format(availableDate.getDate());
        holder.timestampTextView.setText(timestampFormatted);

        if (availableDate.isBooked()) {
            holder.bookedTextView.setVisibility(View.VISIBLE);
        } else {
            holder.bookedTextView.setVisibility(View.INVISIBLE);
        }
    }


    @NonNull
    @Override
    public ServiceDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_date_item,
                parent, false);
        return new ServiceDetailsHolder(v);
    }


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
}