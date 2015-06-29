package com.yagodar.android.bill_please.activity.bill;

import android.app.Activity;
import android.os.Bundle;

import com.yagodar.android.bill_please.R;

/**
 * Created by yagodar on 23.06.2015.
 */
public class BillActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill);

        if (savedInstanceState == null) {
            billFragment = new BillFragment();
            billFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.bill_order_list_fragment_container, billFragment).commit();
        } else {
            billFragment = (BillFragment) getFragmentManager().findFragmentById(R.id.bill_order_list_fragment_container);
        }

        //getActionBar().setDisplayHomeAsUpEnabled(true);
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
