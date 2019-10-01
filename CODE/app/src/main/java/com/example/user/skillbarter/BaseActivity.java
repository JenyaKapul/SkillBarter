package com.example.user.skillbarter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import static com.example.user.skillbarter.Constants.APPOINTMENTS_COLLECTION;
import static com.example.user.skillbarter.Constants.SKILLS_COLLECTION;
import static com.example.user.skillbarter.Constants.USERS_COLLECTION;


public abstract class BaseActivity extends AppCompatActivity {

    protected final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    protected final FirebaseStorage mStorage = FirebaseStorage.getInstance();
    protected final CollectionReference skillsCollection = mFirestore.collection(SKILLS_COLLECTION);
    protected final CollectionReference appointmentsCollection = mFirestore.collection(APPOINTMENTS_COLLECTION);
    protected final CollectionReference usersCollection = mFirestore.collection(USERS_COLLECTION);


    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    @Override
    protected void onResume() {
        super.onResume();
        setupUI(findViewById(R.id.main_layout));
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Problem: hide keyboard when clicked outside of EditText
     * Solution from:
     * https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
     * **/
    public void setupUI(final View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(view);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }


    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String formatTime(int hourOfDay, int minute){
        String time;
        if (hourOfDay < 10){
            time = "0" + hourOfDay + ":";
        }
        else{
            time = hourOfDay + ":";
        }
        if (minute < 10){
            time += "0" + minute;
        }
        else{
            time += minute;
        }
        return time;
    }


    public void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public static int getSkillArrayID(String categoryLabel) {
        int skillArrayID;
        switch (categoryLabel) {
            case "Tutoring":
                skillArrayID = R.array.Tutoring;
                break;
            case "Music":
                skillArrayID = R.array.Music;
                break;
            case "Dance":
                skillArrayID = R.array.Dance;
                break;
            case "Arts and Crafts":
                skillArrayID = R.array.arts_and_crafts;
                break;
            case "Sport":
                skillArrayID = R.array.Sport;
                break;
            case "Household Services":
                skillArrayID = R.array.Household;
                break;
            case "Beauty Care":
                skillArrayID = R.array.Beauty;
                break;
            case "Culinary":
                skillArrayID = R.array.Culinary;
                break;
            default:
                skillArrayID = R.array.Empty;
        }
        return skillArrayID;
    }

    /**
     * Get points represented as dollar signs.
     */
    public static String getPointsString(int pointsInt) {
        switch (pointsInt) {
            case 20:
                return "$";
            case 50:
                return "$$";
            case 100:
                return "$$$";
            default:
                return "$$$";
        }
    }
}

