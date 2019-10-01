package com.example.user.skillbarter;

import android.arch.lifecycle.ViewModel;

import com.example.user.skillbarter.search.Filters;

/*
 * ViewModel for {@link com.example.user.skillbarter.SearchResultActivity}.
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