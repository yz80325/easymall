package jp.mmall.util;

import java.math.BigDecimal;

/**
 * Created by 2021 on 2019/10/21.
 */
public class BigDecimalUtil {
    private BigDecimalUtil() {
    }
    public static BigDecimal add(double d1,double d2){
        BigDecimal decimal=new BigDecimal(Double.toString(d1));
        BigDecimal decima2=new BigDecimal(Double.toString(d2));
        return decimal.add(decima2);
    }
    public static BigDecimal sub(double d1,double d2){
        BigDecimal decimal=new BigDecimal(Double.toString(d1));
        BigDecimal decima2=new BigDecimal(Double.toString(d2));
        return decimal.subtract(decima2);
    }
    public static BigDecimal mul(double d1,double d2){
        BigDecimal decimal=new BigDecimal(Double.toString(d1));
        BigDecimal decima2=new BigDecimal(Double.toString(d2));
        return decimal.multiply(decima2);
    }
    public static BigDecimal div(double d1,double d2){
        BigDecimal decimal=new BigDecimal(Double.toString(d1));
        BigDecimal decima2=new BigDecimal(Double.toString(d2));
        return decimal.divide(decima2,2,BigDecimal.ROUND_HALF_UP);
    }
}
