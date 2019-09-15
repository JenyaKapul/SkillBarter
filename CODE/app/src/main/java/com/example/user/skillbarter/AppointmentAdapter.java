package com.example.user.skillbarter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class AppointmentAdapter extends FirestoreRecyclerAdapter<Appointment, AppointmentAdapter.AppointmentHolder> {

    private static final String TAG = "AppointmentAdapter";
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private AppointmentAdapter.OnItemClickListener listener;

    public AppointmentAdapter(@NonNull FirestoreRecyclerOptions<Appointment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AppointmentHolder holder, int position, @NonNull Appointment model) {
        Log.v(TAG, "onBindViewHolder: AppointmentHolder");
        holder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(model.getDate().toDate()));
        this.setDataFromUserData(this.getOtherUser(FirebaseAuth.getInstance().getUid(), model), holder);
        this.setDataFromSkillData(model.getSkillID(), holder);
    }

    private String getOtherUser(String currUID, Appointment appointment) {
        if (currUID == appointment.getProviderUID()) {
            return appointment.getClientUID();
        }
        return appointment.getProviderUID();
    }

    private void setDataFromUserData(String uID, @NonNull final AppointmentAdapter.AppointmentHolder holder) {
        Log.v(TAG, "onBindViewHolder: setDataFromUserData");
        DocumentReference mUserRef = mFirestore.collection("User Data")
                .document(uID);
        mUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    UserData ud = documentSnapshot.toObject(UserData.class);
                    holder.tvOtherProfileName.setText(ud.getFullName());
                    holder.ivOtherProfilePicture.setImageResource(R.drawable.incognito); //TODO
                } else {
                    Log.e(TAG, "Document does not exist");
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    private void setDataFromSkillData(String skillID, @NonNull final AppointmentAdapter.AppointmentHolder holder) {
        Log.v(TAG, "onBindViewHolder: setDataFromSkillData");
        DocumentReference mUserRef = mFirestore.collection("User Skills")
                .document(skillID);
        mUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    UserSkill us = documentSnapshot.toObject(UserSkill.class);
                    holder.tvSkill.setText(us.getSkill());
                    holder.tvCategory.setText("(" + us.getCategory() + ")");
                    holder.tvValue.setText(String.valueOf(us.getPointsValue()));
                } else {
                    Log.e(TAG, "Document does not exist");
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.toString());
                    }
                });
    }

    @NonNull
    @Override
    public AppointmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.appointment_item,
                viewGroup, false);
        return new AppointmentHolder(v);
    }
//
    class AppointmentHolder extends RecyclerView.ViewHolder {
        TextView tvOtherProfileName, tvDate, tvSkill, tvCategory, tvValue;
        ImageView ivOtherProfilePicture;

        public AppointmentHolder(@NonNull View itemView) {
            super(itemView);
            tvOtherProfileName = itemView.findViewById(R.id.appointment_profile_name);
            ivOtherProfilePicture = itemView.findViewById(R.id.appointment_profile_picture_holder);
            tvDate = itemView.findViewById(R.id.appointment_date_text_view);
            tvSkill = itemView.findViewById(R.id.appointment_skill_text_view);
            tvCategory = itemView.findViewById(R.id.appointment_category_text_view);
            tvValue = itemView.findViewById(R.id.appointment_value_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener !=null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(AppointmentAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
