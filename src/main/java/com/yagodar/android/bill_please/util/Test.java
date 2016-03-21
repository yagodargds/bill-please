package com.yagodar.android.bill_please.util;

import com.yagodar.essential.debug.DebugUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by yagodar on 19.02.2016.
 */
public class Test {
    public static void main (String[] args) {
        nullTest();
    }

    public static int raundV(List<Float> probability) {
        int total = 0;
        int raund = rand(999);
        int index = 0;

        if (probability == null) {
            return -1;
        }

        for (float f : probability) {
            total += (int)(f * 10);
            if (total > raund) {
                break;
            }

            ++index;
        }

        if (index >= probability.size()) {
            throw new IllegalStateException("WRONG RAUNDV");
        }

        return index;
    }

    public static int rand(int bound) {
        if (bound == 0) {
            return 0;
        }
        return ThreadLocalRandom.current().nextInt(bound);
    }

    private static void nullTest() {
        int countM = 0;
        int countN = 0;
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.DATE, 4);
        startDate.set(Calendar.MONTH, 4);
        Calendar endDate = (Calendar) startDate.clone();
        endDate.set(Calendar.DATE, 4);
        endDate.set(Calendar.MONTH, 5);
        List<Float> satTripProb = new ArrayList<>();
        satTripProb.add(25.0f);
        satTripProb.add(75.0f);

        System.out.println("---------------------------------------------------------");
        calcUniform(startDate, countM, countN, endDate, satTripProb, new T_K1_UniformRouteRule());
        //System.out.println("---------------------------------------------------------");
        //calcUniformMonthNx(startDate, countM, countN, endDate, getMonthN1Price() / 30.0f, satTripProb, new T_K1_UniformMonthTramRouteRule());
        System.out.println("---------------------------------------------------------");
        calcComboMonth(startDate, countM, endDate, satTripProb, new T_K1_ComboMonthRouteRule());

        //System.out.println("---------------------------------------------------------");
        //calcUniform(startDate, countM, countN, endDate, satTripProb, new T_B_UniformRouteRule());
        //System.out.println("---------------------------------------------------------");
        //calcUniformMonthNx(startDate, countM, countN, endDate, getMonthN2Price() / 30.0f, satTripProb,  new T_B_UniformMonthTramBusRouteRule());
        //System.out.println("---------------------------------------------------------");
        //calcComboMonth(startDate, countM, endDate, satTripProb, new T_B_ComboMonthRouteRule());
    }

    private static void calcUniform(Calendar startDate, int countM, int countN, Calendar endDate, List<Float> satTripProb, AbsUniformRouteRule routeRule) {
        Calendar curDate = (Calendar) startDate.clone();
        boolean satTrip;
        UniformTripInfo tripInfo;
        float price = 0.0f;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy E");
        System.out.println("calcUniform {start_date={" + dateFormat.format(startDate.getTime()) + "} countM=" + countM + " countN=" + countN + " price=" + price + "}");
        while(curDate.before(endDate)) {
            if(curDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                satTrip = raundV(satTripProb) == 1;
            } else {
                satTrip = false;
            }

            tripInfo = routeRule.getTripInfoTo(curDate, countM, countN, satTrip);
            countM += tripInfo.countM;
            countN += tripInfo.countN;
            price += tripInfo.price;
            System.out.println("calcUniform {date={" + dateFormat.format(curDate.getTime()) + "} countM=" + countM + " countN=" + countN + " price=" + price + " trip_to=" + tripInfo + "}");

            tripInfo = routeRule.getTripInfoFrom(curDate, countM, countN, satTrip);
            countM += tripInfo.countM;
            countN += tripInfo.countN;
            price += tripInfo.price;
            System.out.println("calcUniform {date={" + dateFormat.format(curDate.getTime()) + "} countM=" + countM + " countN=" + countN + " price=" + price + " trip_from=" + tripInfo + "}");

            curDate.add(Calendar.DATE, 1);
            if(curDate.get(Calendar.DATE) == 1) {
                countM = 0;
                countN = 0;
            }
        }
        System.out.println("calcUniform {end_date={" + dateFormat.format(endDate.getTime()) + "} countM=" + countM + " countN=" + countN + " price=" + price + "}");
    }

    private static void calcUniformMonthNx(Calendar startDate, int countM, int countN, Calendar endDate, float dayPrice, List<Float> satTripProb, AbsUniformRouteRule routeRule) {
        Calendar curDate = (Calendar) startDate.clone();
        boolean satTrip;
        UniformTripInfo tripInfo;
        float price = 0.0f;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy E");
        System.out.println("calcUniformMonthNx {start_date={" + dateFormat.format(startDate.getTime()) + "} countM=" + countM  + " countN=" + countN + " price=" + price + "}");
        while(curDate.before(endDate)) {
            price += dayPrice;

            if(curDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                satTrip = raundV(satTripProb) == 1;
            } else {
                satTrip = false;
            }

            tripInfo = routeRule.getTripInfoTo(curDate, countM, countN, satTrip);
            countM += tripInfo.countM;
            countN += tripInfo.countN;
            price += tripInfo.price;
            System.out.println("calcUniformMonthNx {date={" + dateFormat.format(curDate.getTime()) + "} countM=" + countM + " price=" + price + " trip_to=" + tripInfo + "}");

            tripInfo = routeRule.getTripInfoFrom(curDate, countM, countN, satTrip);
            countM += tripInfo.countM;
            countN += tripInfo.countN;
            price += tripInfo.price;
            System.out.println("calcUniformMonthNx {date={" + dateFormat.format(curDate.getTime()) + "} countM=" + countM + " price=" + price + " trip_from=" + tripInfo + "}");

            curDate.add(Calendar.DATE, 1);
            if(curDate.get(Calendar.DATE) == 1) {
                countM = 0;
                countN = 0;
            }
        }
        System.out.println("calcUniformMonthNx {end_date={" + dateFormat.format(endDate.getTime()) + "} countM=" + countM + " price=" + price + "}");
    }

    private static void calcComboMonth(Calendar startDate, int countM, Calendar endDate, List<Float> satTripProb, AbsCountMRouteRule routeRule) {
        Calendar curDate = (Calendar) startDate.clone();
        boolean satTrip;
        CountMTripInfo tripInfo;
        float dayPrice = getMonthComboPrice() / 30.0f;
        float price = 0.0f;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy E");
        System.out.println("calcComboMonth {start_date={" + dateFormat.format(startDate.getTime()) + "} countM=" + countM + " price=" + price + "}");
        while(curDate.before(endDate)) {
            price += dayPrice;

            if(curDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                satTrip = raundV(satTripProb) == 1;
            } else {
                satTrip = false;
            }

            tripInfo = routeRule.getTripInfoTo(curDate, countM, satTrip);
            countM += tripInfo.countM;
            price += tripInfo.price;
            System.out.println("calcComboMonth {date={" + dateFormat.format(curDate.getTime()) + "} countM=" + countM + " price=" + price + " trip_to=" + tripInfo + "}");

            tripInfo = routeRule.getTripInfoFrom(curDate, countM, satTrip);
            countM += tripInfo.countM;
            price += tripInfo.price;
            System.out.println("calcComboMonth {date={" + dateFormat.format(curDate.getTime()) + "} countM=" + countM + " price=" + price + " trip_from=" + tripInfo + "}");

            curDate.add(Calendar.DATE, 1);
            if(curDate.get(Calendar.DATE) == startDate.get(Calendar.DATE)) {
                countM = 0;
            }
        }
        System.out.println("calcComboMonth {end_date={" + dateFormat.format(endDate.getTime()) + "} countM=" + countM + " price=" + price + "}");
    }

    private static float getUniformPriceM(int countM) {
        if(countM >= 0 && countM <= 10) {
            return 34.0f;
        } else if(countM > 10 && countM <= 20) {
            return 33.0f;
        } else if(countM > 20 && countM <= 30) {
            return 32.0f;
        } else if(countM > 30 && countM <= 40) {
            return 31.0f;
        } else if(countM > 40) {
            return 30.0f;
        } else {
            throw new IllegalStateException();
        }
    }

    private static float getUnifromPriceN(int countN) {
        if(countN >= 0 && countN <= 10) {
            return 29.0f;
        } else if(countN > 10 && countN <= 20) {
            return 28.0f;
        } else if(countN > 20 && countN <= 30) {
            return 27.0f;
        } else if(countN > 30 && countN <= 40) {
            return 26.0f;
        } else if(countN > 40) {
            return 25.0f;
        } else {
            throw new IllegalStateException();
        }
    }

    private static float getPriceK_1() {
        return 34.0f;
    }

    private static float getMonthN1Price() {
        return 1445.0f;
    }

    private static float getMonthN2Price() {
        return 1710.0f;
    }

    private static float getMonthN3Price() {
        return 1815.0f;
    }

    private static float getMonthComboPrice() {
        return 2690.0f;
    }

    private static float getDefPriceM() {
        return 35.0f;
    }

    private static class T_K1_UniformRouteRule extends AbsUniformRouteRule {
        @Override
        public UniformTripInfo getTripInfoTo(Calendar date, int countM, int countN, boolean satTrip) {
            UniformTripInfo tripInfo = new UniformTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                tripInfo.countM = 1;
                tripInfo.countN = 1;
                tripInfo.price = getUnifromPriceN(countN) + getUniformPriceM(countM) + getPriceK_1();
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                tripInfo.countN = 1;
                tripInfo.price = getUnifromPriceN(countN) + getUniformPriceM(countM);
            } else {
                tripInfo.countM = 0;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }

        @Override
        public UniformTripInfo getTripInfoFrom(Calendar date, int countM, int countN, boolean satTrip) {
            UniformTripInfo tripInfo = new UniformTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                if(dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY) {
                    tripInfo.countM = 2;
                    tripInfo.countN = 2;
                    tripInfo.price = getUnifromPriceN(countN) + getUniformPriceM(countM) + getUniformPriceM(countM + 1) + getUnifromPriceN(countN + 1);
                } else {
                    tripInfo.countM = 1;
                    tripInfo.countN = 1;
                    tripInfo.price = getPriceK_1() + getUniformPriceM(countM) + getUnifromPriceN(countN);
                }
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                tripInfo.countN = 1;
                tripInfo.price = getUniformPriceM(countM) + getUnifromPriceN(countN);
            } else {
                tripInfo.countM = 0;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }
    }

    private static class T_B_UniformRouteRule extends AbsUniformRouteRule {
        @Override
        public UniformTripInfo getTripInfoTo(Calendar date, int countM, int countN, boolean satTrip) {
            UniformTripInfo tripInfo = new UniformTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                tripInfo.countM = 1;
                tripInfo.countN = 2;
                tripInfo.price = getUnifromPriceN(countN) + getUniformPriceM(countM) + getUnifromPriceN(countN + 1);
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                tripInfo.countN = 1;
                tripInfo.price = getUnifromPriceN(countN) + getUniformPriceM(countM);
            } else {
                tripInfo.countM = 0;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }

        @Override
        public UniformTripInfo getTripInfoFrom(Calendar date, int countM, int countN, boolean satTrip) {
            UniformTripInfo tripInfo = new UniformTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                if(dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY) {
                    tripInfo.countM = 2;
                    tripInfo.countN = 2;
                    tripInfo.price = getUnifromPriceN(countN) + getUniformPriceM(countM) + getUniformPriceM(countM + 1) + getUnifromPriceN(countN + 1);
                } else {
                    tripInfo.countM = 1;
                    tripInfo.countN = 2;
                    tripInfo.price = getUnifromPriceN(countN) + getUniformPriceM(countM) + getUnifromPriceN(countN + 1);
                }
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                tripInfo.countN = 1;
                tripInfo.price = getUniformPriceM(countM) + getUnifromPriceN(countN);
            } else {
                tripInfo.countM = 0;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }
    }

    private static class T_K1_UniformMonthTramRouteRule extends AbsUniformRouteRule {
        @Override
        public UniformTripInfo getTripInfoTo(Calendar date, int countM, int countN, boolean satTrip) {
            UniformTripInfo tripInfo = new UniformTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                tripInfo.countM = 1;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f + getUniformPriceM(countM) + getPriceK_1();
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f + getUniformPriceM(countM);
            } else {
                tripInfo.countM = 0;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }

        @Override
        public UniformTripInfo getTripInfoFrom(Calendar date, int countM, int countN, boolean satTrip) {
            UniformTripInfo tripInfo = new UniformTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                if(dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY) {
                    tripInfo.countM = 2;
                    tripInfo.countN = 1;
                    tripInfo.price = getUnifromPriceN(countN) + getUniformPriceM(countM) + getUniformPriceM(countM + 1) + 0.0f;
                } else {
                    tripInfo.countM = 1;
                    tripInfo.countN = 0;
                    tripInfo.price = getPriceK_1() + getUniformPriceM(countM) + 0.0f;
                }
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                tripInfo.countN = 0;
                tripInfo.price = getUniformPriceM(countM) + 0.0f;
            } else {
                tripInfo.countM = 0;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }
    }

    private static class T_B_UniformMonthTramBusRouteRule extends AbsUniformRouteRule {
        @Override
        public UniformTripInfo getTripInfoTo(Calendar date, int countM, int countN, boolean satTrip) {
            UniformTripInfo tripInfo = new UniformTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                tripInfo.countM = 1;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f + getUniformPriceM(countM) + 0.0f;
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f + getUniformPriceM(countM);
            } else {
                tripInfo.countM = 0;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }

        @Override
        public UniformTripInfo getTripInfoFrom(Calendar date, int countM, int countN, boolean satTrip) {
            UniformTripInfo tripInfo = new UniformTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                if(dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY) {
                    tripInfo.countM = 2;
                    tripInfo.countN = 0;
                    tripInfo.price = 0.0f + getUniformPriceM(countM) + getUniformPriceM(countM + 1) + 0.0f;
                } else {
                    tripInfo.countM = 1;
                    tripInfo.countN = 0;
                    tripInfo.price = 0.0f + getUniformPriceM(countM) + 0.0f;
                }
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                tripInfo.countN = 0;
                tripInfo.price = getUniformPriceM(countM) + 0.0f;
            } else {
                tripInfo.countM = 0;
                tripInfo.countN = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }
    }

    private static class T_K1_ComboMonthRouteRule extends AbsCountMRouteRule {
        @Override
        public CountMTripInfo getTripInfoTo(Calendar date, int countM, boolean satTrip) {
            CountMTripInfo tripInfo = new CountMTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                tripInfo.countM = 1;
                if(countM <= 70) {
                    tripInfo.price = 0.0f + getPriceK_1();
                } else {
                    tripInfo.price = 0.0f + getUniformPriceM(countM - 70) + getPriceK_1();
                }
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                if(countM <= 70) {
                    tripInfo.price = 0.0f;
                } else {
                    tripInfo.price = 0.0f + getUniformPriceM(countM - 70);
                }
            } else {
                tripInfo.countM = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }

        @Override
        public CountMTripInfo getTripInfoFrom(Calendar date, int countM, boolean satTrip) {
            CountMTripInfo tripInfo = new CountMTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                if(dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY) {
                    tripInfo.countM = 2;
                    if(countM <= 70) {
                        tripInfo.price = 0.0f;
                        countM++;
                        if(countM <= 70) {
                            tripInfo.price += 0.0f;
                        } else {
                            tripInfo.price += getUniformPriceM(countM - 70);
                        }
                        tripInfo.price += 0.0f;
                    } else {
                        tripInfo.price = 0.0f + getUniformPriceM(countM - 70) + getUniformPriceM(countM + 1 - 70) + 0.0f;
                    }
                } else {
                    tripInfo.countM = 1;
                    if(countM <= 70) {
                        tripInfo.price = getPriceK_1() + 0.0f + 0.0f;
                    } else {
                        tripInfo.price = getPriceK_1() + getUniformPriceM(countM - 70) + 0.0f;
                    }
                }
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                if(countM <= 70) {
                    tripInfo.price = 0.0f;
                } else {
                    tripInfo.price = getUniformPriceM(countM - 70) + 0.0f;
                }
            } else {
                tripInfo.countM = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }
    }

    private static class T_B_ComboMonthRouteRule extends AbsCountMRouteRule {
        @Override
        public CountMTripInfo getTripInfoTo(Calendar date, int countM, boolean satTrip) {
            CountMTripInfo tripInfo = new CountMTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                tripInfo.countM = 1;
                if(countM <= 70) {
                    tripInfo.price = 0.0f + 0.0f;
                } else {
                    tripInfo.price = 0.0f + getUniformPriceM(countM - 70) + 0.0f;
                }
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                if(countM <= 70) {
                    tripInfo.price = 0.0f;
                } else {
                    tripInfo.price = 0.0f + getUniformPriceM(countM - 70);
                }
            } else {
                tripInfo.countM = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }

        @Override
        public CountMTripInfo getTripInfoFrom(Calendar date, int countM, boolean satTrip) {
            CountMTripInfo tripInfo = new CountMTripInfo();
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                if(dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY) {
                    tripInfo.countM = 2;
                    if(countM <= 70) {
                        tripInfo.price = 0.0f;
                        countM++;
                        if(countM <= 70) {
                            tripInfo.price += 0.0f;
                        } else {
                            tripInfo.price += getUniformPriceM(countM - 70);
                        }
                        tripInfo.price += 0.0f;
                    } else {
                        tripInfo.price = 0.0f + getUniformPriceM(countM - 70) + getUniformPriceM(countM + 1 - 70) + 0.0f;
                    }
                } else {
                    tripInfo.countM = 1;
                    if(countM <= 70) {
                        tripInfo.price = 0.0f + 0.0f + 0.0f;
                    } else {
                        tripInfo.price = 0.0f + getUniformPriceM(countM - 70) + 0.0f;
                    }
                }
            } else if(dayOfWeek == Calendar.SATURDAY && satTrip) {
                tripInfo.countM = 1;
                if(countM <= 70) {
                    tripInfo.price = 0.0f;
                } else {
                    tripInfo.price = getUniformPriceM(countM - 70) + 0.0f;
                }
            } else {
                tripInfo.countM = 0;
                tripInfo.price = 0.0f;
            }
            return tripInfo;
        }
    }

    private static class CountMTripInfo {
        public int countM;
        public float price;
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            DebugUtils.buildShortClassTag(this, sb);
            sb.append(" trip_countM=").append(countM);
            sb.append(" trip_price=").append(price);
            sb.append("}");
            return sb.toString();
        }
    }

    private static class UniformTripInfo extends CountMTripInfo {
        public int countN;
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            sb.insert(sb.indexOf(" "), " trip_countN=" + countN);
            return sb.toString();
        }
    }

    private static abstract class AbsCountMRouteRule {
        public abstract CountMTripInfo getTripInfoTo(Calendar date, int countM, boolean satTrip);
        public abstract CountMTripInfo getTripInfoFrom(Calendar date, int countM, boolean satTrip);
    }

    private static abstract class AbsUniformRouteRule {
        public abstract UniformTripInfo getTripInfoTo(Calendar date, int countM, int countN, boolean satTrip);
        public abstract UniformTripInfo getTripInfoFrom(Calendar date, int countM, int countN, boolean satTrip);
    }
}
