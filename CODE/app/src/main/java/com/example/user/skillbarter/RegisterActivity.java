package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();

        mStorage = FirebaseStorage.getInstance();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "***** onStart");
        super.onStart();
    }


    @OnClick(R.id.button_next)
    public void onNextClicked() {
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

    }
}
