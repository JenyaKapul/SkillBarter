package com.example.user.skillbarter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

//import org.joda.time.LocalDate;


import java.text.DateFormat;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "RegisterActivity";

    private static final int GALLERY_INTENT = 0;

    private static final int CAMERA_INTENT = 1;

    private static final int INITIAL_POINTS_BALANCE = 50;


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

    int pointsBalance = INITIAL_POINTS_BALANCE;



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


    @OnClick(R.id.date_picker)
    public void onDatePickerClicked() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currDateString = DateFormat.getDateInstance().format(c.getTime());
        birthdayView.setText(currDateString);
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

        phoneNumber = phoneNumberView.getText().toString(); //TODO phonenumber + dateofbirth

        int checkedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton b = genderRadioGroup.findViewById(checkedId);
        if (b != null) {
            gender = (String) b.getText();
        }
//        if (!validateForm1()) {
//            return;
//        }
        // Switch user's interface to the next registration form.
        findViewById(R.id.register_page_1).setVisibility(View.GONE);
        findViewById(R.id.register_page_2).setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_gallery)
    public void onButtonGalleryClicked() {
        Log.d(TAG, "***** onButtonGalleryClicked");
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, GALLERY_INTENT);
    }

    @OnClick(R.id.button_camera)
    public void onButtonCameraClicked() {
        Log.d(TAG, "***** onButtonCameraClicked");
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_INTENT);//zero can be replaced with any action code
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

        UserData userData = new UserData(userID, profilePictureURL, dateOfBirth, firstName,
                lastName, phoneNumber, address, email, gender, pointsBalance);

        DocumentReference userRef = mFirestore.collection(getString(R.string.collection_user_data))
                .document(userID);
        userRef.set(userData);
    }



}
