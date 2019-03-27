package com.example.user.skillbarter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

//import org.joda.time.LocalDate;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.bumptech.glide.Glide;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private static final int GALLERY_INTENT = 2;

    @BindView(R.id.input_first_name)
    EditText firstNameView;

    @BindView(R.id.input_last_name)
    EditText lastNameView;

    @BindView(R.id.input_birthday)
    TextView birthdayView;

    @BindView(R.id.gender_button)
    RadioGroup genderRadioGroup;

    @BindView(R.id.male_radio_button)
    RadioButton lastRadioButton;

    @BindView(R.id.input_phone_number)
    EditText phoneNumberView;

    @BindView(R.id.profile_picture_holder)
    ImageView profilePictureView;

    @BindView(R.id.input_address)
    EditText addressView;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get the Uri of the image from the gallery intent data
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            final Uri file = data.getData();

            // Create a storage reference to the user's profile image
            StorageReference storageRef = mStorage.getReference();

            final StorageReference imageRef = storageRef.child("image/" + file.getLastPathSegment());

            imageRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d(TAG, "successfully uploaded user's image from gallery to Firebase Storage. uri= "+ uri.toString());
                            profilePictureURL = uri.toString();
                            profilePictureView.setImageURI(file);
                        }
                    });
                }
            });
        }
    }

    @OnClick(R.id.button_next)
    public void onNextClicked() {
        Log.d(TAG, "***** onNextClicked");

        //birthday, phonenumber
        userID = mAuth.getUid();  //mAuth.getCurrentUser().getUid()
        firstName = firstNameView.getText().toString();

        lastName = lastNameView.getText().toString();
        address = addressView.getText().toString();
        email = mAuth.getCurrentUser().getEmail();

        phoneNumber = phoneNumberView.getText().toString(); //TODO

        int checkedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton b = genderRadioGroup.findViewById(checkedId);
        if (b != null) {
            gender = (String) b.getText();
        }
        if (!validateForm1()) {
            return;
        }

        Log.d(TAG, "***** onNextClicked: full name: " + firstName + " " + lastName + " address: " + address + " email: " + email + " phone: " + phoneNumber + " gender: " + gender);
        findViewById(R.id.register_page_1).setVisibility(View.GONE);
        findViewById(R.id.register_page_2).setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_gallery)
    public void onButtonGalleryClicked() {
        Log.d(TAG, "launching gallery");

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    private boolean validateForm1() {
        Log.d(TAG, "***** validateForm");
        boolean valid = true;

        if (TextUtils.isEmpty(firstName)) {
            firstNameView.setError("Required.");
            valid = false;
        } else {
            firstNameView.setError(null);
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameView.setError("Required.");
            valid = false;
        } else {
            lastNameView.setError(null);
        }
        //TODO: require dateOfBirth not picked

        if (gender == null) {
            lastRadioButton.setError("Required.");
        } else {
            lastRadioButton.setError(null);
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberView.setError("Required.");
            valid = false;
        } else {
            phoneNumberView.setError(null);
        }
        if (TextUtils.isEmpty(address)) {
            addressView.setError("Required.");
            valid = false;
        } else {
            addressView.setError(null);
        }
        return valid;
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
