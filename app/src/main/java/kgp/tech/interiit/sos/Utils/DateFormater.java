package kgp.tech.interiit.sos.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by akshaygupta on 27/01/16.
 */
public class DateFormater {
    public static String formatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(date);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM");
        return format.format(date);
    }

    public static String formatTimeDate(Date date) {
        //MMM dd,yyyy hh:mm a
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a, dd MMM");
        return format.format(date);
    }

    public static String formatString(String sdate)
    {
        SimpleDateFormat format=new SimpleDateFormat("hh:mm a, dd MMM");
        try {
            String dateFormat  = "EEE MMM d HH:mm:ss z yyyy";
            Date date = new SimpleDateFormat(dateFormat).parse(sdate);
            return format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
