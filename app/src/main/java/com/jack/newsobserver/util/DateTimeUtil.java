package com.jack.newsobserver.util;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
//    private Context context;
//
//    public DateTimeUtil(Context context) {
//    this.context=context;
//    }


    public static long convertStringToMsec (String dateTimeValue){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        Date dateItem = new Date();
        try {
            dateItem=format.parse(dateTimeValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(dateItem);
        return calendar.getTimeInMillis();
    }

    public static String getStringFromMsec(Context context, long millisec){
        Date dateItem = new Date();
        dateItem.setTime(millisec);
        java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        return dateFormat.format(dateItem)+" "+ timeFormat.format(dateItem);

    }
}
