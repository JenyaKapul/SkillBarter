package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ActionBarMenuActivity extends BaseActivity {


    private static final String TAG = "ActionBarMenuActivity";

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "***** onCreate");
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "***** onOptionsItemSelected: ");


        if (this.getClass().getSimpleName().equals(item.getTitleCondensed())) {
            return true;
        }
        Intent intent;

        switch (item.getItemId()) {
            case R.id.app_bar_profile:
                intent = new Intent(this, UserHomeProfile.class);
                break;
            case R.id.app_bar_search:
                intent = new Intent(this, SearchEngine.class);
                break;
            case R.id.menu_edit_profile:
                intent = new Intent(this, RegisterActivity.class);
                intent.putExtra(RegisterActivity.KEY_USER_ID, currentUser.getUid());
                break;
            case R.id.menu_history:
                intent = new Intent(this, History.class);
                break;
            case R.id.menu_manage_skills:
                intent = new Intent(this, SkillsManager.class);
                break;
            case R.id.menu_sign_out:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, EmailPasswordActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }
}
