package com.yagodar.android.bill_please.activity.bill_order;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.yagodar.android.bill_please.R;

/**
 * Created by yagodar on 30.06.2015.
 */
public class BillOrderActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill_order);

        if (savedInstanceState == null) {
            billOrderFragment = new BillOrderFragment();
            billOrderFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.bill_order_fragment_container, billOrderFragment).commit();
        } else {
            billOrderFragment = (BillOrderFragment) getSupportFragmentManager().findFragmentById(R.id.bill_order_fragment_container);
        }
    }

    @Override
    public void onBackPressed() {
        if(!billOrderFragment.onActivityBackPressed()) {
            super.onBackPressed();
        }
    }

    private BillOrderFragment billOrderFragment;
}
