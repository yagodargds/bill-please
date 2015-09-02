package com.yagodar.android.bill_please.activity.bill_list;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yagodar.android.bill_please.R;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill_list);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.bill_list_fragment_container, new BillListFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_rest_activity_bill_list, menu);

        MenuItem menuItem = menu.findItem(R.id.action_rest_activiry_bill_list_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setMaxWidth(Integer.MAX_VALUE);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo =  searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);

        return super.onCreateOptionsMenu(menu);
    }
}