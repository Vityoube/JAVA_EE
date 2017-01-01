package org.vkalashnykov.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vkalashnykov on 31.12.16.
 */
public class DateTimeUtil {
    private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    public static String convertDatetoString(Date date){
        if (date!=null){
            String formattedDateString=format.format(date);
            return formattedDateString;
        }
        return null;
    }

    public static Date convertStringToDate(String dateString) throws ParseException {
        if (dateString!=null && !"".equals(dateString)){
            Date date=format.parse(dateString);
            return  date;
        }
        return null;

    }

    public static Date getCurrentDate(){
        Calendar calendar=Calendar.getInstance();
        return calendar.getTime();
    }

    public static Date getApplicationStartDate(){
        Calendar calendar=Calendar.getInstance();
        calendar.set(2016,01,01);
        return calendar.getTime();
    }

    public static boolean isEmptyStringDate(String stringDate){
        return stringDate.isEmpty() ? true :  false;
    }
}
