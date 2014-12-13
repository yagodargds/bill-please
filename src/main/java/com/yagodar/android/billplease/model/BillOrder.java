package com.yagodar.android.billplease.model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by АППДКт78М on 07.11.2014.
 */
public class BillOrder {
    public BillOrder(String name, BigInteger share, BigDecimal cost) {
        this.name = name;
        this.share = share;
        this.cost = cost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setShare(BigInteger share) {
        this.share = share;
    }

    public BigInteger getShare() {
        return share;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getShareCost() {
        return cost.divide(new BigDecimal(share), BillModel.BIG_VALUES_SCALE, BillModel.BIG_VALUES_ROUNDING_MODE);
    }

    private String name;
    private BigInteger share;
    private BigDecimal cost;
}
