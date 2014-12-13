package com.yagodar.android.billplease.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * Created by АППДКт78М on 24.10.2014.
 */
public class BillModel {
    public BillModel() {
        billOrderList = new HashMap<>();

        setDefOrderName(DEF_ORDER_NAME);
        setDefOrderShare(DEF_ORDER_SHARE);
        setDefOrderCost(DEF_ORDER_COST);

        setTaxVal(DEF_TAX_TYPE, DEF_TAX_VAL);
        setTipVal(DEF_TIP_TYPE, DEF_TIP_VAL);
    }

    public void addBillOrder(long id) {
        addBillOrder(id, defOrderName, defOrderShare, defOrderCost);
    }

    public void addBillOrder(long id, String name, BigInteger share, BigDecimal cost) {
        billOrderList.put(id, new BillOrder(name, share, cost));
    }

    public void setBillOrderName(long id, String name) {
        BillOrder order = getBillOrder(id);
        
        if(order != null) {
            order.setName(name); 
        }
    }

    public void setBillOrderShare(long id, BigInteger share) {
        BillOrder order = getBillOrder(id);

        if(order != null) {
            order.setShare(share);
        }
    }

    public void setBillOrderCost(long id, BigDecimal cost) {
        BillOrder order = getBillOrder(id);

        if(order != null) {
            order.setCost(cost);
        }
    }

    public BillOrder getBillOrder(long id) {
        return billOrderList.get(id);
    }

    public void delBillOrder(long id) {
        billOrderList.remove(id);
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.valueOf(0.0);

        for (BillOrder order : billOrderList.values()) {
            subtotal = subtotal.add(order.getShareCost());
        }

        return subtotal;
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getTaxAbs()).add(getTipAbs());
    }

    public void setDefOrderName(String name) {
        defOrderName = name;
    }

    public void setDefOrderShare(BigInteger share) {
        defOrderShare = share;
    }

    public void setDefOrderCost(BigDecimal cost) {
        defOrderCost = cost;
    }

    public void setTaxVal(TaxTipType type, BigDecimal val) {
        taxType = type;
        taxVal = val;
    }

    public BigDecimal getTaxVal() {
        return taxVal;
    }

    public TaxTipType getTaxType() {
        return taxType;
    }

    public BigDecimal getTaxAbs() {
        return getTaxTipAbs(taxType, taxVal);
    }

    public void setTipVal(TaxTipType type, BigDecimal val) {
        tipType = type;
        tipVal = val;
    }

    public BigDecimal getTipVal() {
        return tipVal;
    }

    public TaxTipType getTipType() {
        return tipType;
    }

    public BigDecimal getTipAbs() {
        return getTaxTipAbs(tipType, tipVal);
    }

    private BigDecimal getTaxTipAbs(TaxTipType type, BigDecimal val) {
        switch(type) {
            case ABSOLUTE:
                return val;
            case PERCENT:
            default:
                return getSubtotal().multiply(val.divide(FULL_PERCENT, BIG_VALUES_SCALE, BIG_VALUES_ROUNDING_MODE));
        }
    }

    public enum TaxTipType {
        ABSOLUTE,
        PERCENT,
        ;
    }

    private final HashMap<Long, BillOrder> billOrderList;

    private BigDecimal taxVal;
    private TaxTipType taxType;
    private BigDecimal tipVal;
    private TaxTipType tipType;

    private String defOrderName;
    private BigInteger defOrderShare;
    private BigDecimal defOrderCost;

    protected static final int BIG_VALUES_MIN_FRACTION_DIGITS = 2;
    protected static final int BIG_VALUES_MAX_FRACTION_DIGITS = 2;
    protected static final int BIG_VALUES_SCALE = BIG_VALUES_MIN_FRACTION_DIGITS * BIG_VALUES_MAX_FRACTION_DIGITS;
    protected static final RoundingMode BIG_VALUES_ROUNDING_MODE = RoundingMode.HALF_UP;

    private static final String DEF_ORDER_NAME = "item";
    private static final BigInteger DEF_ORDER_SHARE = BigInteger.valueOf(1);
    private static final BigDecimal DEF_ORDER_COST = BigDecimal.valueOf(0.0);

    private static final BigDecimal DEF_TAX_VAL = BigDecimal.valueOf(0.0);
    private static final TaxTipType DEF_TAX_TYPE = TaxTipType.PERCENT;
    private static final BigDecimal DEF_TIP_VAL = BigDecimal.valueOf(10.0);
    private static final TaxTipType DEF_TIP_TYPE = TaxTipType.PERCENT;

    private static final BigDecimal FULL_PERCENT = BigDecimal.valueOf(100.0);
}
