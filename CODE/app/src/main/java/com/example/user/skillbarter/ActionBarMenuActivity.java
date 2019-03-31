package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

enum ActivityName {
    PROFILE_HOME, SEARCH, EDIT_PROFILE, HISTORY, SKILL_MANAGER
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
                Toast.makeText(this, "menu_edit_profile", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_history:
                Toast.makeText(this, "menu_history", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_manage_skills:
                Toast.makeText(this, "menu_manage_skills", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_sign_out:
                Toast.makeText(this, "menu_sign_out", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
