package com.yagodar.android.bill_please.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.yagodar.android.bill_please.R;

/**
 * Created by yagodar on 30.06.2015.
 */
public class OrderActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill_order);

        if (savedInstanceState == null) {
            orderFragment = new OrderFragment();
            orderFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.bill_order_fragment_container, orderFragment).commit();
        } else {
            orderFragment = (OrderFragment) getSupportFragmentManager().findFragmentById(R.id.bill_order_fragment_container);
        }
    }

    @Override
    public void onBackPressed() {
        if(!orderFragment.onActivityBackPressed()) {
            super.onBackPressed();
        }
    }

    private OrderFragment orderFragment;
}
