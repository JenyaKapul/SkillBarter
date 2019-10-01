package com.example.user.skillbarter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.example.user.skillbarter.adapters.SearchResultAdapter;
import com.example.user.skillbarter.models.UserSkill;
import com.example.user.skillbarter.search.FilterDialogFragment;
import com.example.user.skillbarter.search.Filters;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.user.skillbarter.search.Filters.CATEGORY;
import static com.example.user.skillbarter.search.Filters.ENABLED;
import static com.example.user.skillbarter.search.Filters.POINTS;
import static com.example.user.skillbarter.search.Filters.SKILL;

public class SearchResultActivity extends ActionBarMenuActivity
        implements FilterDialogFragment.FilterListener, EventListener<QuerySnapshot> {

    @BindView(R.id.text_current_search)
    TextView mCurrentSearchView;

    @BindView(R.id.text_current_sort_by)
    TextView mCurrentSortByView;

    @BindView(R.id.search_result_recycler_view)
    RecyclerView mSearchResultRecycler;

    private static SearchResultAdapter mAdapter;

    private Query mQuery;
    private ListenerRegistration mRegistration;

    private static final int LIMIT = 50;

    private SearchResultActivityViewModel mViewModel;

    private FilterDialogFragment mFilterDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_search_result);
        ButterKnife.bind(this);
        setTitle(R.string.search_result_title);

        /* View model */
        mViewModel = ViewModelProviders.of(this).get(SearchResultActivityViewModel.class);

        initRecyclerView();

        /* Filter Dialog */
        mFilterDialog = new FilterDialogFragment();
    }

    private void initRecyclerView() {
        mSearchResultRecycler.setLayoutManager(new LinearLayoutManager(this));
        /* Query the categories to be displayed in the search results screen. */
        Query query = skillsCollection.orderBy(CATEGORY, Query.Direction.ASCENDING).limit(LIMIT);

        setQuery(query);
    }

    @Override
    public void onStart() {
        super.onStart();
        hideProgressDialog();

        if (mAdapter != null) {
            mAdapter.startListening();
        }

        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }

        /* Apply filters */
        onFilter(mViewModel.getFilters());
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }

        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @OnClick(R.id.filter_bar)
    public void onFilterClicked() {
        /* Show the dialog containing filter options. */
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }


    @OnClick(R.id.button_clear_filter)
    public void onClearFilterClicked() {
        /* Clear all spinners' states to display 'All...' */
        mFilterDialog = new FilterDialogFragment();

        onFilter(Filters.getDefault());
    }


    /*
     * TODO:
     *  (1) Check if there's an option to filter out current user's skills.
     */
    @Override
    public void onFilter(Filters filters) {

        // Construct query basic query
        Query query = skillsCollection;

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo(CATEGORY, filters.getCategory());
        }

        // Skill (equality filter)
        if (filters.hasSkill()) {
            query = query.whereEqualTo(SKILL, filters.getSkill());
        }

        // Points (equality filter)
        if (filters.hasPoints()) {
            query = query.whereLessThanOrEqualTo("pointsValue", filters.getPoints());

            // In case of an inequality where filter (whereLessThan(), whereGreaterThan(), etc.)
            // on field 'pointsValue', the query must also have 'pointsValue' as the first orderBy() field
            query = query.orderBy(POINTS, filters.getSortDirection());
        }

        // Non-hidden (enabled) Skills
        query = query.whereEqualTo(ENABLED, true);

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        /* Limit items */
        query = query.limit(LIMIT);


        /* Update the query */
        if(mAdapter != null) {
            setQuery(query);
        }

        /* Set header */
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this)));
        mCurrentSortByView.setText(filters.getOrderDescription(this));

        /* Save filters */
        mViewModel.setFilters(filters);
    }

    void setQuery(Query query) {
        mQuery = query;
        FirestoreRecyclerOptions<UserSkill> options = new FirestoreRecyclerOptions.Builder<UserSkill>()
                .setQuery(mQuery, UserSkill.class)
                .build();

        if (mAdapter != null) {
            mAdapter.stopListening();
        }

        mAdapter = new SearchResultAdapter(options, false);
        mSearchResultRecycler.setAdapter(mAdapter);
        mAdapter.startListening();

        mAdapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // Pass skill document id to the next (skill details) activity.
                String docID = documentSnapshot.getId();
                Intent intent = new Intent(SearchResultActivity.this, SearchItemDetailsActivity.class);
                intent.putExtra(SearchItemDetailsActivity.KEY_SKILL_ID, docID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
        // Handle errors
        if (e != null) {
            Log.w("onEvent:error", e);
            return;
        }

        /* Apply filters to reload data */
        onFilter(mViewModel.getFilters());
    }

}
