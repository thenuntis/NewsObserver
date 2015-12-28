package com.jack.newsobserver.util;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    public static Date convertStringToDate (String dateTimeValue){
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        Date dateItem = new Date();
        try {
            dateItem=format.parse(dateTimeValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateItem;
    }

    public static String getStringFromMsec(Context context, long millisec){
        Date dateItem = new Date();
        dateItem.setTime(millisec);
        java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        return dateFormat.format(dateItem)+" "+ timeFormat.format(dateItem);
    }

    public  static Date getDateFromMsec (long millisec){
        Date dateItem = new Date();
        dateItem.setTime(millisec);
        return dateItem;
    }
}

