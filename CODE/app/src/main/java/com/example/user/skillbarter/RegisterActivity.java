package com.example.user.skillbarter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

//import org.joda.time.LocalDate;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.input_first_name)
    EditText firstNameView;

    @BindView(R.id.input_last_name)
    EditText lastNameView;

    @BindView(R.id.input_birthday)
    TextView birthdayView;

    @BindView(R.id.gender_button)
    RadioGroup genderView;

    @BindView(R.id.input_phone_number)
    EditText phoneNumberView;

    @BindView(R.id.input_address)
    EditText addressView;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    String userID, profilePictureURL, firstName, lastName, phoneNumber, address, email, gender;
    Timestamp dateOfBirth;
    int pointsBalance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();

        mStorage = FirebaseStorage.getInstance();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "***** onStart");
        super.onStart();
    }

    @OnClick(R.id.button_next)
    public void onNextClicked() {

        //firstname, lastname, birthday, gender, phonenumber, address
        userID = mAuth.getUid();  //mAuth.getCurrentUser().getUid()
        firstName = firstNameView.getText().toString();
        lastName = lastNameView.getText().toString();
        address = addressView.getText().toString();
        email = mAuth.getCurrentUser().getEmail();

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "You must fill all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.register_page_1).setVisibility(View.GONE);
        findViewById(R.id.register_page_2).setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_prev2)
    public void onPrev2Clicked() {
        findViewById(R.id.register_page_1).setVisibility(View.VISIBLE);
        findViewById(R.id.register_page_2).setVisibility(View.GONE);
    }

    @OnClick(R.id.button_save)
    public void onSaveClicked() {
        createUser();
        Intent intent = new Intent(RegisterActivity.this, UserHomeProfile.class);
        startActivity(intent);
    }

    // Add current user to Firebase Firestore
    private void createUser() {
        //        UserData userData = new UserData(user.getUid(), mUserName, user.getEmail());
//        userRef.set(userData);




        //, profilePictureURL, phoneNumber, gender, pointsBalance, dateOfBirth;

//        // Get the dog's name
//        name = mDogNameView.getText().toString();
//        if (name.isEmpty()) {
//            // No name is given. Notify the user he MUST specify name
//            Toast.makeText(this, "You must specify your dog's name", Toast.LENGTH_SHORT).show();
//            return;
        }

}
