package com.yagodar.android.bill_please.activity.bill_list;

import android.app.Activity;
import android.os.Bundle;

import com.yagodar.android.bill_please.R;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill_list);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.bill_list_fragment_container, new BillListFragment()).commit();
        }
    }

}