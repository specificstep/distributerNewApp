package specificstep.com.utility;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ubuntu on 8/3/17.
 */

public class DateTime {

    public static final String YYYY_MM_DD_HH_MM_SS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * This function return current time.
     * Return time example = 12:59:59
     *
     * @return String current time
     */
    public static String getTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        String s_time = (String.valueOf(hour).length() == 1 ? "0"
                + String.valueOf(hour) : String.valueOf(hour))
                + ":"
                + (String.valueOf(min).length() == 1 ? "0"
                + String.valueOf(min) : String.valueOf(min))
                + ":"
                + (String.valueOf(sec).length() == 1 ? "0"
                + String.valueOf(sec) : String.valueOf(sec));
        return s_time;
    }

    /**
     * This function return current time.
     * Return time example = 29.12.2015
     *
     * @return current date
     */
    public static String getDate() {
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        String s_date = (String.valueOf(date).length() == 1 ? "0"
                + String.valueOf(date) : String.valueOf(date))
                + "."
                + (String.valueOf(month).length() == 1 ? "0"
                + String.valueOf(month) : String.valueOf(month))
                + "."
                + (String.valueOf(year).length() == 1 ? "0"
                + String.valueOf(year) : String.valueOf(year));
        return s_date;
    }

    /**
     * This function return current time.
     * Return time example = 2017-05-09
     *
     * @return current date
     * @param separator
     */
    public static String getDate_YYYY_MM_DD(String separator) {
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        String s_date = (String.valueOf(year).length() == 1 ? "0"
                + String.valueOf(year) : String.valueOf(year))
                + separator
                + (String.valueOf(month).length() == 1 ? "0"
                + String.valueOf(month) : String.valueOf(month))
                + separator
                + (String.valueOf(date).length() == 1 ? "0"
                + String.valueOf(date) : String.valueOf(date));
        return s_date;
    }

    /**
     * Get current date time.
     *
     * @return String current date time yyyy-MM-dd hh:mm:ss
     */
    public static String getCurrentDateTime() {
        Date d = new Date();
        CharSequence s = DateFormat.format(YYYY_MM_DD_HH_MM_SS_DATE_FORMAT, d.getTime());
        return s.toString();
    }

    public static String getFormattedDate(String strDate, String sourceFormat, String outputFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(strDate);
            simpleDateFormat.applyPattern(outputFormat);
            return simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

}
