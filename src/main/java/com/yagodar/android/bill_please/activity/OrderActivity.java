package com.yagodar.android.bill_please.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yagodar.android.bill_please.R;

/**
 * Created by yagodar on 30.06.2015.
 */
public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill_order);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

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
