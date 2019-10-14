package com.example.user.skillbarter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.skillbarter.models.UserData;
import com.example.user.skillbarter.services.CameraService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javax.annotation.Nullable;


public class RegisterActivity extends ActionBarMenuActivity
        implements DatePickerDialog.OnDateSetListener, EventListener<DocumentSnapshot> {


    @BindView(R.id.input_first_name)
    EditText firstNameView;

    @BindView(R.id.input_last_name)
    EditText lastNameView;

    @BindView(R.id.input_birthday)
    TextView birthdayView;

    @BindView(R.id.gender_button)
    RadioGroup genderRadioGroup;

    @BindView(R.id.male_radio_button)
    RadioButton maleRadioButton;

    @BindView(R.id.female_radio_button)
    RadioButton femaleRadioButton;

    @BindView(R.id.input_phone_number)
    EditText phoneNumberView;

    @BindView(R.id.user_profile_image_view)
    ImageView profilePictureView;

    @BindView(R.id.input_address)
    EditText addressView;

    private static final String TAG = "RegisterActivity";
    public static final String KEY_USER_ID = "key_user_id";

    private FirebaseAuth mAuth;
    private String signedInUserID;

    // Indicate if user's info needs to be loaded from database.
    private boolean shouldLoadUser = false;

    // listener to signed-in user's document from database.
    private ListenerRegistration mListener;

    private DocumentReference mUserRef;

    private String userID, profilePictureURL, firstName, lastName, phoneNumber, address, email, gender;
    private Date birthDate;

    int pointsBalance = INITIAL_POINTS_BALANCE;

    private CameraService cameraService;

    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        setTitle(R.string.user_information_title);

        cameraService = new CameraService(this, this);
        mAuth = FirebaseAuth.getInstance();

        // Get user ID from extras
        if (getIntent().getExtras() != null) {
            // user reached this intent by clicking on edit profile button.
            signedInUserID = getIntent().getExtras().getString(KEY_USER_ID);
            mUserRef = mFirestore.collection(getString(R.string.collection_user_data))
                    .document(signedInUserID);
            shouldLoadUser = true;
        } else {
            // user reached this intent by signing in for the first time.
            // disable all options menu bar except for sign out.
            setEnable(false);
        }
        hideSoftKeyboard();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mUserRef != null) {
            mListener = mUserRef.addSnapshotListener(this);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.remove();
            mListener = null;
        }
    }


    /* get the result of the camera or file picker activity invoked in the CameraService class! */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == CameraService.REQUEST_TAKE_PHOTO) {
                try {
                    cameraService.setmPhotoFile(cameraService.getmCompressor()
                            .compressToFile(cameraService.getmPhotoFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mUri =  Uri.fromFile(cameraService.getmPhotoFile());
                profilePictureView.setBackground(null);
                Glide.with(this).load(cameraService.getmPhotoFile())
                        .apply(new RequestOptions().centerCrop().circleCrop()
                                .placeholder(R.drawable.incognito)).into(profilePictureView);
            } else if (requestCode == CameraService.REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    cameraService.setmPhotoFile(cameraService.getmCompressor()
                            .compressToFile(new File(cameraService.getRealPathFromUri(selectedImage))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mUri =  Uri.fromFile(cameraService.getmPhotoFile());
                profilePictureView.setBackground(null);
                Glide.with(this).load(cameraService.getmPhotoFile())
                        .apply(new RequestOptions().centerCrop().circleCrop()
                                .placeholder(R.drawable.incognito)).into(profilePictureView);
            }
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        birthDate = calendar.getTime();
        birthdayView.setText(new SimpleDateFormat("dd.MM.yyyy").format(birthDate));
    }


    public void OnCalendarImageClicked(View view) {
        DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "date picker");
    }


    @OnClick(R.id.button_gallery)
    public void  onButtonGalleryClicked() {
        cameraService.handleUserProfilePicture(false);
    }


    @OnClick(R.id.button_camera)
    public void onButtonCameraClicked() {
        cameraService.handleUserProfilePicture(true);
    }


    @OnClick(R.id.button_next)
    public void onNextClicked() {
        userID = mAuth.getUid();
        firstName = firstNameView.getText().toString();
        lastName = lastNameView.getText().toString();
        address = addressView.getText().toString();
        email = mAuth.getCurrentUser().getEmail();
        phoneNumber = phoneNumberView.getText().toString();

        int checkedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton b = genderRadioGroup.findViewById(checkedId);
        if (b != null) {
            gender = (String) b.getText();
        }

        if (!validateForm()) {
            return;
        }

        // Switch user's interface to the next registration form.
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
        if (mUri == null) {
            /* user was edited and his profile picture didn't change */
            createUser();
            onBackPressed();
        } else {
            /* a new image picked as a profile picture for a new user */
            saveProfileImageInFirebase(mUri);
        }
    }


    private void saveProfileImageInFirebase(Uri imageUri) {

        showProgressDialog();

        /* Create a storage reference to the user's profile image. */
        StorageReference storageRef = mStorage.getReference();

        final StorageReference imageRef = storageRef.child("image/" + imageUri.getLastPathSegment());
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "successfully uploaded image. uri= "+ uri.toString());

                        profilePictureURL = uri.toString();
                        createUser();

                        hideProgressDialog();

                        if (signedInUserID != null) {
                            onBackPressed();
                        } else {
                            startActivity(new Intent(RegisterActivity.this, UserHomeProfile.class));
                        }
                    }
                });
            }
        });
    }


    private boolean validateBirthDay() {
        if (birthDate == null) {
            return false;
        } else {
            Date today = new Date();
            return birthDate.before(today);
        }
    }


    private boolean validateForm() {
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

        if (gender == null) {
            maleRadioButton.setError("Required.");
        } else {
            maleRadioButton.setError(null);
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

        if (!validateBirthDay()) {
            birthdayView.setError("Invalid.");
            valid = false;
        } else {
            birthdayView.setError(null);
        }

        return valid;
    }


    /*
     * create a new User instance and add current user to database.
     */
    private void createUser() {
        UserData userData = new UserData(userID, profilePictureURL, birthDate, firstName,
                lastName, phoneNumber, address, email, gender, pointsBalance);

        DocumentReference userRef = mFirestore.collection(getString(R.string.collection_user_data))
                .document(userID);
        userRef.set(userData);

        hideProgressDialog();
    }


    /**
     * fill the registration form with the currently signed in user's details in order to
     * let him edit his profile.
     */
    private void loadUserData(UserData userData) {

        firstNameView.setText(userData.getFirstName());
        lastNameView.setText(userData.getLastName());
        addressView.setText(userData.getAddress());
        phoneNumberView.setText(userData.getPhoneNumber());

        gender = userData.getGender();

        if (gender != null && gender.equals(getString(R.string.female))) {
            femaleRadioButton.setChecked(true);
            maleRadioButton.setChecked(false);
        } else if (gender != null && gender.equals(getString(R.string.male))) {
            femaleRadioButton.setChecked(false);
            maleRadioButton.setChecked(true);
        }

        birthDate = userData.getDateOfBirth();
        if (birthDate != null) {
            birthdayView.setText(new SimpleDateFormat("dd.MM.yyyy").format(birthDate));
        }

        profilePictureURL = userData.getProfilePictureURL();

        if (profilePictureURL != null) {
            Glide.with(this).load(profilePictureURL)
                    .apply(new RequestOptions().centerCrop().circleCrop()
                            .placeholder(R.drawable.incognito)).into(profilePictureView);
        }
    }


    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }

        if (documentSnapshot != null && documentSnapshot.getReference().equals(mUserRef)) {
            if (shouldLoadUser) {
                loadUserData(documentSnapshot.toObject(UserData.class));
                shouldLoadUser = false;
            }
        }
    }
}
