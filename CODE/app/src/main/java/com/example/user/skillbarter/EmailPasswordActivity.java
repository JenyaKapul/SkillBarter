package com.example.user.skillbarter;


import android.app.DownloadManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.Query;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class EmailPasswordActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Spinner mDomainsSpinner;

    private String mEmail;
    private String mPassword;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private FirebaseFirestore mFireStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        mDomainsSpinner = findViewById(R.id.domainsSpinner);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.verifyEmailButton).setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        mFireStore = FirebaseFirestore.getInstance();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("onStart", "userid= "+mAuth.getCurrentUser().getEmail());
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void createAccount() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
//                            createUser();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

//                            boolean flag = task.getResult().getAdditionalUserInfo().isNewUser();
//                            if (flag) {
//                                Toast.makeText(EmailPasswordActivity.this, "NEW USER.",
//                                        Toast.LENGTH_SHORT).show();
//
//                            } else {
//                                Toast.makeText(EmailPasswordActivity.this, "EXISTING USER.",
//                                        Toast.LENGTH_SHORT).show();
//
//                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
//        Log.d(TAG, "userid= "+mAuth.getCurrentUser().getEmail());
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verifyEmailButton).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private boolean validateForm() {
        boolean valid = true;

        mEmail = mEmailField.getText().toString();
        mPassword = mPasswordField.getText().toString();

        if (TextUtils.isEmpty(mEmail)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }
        String domain = (String) mDomainsSpinner.getSelectedItem();
        mEmail += domain;
        Log.d(TAG, "validateForm: " + mEmail);
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);

            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount();
        } else if (i == R.id.emailSignInButton) {
            signIn();
            createUser();

        } else if (i == R.id.signOutButton) {
            signOut();
        } else if (i == R.id.verifyEmailButton) {
            sendEmailVerification();
        }
    }




    public void createUser(){
        Log.d(TAG, "CREATE USER");

        UserData userData = new UserData("1", "2", "3");
        DocumentReference dogRef = mFireStore
                .collection(getString(R.string.collection_user_data))
                .document();
        dogRef.set(userData);
//        CollectionReference collection = mFireStore.collection("users");

//        DocumentReference docRef = mFireStore.collection("users").document(mAuth.getCurrentUser().getUid());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//
//                    //if the user is already in the database, check the user role
//                    if (document != null && document.exists()) {
//                        UserData user = document.toObject(UserData.class);
////                        if(user.getRoles().get("admin")==true){
////                            Log.d(TAG, "Admin: true");
////                            btn_admin.setVisibility(View.VISIBLE);
////                        }
////
////                        else{
////                            Log.d(TAG, "Admin: false");
////                            btn_admin.setVisibility(View.GONE);
////                        }
//
//                        Log.d(TAG, "*** DocumentSnapshot data: " + document.getData());
//                    } else {
//                        //check whether it's a new user
//                        //if yes, create a new document containing the user details thru my User model
//
//                        Log.d(TAG, "*** No such document");
////                        btn_admin.setVisibility(View.GONE);
////                        Map<String, Boolean> roles = new HashMap<>();
////                        roles.put("editor", false);
////                        roles.put("viewer", false);
////                        roles.put("admin", false);
//                        UserData userData = new UserData(mAuth.getUid(), mAuth.getCurrentUser().getDisplayName(), mEmail);
////                        User user = new User(auth.getCurrentUser().getDisplayName(), auth.getCurrentUser().getUid(),auth.getCurrentUser().getEmail(), roles);
//                        //you can add some action here
//
//                        mFireStore.collection("users").document(mAuth.getCurrentUser().getUid())
//                                .set(userData, SetOptions.merge())
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Log.d(TAG, "*** DocumentSnapshot successfully written!");
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.w(TAG, "*** Error writing document", e);
//                                    }
//                                });
//                        //Log.d(TAG, "Created credential");
//                    }
//                } else {
//                    Log.d(TAG, "** get failed with ", task.getException());
//                }
//            }
//        });
    }
}
