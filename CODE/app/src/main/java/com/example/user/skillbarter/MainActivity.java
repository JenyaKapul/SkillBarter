package com.example.user.skillbarter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, UserHomeProfile.class);
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
