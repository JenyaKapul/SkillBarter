package com.example.user.skillbarter;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.skillbarter.utils.FileCompressor;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

//    private static final int GALLERY_INTENT = 5;
////
////    private static final int CAMERA_INTENT = 6;
////
////    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 7;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    FileCompressor mCompressor;
    @BindView(R.id.imageViewProfilePic)
    ImageView imageViewProfilePic;

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

    @BindView(R.id.input_address)
    EditText addressView;

    @BindView(R.id.date_picker)
    ImageButton datePickerButton;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;

    private String signedInUserID;

    // listener to signed-in user's document from database.
    private ListenerRegistration mListener;

    private DocumentReference mUserRef;

    private String userID, profilePictureURL, firstName, lastName, phoneNumber, address, email, gender;

    Timestamp dateOfBirth;

    int pointsBalance = INITIAL_POINTS_BALANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "***** onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mCompressor = new FileCompressor(this);
        mFirestore = FirebaseFirestore.getInstance();

        mStorage = FirebaseStorage.getInstance();

        mAuth = FirebaseAuth.getInstance();

        // Get user ID from extras
        if (getIntent().getExtras() != null) {
            // user reached this intent by clicking on edit profile button.
            signedInUserID = getIntent().getExtras().getString(KEY_USER_ID);
            showProgressDialog();
            mUserRef = mFirestore.collection(getString(R.string.collection_user_data))
                    .document(signedInUserID);
        } else {
            // user reached this intent by signing in for the first time.
            // disable all options menu bar except for sign out.
            setEnable(false);
        }
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


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        Log.d(TAG, "***** onActivityResult");
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//
//
//        if (requestCode == CAMERA_INTENT || requestCode == GALLERY_INTENT) {
//
//            if (resultCode == RESULT_OK) {
//
//                // Get the Uri of the image from the gallery intent data
//                final Uri selectedImage = imageReturnedIntent.getData();
//
//                // Set the selected image in the profile picture view
//                profilePictureView.setImageURI(selectedImage);
//
//                // Create a storage reference to the user's profile image
//                StorageReference storageRef = mStorage.getReference();
//
//                final StorageReference imageRef = storageRef.child("image/" + selectedImage.getLastPathSegment());
//
//                imageRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Log.d(TAG, "successfully uploaded user's image from gallery to Firebase Storage. uri= "+ uri.toString());
//                            profilePictureURL = uri.toString();
//                        }
//                    });
//                }
//            });
//            }
//        }
//    }

    @OnClick(R.id.button_next)
    public void onNextClicked() {
        Log.d(TAG, "***** onNextClicked");

        userID = mAuth.getUid();
        firstName = firstNameView.getText().toString();

        lastName = lastNameView.getText().toString();
        address = addressView.getText().toString();
        email = mAuth.getCurrentUser().getEmail();

        phoneNumber = phoneNumberView.getText().toString(); // dateofbirth

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

//    @OnClick(R.id.button_gallery)
//    public void onButtonGalleryClicked() {
//        Log.d(TAG, "***** onButtonGalleryClicked");
//        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(pickPhoto, GALLERY_INTENT);
//    }
//
//    @OnClick(R.id.button_camera)
//    public void onButtonCameraClicked() {
//        Log.d(TAG, "***** onButtonCameraClicked");
//        requestRead();
//        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(takePicture, CAMERA_INTENT);
//    }



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

        if (this.userBirthDay == null) {
            birthdayView.setError("Required.");
            valid = false;
        } else if (!validateBirthDay()) {
            birthdayView.setError("You must be at least " + AGE_LIMIT + " years old.");
            Toast.makeText(this, "You must need at least " + AGE_LIMIT + " years old.", Toast.LENGTH_LONG).show();
            valid = false;
        }
        else{
            birthdayView.setError(null);
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

    @OnClick(R.id.button_save)
    public void onSaveClicked() {
        Log.d(TAG, "***** onSaveClicked");
        createUser();
        Intent intent = new Intent(RegisterActivity.this, UserHomeProfile.class);
        startActivity(intent);
    }

    // Add current user to Firebase Firestore
    private void createUser() {
        Log.d(TAG, "***** createUser");
        UserData userData = new UserData(userID, profilePictureURL, dateOfBirth, firstName,
                lastName, phoneNumber, address, email, gender, pointsBalance);

        DocumentReference userRef = mFirestore.collection(getString(R.string.collection_user_data))
                .document(userID);
        userRef.set(userData);
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

        Log.d(TAG, "***** loadUserData: gender= " + userData.getGender());
        if (userData.getGender().equals("Female")) {
            femaleRadioButton.setChecked(true);
            maleRadioButton.setChecked(false);
        } else {
            femaleRadioButton.setChecked(false);
            maleRadioButton.setChecked(true);
        }

        dateOfBirth = userData.getDateOfBirth();
        String dateString = DateFormat.getDateInstance().format(dateOfBirth.toDate());
        birthdayView.setText(dateString);
        datePickerButton.setVisibility(View.INVISIBLE);


        Glide.with(imageViewProfilePic.getContext())
                .load(userData.getProfilePictureURL())
                .into(imageViewProfilePic);
    }

//
//    /**
//     * requestPermissions and do something
//     *
//     */
//    public void requestRead() {
//        Log.d(TAG, "***** requestRead");
//
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
////        } else {
////            readFile();
//        }
//    }

//    /**
//     * do you want to do
//     */
//    public void readFile() {
//        // do something
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        Log.d(TAG, "***** onRequestPermissionsResult");
//        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                readFile();
//            } else {
//                // Permission Denied
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }

        if (documentSnapshot.getReference().equals(mUserRef)) {
            loadUserData(documentSnapshot.toObject(UserData.class));
            hideProgressDialog();
        }
    }


/*****************************************************************************************
 *
 *
 * **********************************************************************************8*/
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } else if (items[item].equals("Choose from Library")) {
                requestStoragePermission(false);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }


    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(RegisterActivity.this).load(mPhotoFile).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.incognito)).into(imageViewProfilePic);
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(RegisterActivity.this).load(mPhotoFile).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.incognito)).into(imageViewProfilePic);

            }
        }
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }


    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Create file with current timestamp name
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

    /**
     * Get real file path from URI
     *
     * @param contentUri
     * @return
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @OnClick(R.id.imageViewProfilePic)
    public void onViewClicked() {
        selectImage();
    }
}
