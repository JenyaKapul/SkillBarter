package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class EmailPasswordActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private Spinner mDomainsSpinner;

    private String mUserName;
    private String mEmail;
    private String mPassword;


    private FirebaseAuth mAuth;

    private FirebaseFirestore mFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "***** onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        // Views
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        mDomainsSpinner = findViewById(R.id.domainsSpinner);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.verifyAgainButton).setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mFirestore = FirebaseFirestore.getInstance();

        signOut(); //DEBUG
    }

    @Override
    public void onStart() {
        Log.d(TAG, "***** onStart");
        super.onStart();
        // Check if user is signed in (non-null) and verified by email.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // User is signed in from earlier session.
            showProgressDialog();
            findViewById(R.id.main_layout).setVisibility(View.GONE);
            directLoggedInUser(currentUser);
        } else if (currentUser != null && !currentUser.isEmailVerified()) {
            // User's account has already been created but is unverified
            findViewById(R.id.verifyAgainButton).setVisibility(View.VISIBLE);
        }
    }

    private void createAccount() {
        Log.d(TAG, "***** createAccount");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            findViewById(R.id.emailCreateAccountButton).setEnabled(false);
                            findViewById(R.id.verifyAgainButton).setVisibility(View.VISIBLE);

                            sendEmailVerification();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signIn() {
        Log.d(TAG, "***** signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            // if user is not verified, prevent from logging in and wait for verification
                            if (!user.isEmailVerified()) {
                                Toast.makeText(EmailPasswordActivity.this, "Email not verified",
                                        Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                                return;
                            }
                            directLoggedInUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }

    private void signOut() {
        Log.d(TAG, "***** signOut");
        mAuth.signOut();
    }

    private void sendEmailVerification() {
        Log.d(TAG, "***** sendEmailVerification");

        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

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
                    }
                });
    }

    private boolean validateForm() {
        Log.d(TAG, "***** validateForm");
        boolean valid = true;

        mUserName = mEmailField.getText().toString();
        mPassword = mPasswordField.getText().toString();

        if (TextUtils.isEmpty(mUserName)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }
        String domain = (String) mDomainsSpinner.getSelectedItem();
        mEmail = mUserName + domain;
        Log.d(TAG, "validateForm: " + mEmail);
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void directLoggedInUser(FirebaseUser user) {
        Log.d(TAG, "***** directLoggedInUser");
        Log.d(TAG, "***** directLoggedInUser: user= " + user + " userUid: " + user.getUid());

        DocumentReference userRef = mFirestore.collection(getString(R.string.collection_user_data)).document(user.getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Intent intent;
                    // the user is already in the database
                    if (document != null && document.exists()) {
                        Log.d(TAG, "***** directLoggedInUser: User is already in database");
                        intent = new Intent(EmailPasswordActivity.this, UserHomeProfile.class);
                    } else {
                        // the user is not in the database.
                        // move to registration page
                        Log.d(TAG, "***** directLoggedInUser: User is NOT in database");
                        intent = new Intent(EmailPasswordActivity.this, RegisterActivity.class);
                    }
                    hideProgressDialog();

                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount();
        } else if (i == R.id.emailSignInButton) {
            signIn();
        } else if (i == R.id.verifyAgainButton) {
            Log.d(TAG, "***** onClick: verifyAgainButton");
            sendEmailVerification();
        }
    }
}
