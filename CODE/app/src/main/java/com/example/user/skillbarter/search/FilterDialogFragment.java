package com.example.user.skillbarter.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.user.skillbarter.BaseActivity;
import com.example.user.skillbarter.R;
import com.example.user.skillbarter.adapters.HintAdapter;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.user.skillbarter.BaseActivity.getSkillArrayID;
import static com.example.user.skillbarter.search.Filters.CATEGORY;


/*
 * Dialog Fragment containing filter form.
 */
public class FilterDialogFragment extends DialogFragment {

    public static final String TAG = "FilterDialog";

    public interface FilterListener {

        void onFilter(Filters filters);

    }

    /* TODO:
        (1) set skill spinner after choosing category
        (2) filter by points (max and min value)
        (3) Fix the height of the skills spinner which gets changed when choosing a category (maybe padding?)

     */

    private View mRootView;

    @BindView(R.id.spinner_category)
    Spinner mCategorySpinner;

    @BindView(R.id.spinner_skill)
    Spinner mSkillSpinner;

    @BindView(R.id.spinner_sort)
    Spinner mSortSpinner;

    private FilterListener mFilterListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false);
        ButterKnife.bind(this, mRootView);

        /* Listen to the category spinner in order to load the correct data for the skills spinner. */
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySpinnerChooser(parent, view, position, id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return mRootView;
    }

    void categorySpinnerChooser(AdapterView<?> parent, View view, int position, long id) {
        String categoryLabel = parent.getItemAtPosition(position).toString();

        int skillArrayID = getSkillArrayID(categoryLabel);

        if (skillArrayID != R.array.Empty) {
            mSkillSpinner.setEnabled(true);

            List<CharSequence> skillsList = Arrays.asList(this.getResources().getTextArray(skillArrayID));

            HintAdapter adapter = new HintAdapter(getActivity(), skillsList,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSkillSpinner.setPaddingRelative(0, 0, 0, 56);
            mSkillSpinner.setAdapter(adapter);

            // show hint.
            mSkillSpinner.setSelection(adapter.getCount());
        } else {
            mSkillSpinner.setEnabled(false);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @OnClick(R.id.button_apply)
    public void onApplyClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }
        dismiss();
    }


    @OnClick(R.id.button_cancel)
    public void onCancelClicked() {
        dismiss();
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if (mRootView != null) {
            filters.setCategory(getSelectedCategory());
            filters.setSkill(getSelectedSkill());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }
        return filters;
    }


    @Nullable
    private String getSelectedCategory() {
        String selected = (String) mCategorySpinner.getSelectedItem();
        if (getString(R.string.value_any_category).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }


    @Nullable
    private String getSelectedSkill() {
        String selected = (String) mSkillSpinner.getSelectedItem();
        if (getString(R.string.value_any_skill).equals(selected)) { //TODO!!!
            return null;
        } else {
            return selected;
        }
    }


    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();

        if (getString(R.string.sort_by_category).equals(selected)) {
            return CATEGORY;
        }
        return null;
    }


    @Nullable
    private Query.Direction getSortDirection() {
        return Query.Direction.ASCENDING;
    }
}
