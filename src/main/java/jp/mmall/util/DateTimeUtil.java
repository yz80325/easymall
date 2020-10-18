package jp.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by 2021 on 2019/10/17.
 */
public class DateTimeUtil {



    //定义一个常量类
    public static final String STANDAD_FOMAT="yyyy-MM-dd HH:mm:ss";
//StringへDate
    public static Date strToDate(String dateTimeStr,String formatString){
        //转的类型
       DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(formatString);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    //Date>String
    public static String dateToStr(Date date,String formatDate){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(formatDate);
    }

    public static Date strToDate(String dateTimeStr){
        //转的类型
        DateTimeFormatter dateTimeFormat=DateTimeFormat.forPattern(STANDAD_FOMAT);
        DateTime dateTime=dateTimeFormat.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    //Date>String
    public static String dateToStr(Date date){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDAD_FOMAT);
    }
}
