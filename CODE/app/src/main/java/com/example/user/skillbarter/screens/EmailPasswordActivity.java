package com.example.user.skillbarter.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.skillbarter.BaseActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.RegisterActivity;
import com.example.user.skillbarter.UserHomeProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EmailPasswordActivity extends BaseActivity {

    @BindView(R.id.fieldEmail)
    EditText mEmailField;

    @BindView(R.id.fieldPassword)
    EditText mPasswordField;

    @BindView(R.id.domainsSpinner)
    Spinner mDomainsSpinner;

    @BindView(R.id.main_layout)
    LinearLayout mMainLayout;

    @BindView(R.id.verifyAccountTextView)
    TextView verifyAccountTextView;

    @BindView(R.id.emailCreateAccountButton)
    Button createAccountButton;


    private String mEmail;
    private String mPassword;
    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);
        ButterKnife.bind(this);

        setTitle(R.string.email_password_title);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and verified by email.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null && currentUser.isEmailVerified()) {
            // User is signed in from earlier session.
            showProgressDialog();
            mMainLayout.setVisibility(View.GONE);

            directLoggedInUser(currentUser);

        } else if (currentUser != null && !currentUser.isEmailVerified()) {
            // User's account has already been created but is unverified
            verifyAccountTextView.setVisibility(View.VISIBLE);
        }
    }


    public void onCreateAccountClicked(View v) {
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
                            createAccountButton.setEnabled(false);
                            verifyAccountTextView.setVisibility(View.VISIBLE);
                            onVerifyAccountClicked();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }


    public void onSignInClicked(View v) {
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
                            FirebaseUser user = mAuth.getCurrentUser();

                            // if user is not verified, prevent from logging in and wait for verification
                            if (user != null && !user.isEmailVerified()) {
                                Toast.makeText(EmailPasswordActivity.this, "Email not verified.",
                                        Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                                return;
                            }

                            directLoggedInUser(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }


    @OnClick(R.id.verifyAccountTextView)
    public void onVerifyAccountClicked() {

        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EmailPasswordActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EmailPasswordActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private String parseEmail() {
        String userName = mEmailField.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            return userName;
        } else {
            String domain = (String) mDomainsSpinner.getSelectedItem();
            return userName.concat(domain);
        }
    }


    private boolean validateForm() {
        boolean valid = true;

        mEmail = parseEmail();
        if (TextUtils.isEmpty(mEmail)) {
            mEmailField.setError("Required.");
            valid = false;
        }

        mPassword = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordField.setError("Required.");
            valid = false;
        }

        return valid;
    }


    private void directLoggedInUser(FirebaseUser user) {

        DocumentReference userRef = mFirestore.collection(getString(R.string.collection_user_data))
                .document(user.getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Intent intent;
                    if (document != null && document.exists()) {
                        // the user is already in the database
                        intent = new Intent(EmailPasswordActivity.this, UserHomeProfile.class);
                    } else {
                        // the user is not in the database.
                        // move to registration page
                        intent = new Intent(EmailPasswordActivity.this, RegisterActivity.class);
                    }
                    hideProgressDialog();
                    startActivity(intent);
                }
            }
        });
    }
}
