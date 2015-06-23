package com.yagodar.android.bill_please.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by АППДКт78М on 24.10.2014.
 */
public class Bill {
    public Bill(long id) {
        this(id, null, null, null, null, null);
    }

    public Bill(long id, String name, TaxTipType taxType, BigDecimal taxVal, TaxTipType tipType, BigDecimal tipVal) {
        this.id = id;
        billOrderList = new LinkedHashMap<>();
        setName(name);
        setTaxVal(taxType, taxVal);
        setTipVal(tipType, tipVal);
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

    public BigDecimal getTaxVal() {
        return taxVal;
    }

    public String getFormattedTaxVal() {
        return DECIMAL_FORMAT.format(taxVal);
    }

    public BigDecimal getTaxAbs() {
        return getTaxTipAbs(taxType, taxVal);
    }

    public void setTaxVal(TaxTipType taxType, BigDecimal taxVal) {
        if(taxType == null) {
            this.taxType = DEF_TAX_TIP_TYPE;
        } else {
            this.taxType = taxType;
        }

        if(taxVal == null || taxVal.compareTo(MIN_TAX_TIP_VAL) < 0) {
            this.taxVal = MIN_TAX_TIP_VAL;
        } else {
            this.taxVal = taxVal;
        }
    }

    public TaxTipType getTaxType() {
        return taxType;
    }

    public BigDecimal getTipVal() {
        return tipVal;
    }

    public String getFormattedTipVal() {
        return DECIMAL_FORMAT.format(tipVal);
    }

    public BigDecimal getTipAbs() {
        return getTaxTipAbs(tipType, tipVal);
    }

    public void setTipVal(TaxTipType tipType, BigDecimal tipVal) {
        if(tipType == null) {
            this.tipType = DEF_TAX_TIP_TYPE;
        } else {
            this.tipType = tipType;
        }

        if(tipVal == null || tipVal.compareTo(MIN_TAX_TIP_VAL) < 0) {
            this.tipVal = MIN_TAX_TIP_VAL;
        } else {
            this.tipVal = tipVal;
        }
    }

    public TaxTipType getTipType() {
        return tipType;
    }

    private BigDecimal getTaxTipAbs(TaxTipType type, BigDecimal val) {
        switch(type) {
            case ABSOLUTE:
                return val;
            case PERCENT:
            default:
                return getSubtotal().multiply(val.divide(FULL_TAX_TIP_PERCENT_VAL, BIG_VALUES_SCALE, BIG_VALUES_ROUNDING_MODE));
        }
    }

    public void putBillOrder(long id, BillOrder billOrder) {
        if(billOrder == null) {
            throw new IllegalArgumentException("Bill Order must not be null!");
        }

        billOrderList.put(id, billOrder);
    }

    public BillOrder getBillOrder(long id) {
        return billOrderList.get(id);
    }

    public void removeBillOrder(long id) {
        billOrderList.remove(id);
    }

    public Map<Long, BillOrder> getBillOrderList() {
        return billOrderList;
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (BillOrder order : billOrderList.values()) {
            subtotal = subtotal.add(order.getShareCost());
        }

        return subtotal;
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getTaxAbs()).add(getTipAbs());
    }

    public boolean isLoaded() {
        return billOrderList != null;
    }

    public enum TaxTipType {
        ABSOLUTE,
        PERCENT,
        ;
    }

    private long id;
    private String name;
    private TaxTipType taxType;
    private BigDecimal taxVal;
    private TaxTipType tipType;
    private BigDecimal tipVal;

    private Map<Long, BillOrder> billOrderList;

    public static final String BILL_ID_KEY = "bill_id";

    protected static final int BIG_VALUES_MIN_FRACTION_DIGITS = 2;
    protected static final int BIG_VALUES_MAX_FRACTION_DIGITS = 2;
    protected static final int BIG_VALUES_SCALE = BIG_VALUES_MIN_FRACTION_DIGITS * BIG_VALUES_MAX_FRACTION_DIGITS;
    protected static final RoundingMode BIG_VALUES_ROUNDING_MODE = RoundingMode.HALF_UP;
    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();
    static {
        DECIMAL_FORMAT.setMinimumFractionDigits(BIG_VALUES_MIN_FRACTION_DIGITS);
        DECIMAL_FORMAT.setMaximumFractionDigits(BIG_VALUES_MAX_FRACTION_DIGITS);
        DECIMAL_FORMAT.setGroupingUsed(false);

        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        DECIMAL_FORMAT.setDecimalFormatSymbols(custom);
    }

    private static final String DEF_NAME = "bill";
    private static final BigDecimal MIN_TAX_TIP_VAL = BigDecimal.ZERO;
    private static final BigDecimal FULL_TAX_TIP_PERCENT_VAL = BigDecimal.valueOf(100.0);
    private static final TaxTipType DEF_TAX_TIP_TYPE = TaxTipType.PERCENT;
}
