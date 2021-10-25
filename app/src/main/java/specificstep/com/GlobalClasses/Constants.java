package specificstep.com.GlobalClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import specificstep.com.Database.DatabaseHelper;
import specificstep.com.Models.Default;
import specificstep.com.R;

/**
 * Created by ubuntu on 13/1/17.
 */

public class Constants {

    public static final String ACTION_NOTIFICATION_UPDATE = "specificstep.com.perfectrecharge_dist.ACTION_NOTIFICATION_UPDATE";
    public static final String ACTION_INVALID_ACCESS_TOKEN = "specificstep.com.perfectrecharge_dist.ACTION_INVALID_TOKEN";
    // Preference key
    public static final String PREF_NAME = "setting_data";
    public final String PREF_UPDATE_DATE = "update_date";
    public final String PREF_UPDATE_TIME = "update_time";
    public final static String LOGIN_TYPE_RESELLER = "3";
    public final static String LOGIN_TYPE_RETAILER = "4";
    public final static String LOGIN_TYPE_DISTRIBUTER = "2";
    public static final String INVALID_DETAILS = "Invalid details";
    public static String APP_VERSION = "1.4";
    public static String TOTAL_UNREAD_NOTIFICATION = "0";
    public static String SENDER_ID = "ARINFO";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static String SHAREEDPREFERENCE = "SHAREEDPREFERENCE";

    public static String Lati = "";
    public static String Long = "";

    public static DatabaseHelper databaseHelper;

    public static String APP_PACKAGE_NAME = "";
    public static String PACKAGE_NAME_PERFECT_RECHARGE = "specificstep.com.perfectrecharge_dist";
    public static String PACKAGE_NAME_ONUS = "specificstep.com.onus_dist";
    public static String PACKAGE_NAME_OMKAR = "specificstep.com.omkar_dist";
    public static String PACKAGE_NAME_ADVANCE_RECHARGE = "specificstep.com.advancerecharge_dist";
    public static String PACKAGE_NAME_GREEN_PEARL = "specificstep.com.greenpearlrecharge_dist";
    public static String PACKAGE_NAME_SHARP_RECHARGE = "specificstep.com.sharprecharge_dist";
    public static String PACKAGE_NAME_HR_TRAVEL_MANIPUR = "specificstep.com.hrtravelmanipur_dist";
    public static String PACKAGE_NAME_MAHADEVPAY = "specificstep.com.mahadevpay_dist";
    public static String PACKAGE_NAME_DEMO = "specificstep.com.wldemorechargeengine_dist";
    public static String PACKAGE_NAME_SWAMI = "specificstep.com.swamiagencies_dist";
    public static String PACKAGE_NAME_SABHAJITCOMMUNICATION = "specificstep.com.sabhajitcommunication_dist";
    public static String PACKAGE_NAME_JUGADICOMMUNICATION = "specificstep.com.jugadicommunication_dist";
    public static String PACKAGE_NAME_POCKETBOX = "specificstep.com.pocketbox_dist";
    public static String PACKAGE_NAME_EXPERTRECHARGE = "specificstep.com.expertrecharge_dist";
    public static String PACKAGE_NAME_SARANYA = "specificstep.com.saranyarecharges_dist";
    public static String PACKAGE_NAME_ZULAN = "specificstep.com.zulanrecharge_dist";


    //set app image in all imageview as per package name
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void chaneIcon(Activity activity, ImageView img) {
        Constants.APP_PACKAGE_NAME = activity.getPackageName();
        System.out.println("New Package: " + Constants.APP_PACKAGE_NAME);
        if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_PERFECT_RECHARGE)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ONUS)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher_onus));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_OMKAR)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher_omkar));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ADVANCE_RECHARGE)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.advancerechargelogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_GREEN_PEARL)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.greenpearlrechargelogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SHARP_RECHARGE)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.sharprechargelogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_HR_TRAVEL_MANIPUR)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_hr_manipur));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_MAHADEVPAY)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.mahadevpaylogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_DEMO)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_demo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SWAMI)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_swami));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SABHAJITCOMMUNICATION)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.sabhajitlogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_JUGADICOMMUNICATION)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.jugadilogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_POCKETBOX)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.pocketboxlogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_EXPERTRECHARGE)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.expertrechargelogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SARANYA)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.saranyalogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ZULAN)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.zulanrechargelogo));
        }
    }

    //Change HomeActivity slider app image as per package name
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void chaneIcon(Activity activity, CircleImageView img) {
        Constants.APP_PACKAGE_NAME = activity.getPackageName();
        System.out.println("New Package: " + Constants.APP_PACKAGE_NAME);
        if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_PERFECT_RECHARGE)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ONUS)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher_onus));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_OMKAR)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher_omkar));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ADVANCE_RECHARGE)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.advancerechargelogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_GREEN_PEARL)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher_green_pearl));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SHARP_RECHARGE)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher_sharp_recharge));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_HR_TRAVEL_MANIPUR)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher_hr_manipur));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_MAHADEVPAY)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.mahadevpaylogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_DEMO)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher_demo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SWAMI)) {
            img.setBackground(activity.getResources().getDrawable(R.mipmap.ic_launcher_swami));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SABHAJITCOMMUNICATION)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.sabhajitlogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_JUGADICOMMUNICATION)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.jugadilogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_POCKETBOX)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.pocketboxlogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_EXPERTRECHARGE)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.expertrechargelogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SARANYA)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.saranyalogo));
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ZULAN)) {
            img.setBackground(activity.getResources().getDrawable(R.drawable.zulanrechargelogo));
        }
    }

    //Change App Image in all java files as per package name
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static int chaneIcon(Activity activity) {
        Constants.APP_PACKAGE_NAME = activity.getPackageName();
        System.out.println("New Package: " + Constants.APP_PACKAGE_NAME);
        //Drawable image = null;
        int image = 0;
        if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_PERFECT_RECHARGE)) {
            image = activity.getResources().getIdentifier("ic_launcher", "mipmap", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ONUS)) {
            image = activity.getResources().getIdentifier("ic_launcher_onus", "mipmap", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_OMKAR)) {
            image = activity.getResources().getIdentifier("ic_launcher_omkar", "mipmap", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ADVANCE_RECHARGE)) {
            image = activity.getResources().getIdentifier("advancerechargelogo", "drawable", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_GREEN_PEARL)) {
            image = activity.getResources().getIdentifier("ic_launcher_green_pearl", "mipmap", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SHARP_RECHARGE)) {
            image = activity.getResources().getIdentifier("ic_launcher_sharp_recharge", "mipmap", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_HR_TRAVEL_MANIPUR)) {
            image = activity.getResources().getIdentifier("ic_launcher_hr_manipur", "mipmap", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_MAHADEVPAY)) {
            image = activity.getResources().getIdentifier("ic_launcher_mahadevpay", "mipmap", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_DEMO)) {
            image = activity.getResources().getIdentifier("ic_launcher_demo", "mipmap", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SWAMI)) {
            image = activity.getResources().getIdentifier("ic_launcher_swami", "mipmap", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SABHAJITCOMMUNICATION)) {
            image = activity.getResources().getIdentifier("sabhajitlogo", "drawable", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_JUGADICOMMUNICATION)) {
            image = activity.getResources().getIdentifier("jugadilogo", "drawable", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_POCKETBOX)) {
            image = activity.getResources().getIdentifier("pocketboxlogo", "drawable", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_EXPERTRECHARGE)) {
            image = activity.getResources().getIdentifier("expertrechargelogo", "drawable", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SARANYA)) {
            image = activity.getResources().getIdentifier("saranyalogo", "drawable", APP_PACKAGE_NAME);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ZULAN)) {
            image = activity.getResources().getIdentifier("zulanrechargelogo", "drawable", APP_PACKAGE_NAME);
        }
        return image;
    }

    //Change app name as per package name
    public static Drawable changeActionbarLogo(Activity activity) {
        Constants.APP_PACKAGE_NAME = activity.getPackageName();
        System.out.println("New Package: " + Constants.APP_PACKAGE_NAME);
        Drawable image = null;
        if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_PERFECT_RECHARGE)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ONUS)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_onus);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_OMKAR)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_omkar);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ADVANCE_RECHARGE)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_advance_recharge);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_GREEN_PEARL)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_green_pearl);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SHARP_RECHARGE)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_sharp_recharge);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_HR_TRAVEL_MANIPUR)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_hr_manipur);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_MAHADEVPAY)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_mahadevpay);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_DEMO)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_demo);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SWAMI)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_swami);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SABHAJITCOMMUNICATION)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_sabhajit);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_JUGADICOMMUNICATION)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_jugadi);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_POCKETBOX)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_pocketbox);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_EXPERTRECHARGE)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_expertrecharge);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SARANYA)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_saranya);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ZULAN)) {
            image = activity.getResources().getDrawable(R.drawable.actionbar_logo_with_space_zulan);
        }
        return image;
    }

    //Change app name as per package name
    public static String changeAppName(Activity activity) {
        Constants.APP_PACKAGE_NAME = activity.getPackageName();
        System.out.println("New Package: " + Constants.APP_PACKAGE_NAME);
        String app_name = "";
        if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_PERFECT_RECHARGE)) {
            app_name = activity.getResources().getString(R.string.app_name);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ONUS)) {
            app_name = activity.getResources().getString(R.string.app_name_onus);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_OMKAR)) {
            app_name = activity.getResources().getString(R.string.app_name_omkar);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ADVANCE_RECHARGE)) {
            app_name = activity.getResources().getString(R.string.app_name_advance_recharge);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_GREEN_PEARL)) {
            app_name = activity.getResources().getString(R.string.app_name_green_pearl);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SHARP_RECHARGE)) {
            app_name = activity.getResources().getString(R.string.app_name_sharp_recharge);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_HR_TRAVEL_MANIPUR)) {
            app_name = activity.getResources().getString(R.string.app_name_hr_travel_manipur);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_MAHADEVPAY)) {
            app_name = activity.getResources().getString(R.string.app_name_mahadevpay);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_DEMO)) {
            app_name = activity.getResources().getString(R.string.app_name_demo);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SWAMI)) {
            app_name = activity.getResources().getString(R.string.app_name_swami);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SABHAJITCOMMUNICATION)) {
            app_name = activity.getResources().getString(R.string.app_name_sabhajitcommunication);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_JUGADICOMMUNICATION)) {
            app_name = activity.getResources().getString(R.string.app_name_jugadicommunication);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_POCKETBOX)) {
            app_name = activity.getResources().getString(R.string.app_name_pocketbox);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_EXPERTRECHARGE)) {
            app_name = activity.getResources().getString(R.string.app_name_expertrecharge);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_SARANYA)) {
            app_name = activity.getResources().getString(R.string.app_name_saranyarecharges);
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ZULAN)) {
            app_name = activity.getResources().getString(R.string.app_name_zulanrecharge);
        }
        return app_name;
    }

    //Change App share link as per package name
    public static String changeAppShareLink() {
        String app_name = "https://play.google.com/store/apps/details?id=" + APP_PACKAGE_NAME;
        /*if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_PERFECT_RECHARGE)) {
            app_name = "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME_PERFECT_RECHARGE;
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ONUS)) {
            app_name = "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME_ONUS;
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_OMKAR)) {
            app_name = "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME_OMKAR;
        } else if(Constants.APP_PACKAGE_NAME.equals(PACKAGE_NAME_ADVANCE_RECHARGE)) {
            app_name = "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME_OMKAR;
        }*/
        return app_name;
    }

    public static boolean checkInternet(Context activity) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }

    public static String addRsSymbol(Activity context, String amount) {
        String cAmount = amount;
        // Decimal format
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);
        // Add RS symbol in credit and debit amount
        try {
            if (!TextUtils.equals(cAmount, "0")) {
                cAmount = context.getResources().getString(R.string.currency_format, String.valueOf(format.parse(cAmount).floatValue()));
            }
        }
        catch (Exception ex) {
            Log.e("Cash Adapter", "Error in decimal number");
            Log.e("Cash Adapter", "Error : " + ex.getMessage());
            ex.printStackTrace();
            cAmount = context.getResources().getString(R.string.currency_format, amount);
        }
        return cAmount;
    }

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String commonDateFormate(String time, String inputPattern, String outputPattern) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /*Method : decryptAPI
           Decrypt response of webservice*/
    public static String decryptAPI(Activity activity, String response, String strMacAddress) {
        databaseHelper = new DatabaseHelper(activity);
        ArrayList<Default> defaultArrayList;
        defaultArrayList = databaseHelper.getDefaultSettings();
        String user_id = defaultArrayList.get(0).getUser_id();
        MCrypt mCrypt = new MCrypt(user_id, strMacAddress);
        String decrypted_response = null;
        byte[] decrypted_bytes = Base64.decode(response, Base64.DEFAULT);
        try {
            decrypted_response = new String(mCrypt.decrypt(mCrypt.bytesToHex(decrypted_bytes)), "UTF-8");
        }
        catch (Exception e) {
            System.out.println("Cashbook : " + "Error 7 : " + e.getMessage());
            e.printStackTrace();
        }
        return decrypted_response;
    }

}
