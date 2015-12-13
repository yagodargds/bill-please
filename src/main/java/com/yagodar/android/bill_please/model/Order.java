package com.yagodar.android.bill_please.model;

import com.yagodar.essential.model.Model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by АППДКт78М on 07.11.2014.
 */
public class Order extends Model {
    public Order(long id) {
        this(id, null, null, null);
    }

    public Order(long id, String name, String cost, String share) {
        super(id, DEF_NAME, name);
        setCost(cost);
        setShare(share);
    }

    public String getFormattedCost() {
        return Bill.DECIMAL_FORMAT.format(mCost);
    }

    public boolean setCost(String costVal) {
        BigDecimal costValNumber = null;
        try {
            costValNumber = new BigDecimal(costVal);
        } catch(NullPointerException ignored) {
        } catch(NumberFormatException ignored) {
        } finally {
            if(costValNumber == null || costValNumber.compareTo(MIN_COST) < 0) {
                costValNumber = MIN_COST;
            } else {
                costValNumber = costValNumber.round(Bill.MATH_CONTEXT);
            }
        }

        boolean changed = false;

        if(mCost == null || costValNumber.compareTo(mCost) != 0) {
            mCost = costValNumber;
            changed = true;
        }

        return changed;
    }

    public String getFormattedShare() {
        return mShare.toString();
    }

    public boolean setShare(String shareVal) {
        BigInteger shareValNumber = null;
        try {
            shareValNumber = new BigInteger(shareVal);
        } catch(NullPointerException ignored) {
        } catch(NumberFormatException ignored) {
        } finally {
            if(shareValNumber == null || shareValNumber.compareTo(MIN_SHARE) < 0) {
                shareValNumber = MIN_SHARE;
            }
        }

        boolean changed = false;

        if(mShare == null || shareValNumber.compareTo(mShare) != 0) {
            mShare = shareValNumber;
            changed = true;
        }

        return changed;
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
