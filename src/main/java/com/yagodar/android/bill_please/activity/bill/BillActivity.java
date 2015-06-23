package com.yagodar.android.bill_please.activity.bill;

import android.app.Activity;
import android.os.Bundle;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.bill_list.BillListFragment;

/**
 * Created by yagodar on 23.06.2015.
 */
public class BillActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.bill_order_list_fragment_container, new BillListFragment()).commit();
        }
    }
}
