package com.example.user.skillbarter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

public class skillsSearchResultActivity extends ActionBarMenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_search_result);

        FilterSearchResult filterSearchResult = (FilterSearchResult) getIntent().getParcelableExtra("parcel_data");
        Log.d("SearchResults", "***** category: " + filterSearchResult.getCategory());
        Log.d("SearchResults", "***** skill: " + filterSearchResult.getSkill());
        Log.d("SearchResults", "***** minpoints : " + filterSearchResult.getMinPoints());
        Log.d("SearchResults", "***** maxpoints: : " + filterSearchResult.getMaxPoints());

    }
}

//public class NextActivity extends Activity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//// Using getParcelableExtra(String key) method
//        Movie movie = (Movie) getIntent().getParcelableExtra("parcel_data");
//    }
//}