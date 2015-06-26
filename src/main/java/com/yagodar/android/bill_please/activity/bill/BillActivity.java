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

        BillFragment billFragment = new BillFragment();
        billFragment.setArguments(getIntent().getExtras());

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.bill_order_list_fragment_container, billFragment).commit();
        }

        //getActionBar().setDisplayHomeAsUpEnabled(true);
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
}
