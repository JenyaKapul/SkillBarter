package com.example.user.skillbarter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.skillbarter.R;
import com.example.user.skillbarter.models.Appointment;
import com.example.user.skillbarter.models.UserData;
import com.example.user.skillbarter.models.UserSkill;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

import static com.example.user.skillbarter.Constants.USERS_COLLECTION;

public class AppointmentAdapter extends FirestoreRecyclerAdapter<Appointment, AppointmentAdapter.AppointmentHolder> {

    private static final String TAG = "AppointmentAdapter";
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private OnItemClickListener clickListener;


    class AppointmentHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, skillTextView, categoryTextView, pointsTextView;
        ImageView profileImageView;

        public AppointmentHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.appointment_profile_name);
            profileImageView = itemView.findViewById(R.id.appointment_profile_picture_holder);
            dateTextView = itemView.findViewById(R.id.appointment_date_text_view);
            skillTextView = itemView.findViewById(R.id.appointment_skill_text_view);
            categoryTextView = itemView.findViewById(R.id.appointment_category_text_view);
            pointsTextView = itemView.findViewById(R.id.appointment_value_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {
                        clickListener.onItemClick(getSnapshots().getSnapshot(position), position);
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

        holder.dateTextView.setText(new SimpleDateFormat("dd/MM/yy HH:mm").format(appointment.getDate()));

        setDataFromUserData(getClientUid(FirebaseAuth.getInstance().getUid(), appointment), holder);
        setDataFromSkillData(appointment.getSkillID(), holder);
    }

    private String getClientUid(String currUID, Appointment appointment) {
        if (currUID.equals(appointment.getProviderUID())) {
            return appointment.getClientUID();
        }
        return appointment.getProviderUID();
    }


    private void setDataFromUserData(String uID, @NonNull final AppointmentAdapter.AppointmentHolder holder) {
        DocumentReference mUserRef = mFirestore.collection(USERS_COLLECTION)
                .document(uID);
        mUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    UserData ud = documentSnapshot.toObject(UserData.class);
                    String fullName = ud.getFirstName() + " " + ud.getLastName();
                    holder.nameTextView.setText(fullName);
                    holder.profileImageView.setImageResource(R.drawable.incognito); //TODO
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
        DocumentReference mSkillRef = mFirestore.collection("User Skills")
                .document(skillID);
        mSkillRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    UserSkill us = documentSnapshot.toObject(UserSkill.class);
                    holder.skillTextView.setText(us.getSkill());
                    holder.categoryTextView.setText("(" + us.getCategory() + ")");
                    holder.pointsTextView.setText(String.valueOf(us.getPointsValue()));
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



    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(AppointmentAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }
}
