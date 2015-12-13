package com.yagodar.android.bill_please.model;

import com.yagodar.essential.model.ConcurrentListModel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by АППДКт78М on 24.10.2014.
 */
public class Bill extends ConcurrentListModel<Order> {
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

    public boolean setTaxVal(TaxTipType taxType, String taxVal) {
        if(taxType == null) {
            taxType = DEF_TAX_TIP_TYPE;
        }
        BigDecimal taxValNumber = parseTaxTipVal(taxVal);

        boolean changed = false;

        if(mTaxType == null || !taxType.equals(mTaxType)) {
            mTaxType = taxType;
            changed = true;
        }

        if(mTaxVal == null || taxValNumber.compareTo(mTaxVal) != 0) {
            mTaxVal = taxValNumber;
            changed = true;
        }

        return changed;
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

    public boolean setTipVal(TaxTipType tipType, String tipVal) {
        if(tipType == null) {
            tipType = DEF_TAX_TIP_TYPE;
        }
        BigDecimal tipValNumber = parseTaxTipVal(tipVal);

        boolean changed = false;

        if(mTipType == null || !tipType.equals(mTipType)) {
            mTipType = tipType;
            changed = true;
        }

        if(mTipVal == null || tipValNumber.compareTo(mTipVal) != 0) {
            mTipVal = tipValNumber;
            changed = true;
        }

        return changed;
    }

    public TaxTipType getTipType() {
        return mTipType;
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;

        if(isLoaded()) {
            for (Order order : getModelList()) {
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

    private BigDecimal parseTaxTipVal(String val) {
        BigDecimal valNumber = null;
        try {
            valNumber = new BigDecimal(val);
        } catch(NullPointerException ignored) {
        } catch(NumberFormatException ignored) {
        } finally {
            if(valNumber == null || valNumber.compareTo(MIN_TAX_TIP_VAL) < 0) {
                valNumber = MIN_TAX_TIP_VAL;
            }
            else {
                valNumber = valNumber.round(MATH_CONTEXT);
            }
        }
        return valNumber;
    }

    private BigDecimal getTaxTipVal(TaxTipType intentType, TaxTipType type, BigDecimal val) {
        BigDecimal intentVal;

        if(intentType == type) {
            intentVal = val;
        } else {
            BigDecimal subtotal = getSubtotal();
            if(subtotal.compareTo(BigDecimal.ZERO) == 0) {
                intentVal = MIN_TAX_TIP_VAL;
            } else {
                switch (intentType) {
                    case ABSOLUTE:
                        intentVal = subtotal.multiply(val).divide(FULL_TAX_TIP_PERCENT_VAL, BIG_VALUES_SCALE, BIG_VALUES_ROUNDING_MODE);
                        break;
                    case PERCENT:
                        intentVal = val.multiply(FULL_TAX_TIP_PERCENT_VAL).divide(subtotal, BIG_VALUES_SCALE, BIG_VALUES_ROUNDING_MODE);
                        break;
                    default:
                        intentVal = MIN_TAX_TIP_VAL;
                        break;
                }
            }
        }

        return intentVal;
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
    protected static final MathContext MATH_CONTEXT = new MathContext(BIG_VALUES_MAX_FRACTION_DIGITS, BIG_VALUES_ROUNDING_MODE);
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
