package com.yagodar.android.bill_please.model;

import com.yagodar.essential.model.ListModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by АППДКт78М on 24.10.2014.
 */
public class Bill extends ListModel<BillOrder> {
    public Bill(long id) {
        this(id, null, null, null, null, null);
    }

    public Bill(long id, String name, TaxTipType taxType, String taxVal, TaxTipType tipType, String tipVal) {
        super(id, DEF_NAME, name);
        setTaxVal(taxType, taxVal);
        setTipVal(tipType, tipVal);
    }

    public String getFormattedTaxVal() {
        return DECIMAL_FORMAT.format(mTaxVal);
    }

    public BigDecimal getTaxVal(TaxTipType intentType) {
        return getTaxTipVal(intentType, mTaxType, mTaxVal);
    }

    public String getFormattedTaxVal(TaxTipType intentType) {
        return DECIMAL_FORMAT.format(getTaxTipVal(intentType, mTaxType, mTaxVal));
    }

    public void setTaxVal(TaxTipType taxType, String taxVal) {
        if(taxType == null) {
            mTaxType = DEF_TAX_TIP_TYPE;
        } else {
            mTaxType = taxType;
        }

        if(taxVal == null) {
            mTaxVal = MIN_TAX_TIP_VAL;
        } else {
            mTaxVal = new BigDecimal(taxVal);
            if(mTaxVal.compareTo(MIN_TAX_TIP_VAL) < 0) {
                mTaxVal = MIN_TAX_TIP_VAL;
            }
        }
    }

    public TaxTipType getTaxType() {
        return mTaxType;
    }

    public String getFormattedTipVal() {
        return DECIMAL_FORMAT.format(mTipVal);
    }

    public BigDecimal getTipVal(TaxTipType intentType) {
        return getTaxTipVal(intentType, mTipType, mTipVal);
    }

    public String getFormattedTipVal(TaxTipType intentType) {
        return DECIMAL_FORMAT.format(getTaxTipVal(intentType, mTipType, mTipVal));
    }

    public void setTipVal(TaxTipType tipType, String tipVal) {
        if(tipType == null) {
            mTipType = DEF_TAX_TIP_TYPE;
        } else {
            mTipType = tipType;
        }

        if(tipVal == null) {
            mTipVal = MIN_TAX_TIP_VAL;
        } else {
            mTipVal = new BigDecimal(tipVal);
            if(mTipVal.compareTo(MIN_TAX_TIP_VAL) < 0) {
                mTipVal = MIN_TAX_TIP_VAL;
            }
        }
    }

    public TaxTipType getTipType() {
        return mTipType;
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;

        if(isLoaded()) {
            for (BillOrder order : getModelList()) {
                subtotal = subtotal.add(order.getSubtotal());
            }
        }

        return subtotal;
    }

    public String getFormattedSubtotal() {
        return DECIMAL_FORMAT.format(getSubtotal());
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getTaxVal(TaxTipType.ABSOLUTE)).add(getTipVal(TaxTipType.ABSOLUTE));
    }

    public String getFormattedTotal() {
        return DECIMAL_FORMAT.format(getTotal());
    }

    private BigDecimal getTaxTipVal(TaxTipType intentType, TaxTipType type, BigDecimal val) {
        if(intentType == type) {
            return val;
        }

        switch(intentType) {
            case ABSOLUTE:
                return getSubtotal().multiply(val).divide(FULL_TAX_TIP_PERCENT_VAL, BIG_VALUES_SCALE, BIG_VALUES_ROUNDING_MODE);
            case PERCENT:
                return val.multiply(FULL_TAX_TIP_PERCENT_VAL).divide(getSubtotal(), BIG_VALUES_SCALE, BIG_VALUES_ROUNDING_MODE);
        }

        return MIN_TAX_TIP_VAL;
    }

    public enum TaxTipType {
        ABSOLUTE,
        PERCENT,
        ;
    }

    private TaxTipType mTaxType;
    private BigDecimal mTaxVal;
    private TaxTipType mTipType;
    private BigDecimal mTipVal;

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
