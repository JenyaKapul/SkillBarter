package com.example.user.skillbarter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchEngineActivity extends ActionBarMenuActivity {

	private Spinner categorySpinner, skillSpinner;

    // args for creating a new FilterSearchResult object.
    private String mCategory, mSkill;
    private int mPointsMin, mPointsMax;

    @BindView(R.id.points_min)
    EditText pointsMinView;

    @BindView(R.id.points_max)
    EditText pointsMaxView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_engine);
        ButterKnife.bind(this);

        categorySpinner = findViewById(R.id.category_spinner);
        skillSpinner = findViewById(R.id.skills_spinner);

        List<CharSequence> categories = Arrays.asList(this.getResources().getTextArray(R.array.skills_categories));

        // adapter for displaying hint 'Choose Category' in the first spinner.
        HintAdapter adapter = new HintAdapter(this, categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // show hint.
        categorySpinner.setSelection(adapter.getCount());

        // add listener to the category spinner in order to load the correct data for the skills spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySpinnerChooser(parent, view, position, id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        skillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                skillSpinnerChooser(parent, view, position, id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void categorySpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        String categoryLabel = parent.getItemAtPosition(position).toString();

        int skillArrayID = getSkillArrayID(categoryLabel);

        if (skillArrayID != R.array.Empty) {
            skillSpinner.setEnabled(true);
            mCategory = categoryLabel;
        } else {
            skillSpinner.setEnabled(false);
        }

        List<CharSequence> skillsList = Arrays.asList(this.getResources().getTextArray(skillArrayID));

        HintAdapter adapter = new HintAdapter(this, skillsList,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        skillSpinner.setAdapter(adapter);

        // show hint.
        skillSpinner.setSelection(adapter.getCount());
    }


    private void skillSpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        String skillLabel = parent.getItemAtPosition(position).toString();
        if (!skillLabel.startsWith("Choose")) {
            mSkill = skillLabel;
        }
    }

    @OnClick(R.id.search_button)
    public void onSearchClicked() {
        String minPointsStr = pointsMinView.getText().toString();
        if (TextUtils.isEmpty(minPointsStr)) {
            mPointsMin = 0;
        } else {
            mPointsMin = Integer.parseInt(minPointsStr);
        }

        String maxPointsStr = pointsMaxView.getText().toString();
        if (TextUtils.isEmpty(maxPointsStr)) {
            mPointsMax = Integer.MAX_VALUE;
        } else {
            mPointsMax = Integer.parseInt(maxPointsStr);
        }

        FilterSearchResult filterSearchResult = new FilterSearchResult(mCategory, mSkill, mPointsMin, mPointsMax);

        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("parcel_data", filterSearchResult);
        startActivity(intent);
    }
}