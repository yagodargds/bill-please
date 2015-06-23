package com.yagodar.android.bill_please.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillList {
    public BillList() {}

    public static BillList getInstance() {
        return INSTANCE;
    }

    public synchronized List<Bill> getBillList() {
        return billList;
    }

    public synchronized void setBillList(List<Bill> billList) {
        if(billList == null) {
            throw new IllegalArgumentException("Bill list must not be null!");
        }

        this.billList = billList;

        billIdList = new LinkedList<>();
        for(Bill bill : this.billList) {
            billIdList.add(bill.getId());
        }
    }

    public synchronized int getCount() {
        return billList.size();
    }

    public synchronized void putBill(Bill bill) {
        if(bill == null) {
            throw new IllegalArgumentException("Bill must not be null!");
        }

        long id = bill.getId();
        int pos = billIdList.indexOf(id);
        if(pos != -1) {
            billList.set(pos, bill);
        } else {
            billList.add(bill);
            billIdList.add(id);
        }
    }

    public synchronized Bill getBill(long id) {
        int pos = billIdList.indexOf(id);
        if(pos != -1) {
            return billList.get(pos);
        } else {
            return null;
        }
    }

    public synchronized Bill getBill(int pos) {
        return billList.get(pos);
    }

    public synchronized boolean removeBill(long id) {
        int pos = billIdList.indexOf(id);
        if(pos != -1) {
            billList.remove(pos);
            billIdList.remove(pos);
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean isLoaded() {
        return billList != null;
    }

    private List<Bill> billList;
    private List<Long> billIdList;

    private static final BillList INSTANCE = new BillList();
}
