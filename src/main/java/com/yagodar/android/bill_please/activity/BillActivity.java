package com.yagodar.android.bill_please.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yagodar.android.bill_please.R;

/**
 * Created by yagodar on 23.06.2015.
 */
public class BillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill);

        setSupportActionBar((Toolbar) findViewById(R.id.tool_bar));

        if (savedInstanceState == null) {
            billFragment = new BillFragment();
            billFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.bill_order_list_fragment_container, billFragment).commit();
        } else {
            billFragment = (BillFragment) getSupportFragmentManager().findFragmentById(R.id.bill_order_list_fragment_container);
        }

        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if(!billFragment.onActivityBackPressed()) {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }*/

    private BillFragment billFragment;
}
