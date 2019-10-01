package com.example.user.skillbarter.search;

import android.content.Context;
import android.text.TextUtils;

import com.example.user.skillbarter.BaseActivity;
import com.example.user.skillbarter.R;
import com.google.firebase.firestore.Query;

/*
 * Object for passing filters around.
 */
public class Filters {

    /*
     * TODO:
     *  (1) In case filtered by price, disable "Sort by..." spinner and set its label to "Sort by Points"
     *
     */

    public static final String CATEGORY = "category";
    public static final String SKILL = "skill";
    public static final String POINTS = "pointsValue";
    public static final String LEVEL = "level";
    public static final String ENABLED = "enabled";


    private String category = null;
    private String skill = null;
    private String sortBy = null;
    private int points = -1;
    private Query.Direction sortDirection = null;

    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortBy(POINTS);
        filters.setSortDirection(Query.Direction.ASCENDING);

        return filters;
    }

    public boolean hasCategory() {
        return !(TextUtils.isEmpty(category));
    }

    public boolean hasSkill() {
        if (skill != null && !skill.startsWith("Choose")) {
            return true;
        }
        return false;
    }

    public boolean hasPoints() {
        return (points > 0);
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (category == null && !hasSkill()) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_categories));
            desc.append("</b>");
        }

        if (category != null) {
            desc.append("<b>");
            desc.append(category);
            desc.append("</b>");
        }

        if (category != null && hasSkill()) {
            desc.append(" , ");
        }

        if (hasSkill()) {
            desc.append("<b>");
            desc.append(skill);
            desc.append("</b>");
        }

        if (points > 0) {
            desc.append(" for ");
            desc.append("<b>");
            desc.append(BaseActivity.getPointsString(points));
            desc.append("</b>");
        }

        return desc.toString();
    }


    public String getOrderDescription(Context context) {

        if (CATEGORY.equals(sortBy)) {
            return context.getString(R.string.sorted_by_category);
        } else if (POINTS.equals(sortBy)){
            return context.getString(R.string.sorted_by_points);
        } else {
            return context.getString(R.string.sorted_by_level);
        }
    }
}

