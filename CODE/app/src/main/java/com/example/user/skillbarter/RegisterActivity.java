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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javax.annotation.Nullable;


public class RegisterActivity extends ActionBarMenuActivity
        implements DatePickerDialog.OnDateSetListener, EventListener<DocumentSnapshot> {

    //TODO: implement 2 different cases: 1.first registration 2. edit profile.
    //TODO: remove calendar picker when 2
    //TODO: force phone's next button to move to the next editText input

    private static final String TAG = "RegisterActivity";

    public static final String KEY_USER_ID = "key_user_id";

    private static final int AGE_LIMIT = 17;

    private static final int INITIAL_POINTS_BALANCE = 50;

    private Calendar userBirthDay = null;

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

    @BindView(R.id.profile_picture_holder)
    ImageView profilePictureView;

    @BindView(R.id.input_address)
    EditText addressView;

    @BindView(R.id.date_picker)
    ImageButton datePickerButton;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;

    private String signedInUserID;

    /* indicate if user's info needs to be loaded from database */
    private boolean shouldLoadUser = false;

    // listener to signed-in user's document from database.
    private ListenerRegistration mListener;

    private DocumentReference mUserRef;

    private String userID, profilePictureURL, firstName, lastName, phoneNumber, address, email, gender;

    Timestamp dateOfBirth;

    int pointsBalance = INITIAL_POINTS_BALANCE;

    private CameraService cameraService;

    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "***** onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mFirestore = FirebaseFirestore.getInstance();

        mStorage = FirebaseStorage.getInstance();

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
        cameraService = new CameraService(this, this);
        hideSoftKeyboard();
    }


    @Override
    public void onStart() {
        Log.d(TAG, "***** onStart");
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


    @OnClick(R.id.button_gallery)
    public void  onButtonGalleryClicked() {
        cameraService.handleUserProfilePicture(false);
    }


    @OnClick(R.id.button_camera)
    public void onButtonCameraClicked() {
        cameraService.handleUserProfilePicture(true);
    }


    private void setProfilePictureBackgroundInvisible(){
        ImageView imageView = findViewById(R.id.profile_picture_holder);
        imageView.setBackground(null);
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
                setProfilePictureBackgroundInvisible();
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
                setProfilePictureBackgroundInvisible();
                Glide.with(this).load(cameraService.getmPhotoFile())
                        .apply(new RequestOptions().centerCrop().circleCrop()
                                .placeholder(R.drawable.incognito)).into(profilePictureView);
            }
        }
    }


    private void saveProfileImageInFirebase(Uri imageUri){
        showProgressDialog();
        /* Create a storage reference to the user's profile image */
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


    @OnClick(R.id.date_picker)
    public void onDatePickerClicked() {
        if (this.userBirthDay != null){
            DatePickerFragment.setCalendar(this.userBirthDay);
        }
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        this.userBirthDay = c;
        String currDateString = DateFormat.getDateInstance().format(c.getTime());
        birthdayView.setText(currDateString);
        dateOfBirth = new Timestamp(c.getTime());
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


    private boolean validateBirthDay(){
        Calendar currentDate = Calendar.getInstance();
        int yearDiff = currentDate.get(Calendar.YEAR) - this.userBirthDay.get(Calendar.YEAR);
        int monthDiff = currentDate.get(Calendar.MONTH) - this.userBirthDay.get(Calendar.MONTH);
        int dayDiff = currentDate.get(Calendar.DAY_OF_MONTH) - this.userBirthDay.get(Calendar.DAY_OF_MONTH);
        if ((yearDiff > AGE_LIMIT) ||
                ((yearDiff == AGE_LIMIT) && (monthDiff > 0)) ||
                ((yearDiff == AGE_LIMIT) && (monthDiff == 0) && (dayDiff > 0))) {
            return true;
        }
        return false;
    }

    private boolean validateForm() {
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

        return valid;
    }


    @OnClick(R.id.button_prev2)
    public void onPrev2Clicked() {
        Log.d(TAG, "***** onPrev2Clicked");
        findViewById(R.id.register_page_1).setVisibility(View.VISIBLE);
        findViewById(R.id.register_page_2).setVisibility(View.GONE);
    }


    /*
     * create a new User instance and add current user to database.
     */
    private void createUser() {
        UserData userData = new UserData(userID, profilePictureURL, dateOfBirth, firstName,
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
        Log.d(TAG, "***** loadUserData");

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

        dateOfBirth = userData.getDateOfBirth();

        if (dateOfBirth != null) {
            String dateString = DateFormat.getDateInstance().format(dateOfBirth.toDate());
            birthdayView.setText(dateString);
        }

        profilePictureURL = userData.getProfilePictureURL();

        if (profilePictureURL != null) {
            setProfilePictureBackgroundInvisible();
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

        if (documentSnapshot.getReference().equals(mUserRef)) {
            if (shouldLoadUser) {
                loadUserData(documentSnapshot.toObject(UserData.class));
                shouldLoadUser = false;
            }
        }
    }
}
