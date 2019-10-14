package com.example.user.skillbarter.search;

import android.arch.lifecycle.ViewModel;


/*
 * ViewModel for {@link com.example.user.skillbarter.search.SearchSkillsActivity}.
 */

public class SearchResultActivityViewModel extends ViewModel {

    private Filters mFilters;

    public SearchResultActivityViewModel() {
        mFilters = Filters.getDefault();
    }

    public Filters getFilters() {
        return mFilters;
    }

    public void setFilters(Filters mFilters) {
        this.mFilters = mFilters;
    }
}