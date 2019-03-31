package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

enum ActivityName {
    PROFILE_HOME, SEARCH, EDIT_PROFILE, HISTORY, SKILLS_MANAGER
}

public class ActionBarMenuActivity extends AppCompatActivity {
    private static ActivityName currActivity = ActivityName.PROFILE_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        switch (item.getItemId()) {
            case R.id.app_bar_profile:
                //When current activity is not an user home profile activity - we would want to start it
                if (!(ActionBarMenuActivity.currActivity == ActivityName.PROFILE_HOME)){
                    ActionBarMenuActivity.currActivity = ActivityName.PROFILE_HOME;
                    Intent intent = new Intent(ActionBarMenuActivity.this, UserHomeProfile.class);
                    startActivity(intent);
                }
                return true;
            case R.id.app_bar_search:
                //When current activity is not a search engine activity - we would want to start it
                if (!(ActionBarMenuActivity.currActivity == ActivityName.SEARCH)){
                    ActionBarMenuActivity.currActivity = ActivityName.SEARCH;
                    Intent intent = new Intent(ActionBarMenuActivity.this, SearchEngine.class);
                    startActivity(intent);
                }
                return true;
            case R.id.menu_edit_profile:
                //When current activity is not a RegisterActivity activity - we would want to start it
                if (!(ActionBarMenuActivity.currActivity == ActivityName.EDIT_PROFILE)){
                    ActionBarMenuActivity.currActivity = ActivityName.EDIT_PROFILE;
                    Intent intent = new Intent(ActionBarMenuActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.menu_history:
                //When current activity is not a history activity - we would want to start it
                if (!(ActionBarMenuActivity.currActivity == ActivityName.HISTORY)){
                    ActionBarMenuActivity.currActivity = ActivityName.HISTORY;
                    Intent intent = new Intent(ActionBarMenuActivity.this, History.class);
                    startActivity(intent);
                }
                return true;
            case R.id.menu_manage_skills:
                //When current activity is not a skillsManager activity - we would want to start it
                if (!(ActionBarMenuActivity.currActivity == ActivityName.SKILLS_MANAGER)){
                    ActionBarMenuActivity.currActivity = ActivityName.SKILLS_MANAGER;
                    Intent intent = new Intent(ActionBarMenuActivity.this, SkillsManager.class);
                    startActivity(intent);
                }
                return true;
            case R.id.menu_sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ActionBarMenuActivity.this, EmailPasswordActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
