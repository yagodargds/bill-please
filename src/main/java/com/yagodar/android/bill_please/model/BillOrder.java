package com.yagodar.android.bill_please.model;

import com.yagodar.essential.model.Model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by АППДКт78М on 07.11.2014.
 */
public class BillOrder extends Model {
    public BillOrder(long id) {
        this(id, null, null, null);
    }

    public BillOrder(long id, String name, String cost, String share) {
        super(id, DEF_NAME, name);
        setCost(cost);
        setShare(share);
    }

    public String getFormattedCost() {
        return Bill.DECIMAL_FORMAT.format(mCost);
    }

    public void setCost(String cost) {
        if(mCost == null) {
            mCost = MIN_COST;
        } else {
            mCost = new BigDecimal(cost);
            if(mCost.compareTo(MIN_COST) < 0) {
                mCost = MIN_COST;
            }
        }
    }

    public String getFormattedShare() {
        return Bill.DECIMAL_FORMAT.format(mShare);
    }

    public void setShare(String share) {
        if(share == null) {
            mShare = MIN_SHARE;
        } else {
            mShare = new BigInteger(share);
            if(mShare.compareTo(MIN_SHARE) < 0) {
                mShare = MIN_SHARE;
            }
        }
    }

    public BigDecimal getSubtotal() {
        return mCost.divide(new BigDecimal(mShare), Bill.BIG_VALUES_SCALE, Bill.BIG_VALUES_ROUNDING_MODE);
    }

    public String getFormattedSubtotal() {
        return Bill.DECIMAL_FORMAT.format(getSubtotal());
    }

    private BigDecimal mCost;
    private BigInteger mShare;

    private static final String DEF_NAME = "order";
    private static final BigInteger MIN_SHARE = BigInteger.ONE;
    private static final BigDecimal MIN_COST = BigDecimal.ZERO;
}
