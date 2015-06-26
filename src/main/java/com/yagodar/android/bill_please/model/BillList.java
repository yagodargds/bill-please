package com.yagodar.android.bill_please.model;

import com.yagodar.essential.model.ConcurrentListModel;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillList extends ConcurrentListModel<Bill> {

    public BillList() {
        super(0L, "bill_list");
    }

    public static BillList getInstance() {
        return INSTANCE;
    }


    private static final BillList INSTANCE = new BillList();
}
