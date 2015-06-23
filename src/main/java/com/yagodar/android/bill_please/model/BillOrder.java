package com.yagodar.android.bill_please.model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by АППДКт78М on 07.11.2014.
 */
public class BillOrder {
    public BillOrder(long id) {
        this(id, null, null, null);
    }

    public BillOrder(long id, String name, BigDecimal cost, BigInteger share) {
        this.id = id;
        setName(name);
        setCost(cost);
        setShare(share);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null || name.isEmpty()) {
            this.name = DEF_NAME + "#" + this.id;
        } else {
            this.name = name;
        }
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        if(cost == null || cost.compareTo(MIN_COST) < 0) {
            this.cost = MIN_COST;
        } else {
            this.cost = cost;
        }
    }

    public BigInteger getShare() {
        return share;
    }

    public void setShare(BigInteger share) {
        if(share == null || share.compareTo(MIN_SHARE) < 0) {
            this.share = MIN_SHARE;
        } else {
            this.share = share;
        }
    }

    public BigDecimal getShareCost() {
        return cost.divide(new BigDecimal(share), Bill.BIG_VALUES_SCALE, Bill.BIG_VALUES_ROUNDING_MODE);
    }

    private long id;
    private String name;
    private BigDecimal cost;
    private BigInteger share;

    public static final String DEF_NAME = "item";
    public static final BigInteger MIN_SHARE = BigInteger.ONE;
    public static final BigDecimal MIN_COST = BigDecimal.ZERO;
}
