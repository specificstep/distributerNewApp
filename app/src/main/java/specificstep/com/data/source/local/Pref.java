package specificstep.com.data.source.local;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import specificstep.com.GlobalClasses.Constants;

@Singleton
public class Pref {
    public static final String PREF_FILE = Constants.PREF_NAME;

    public static final String KEY_OPT_CODE = "opt_code";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_IS_OTP_VERIFIED = "is_otp_verified";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_FNAME = "user_first_name";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REMEMBER_PASSWORD = "remember_password";
    public static final String KEY_IS_FIRST_TIME = "is_first_time_user";
    public static final String KEY_LAST_UPDATE_DATE = "last_update_time";
    public static final String KEY_MIN_AMOUNT_FILTER_VALUE = "filter_min_amount";
    public static final String KEY_MAX_AMOUNT_FILTER_VALUE = "filter_max_amount";
    public static final String KEY_SORTING_FILTER_VALUE = "sorting_by";
    public static final String KEY_GCM_TOKEN = "gcm_token";
    public static final String KEY_BALANCE = "balance";
    public static final String KEY_MOBILE = "mobile";

    public static final String KEY_MAC_ADDRESS = "mac_address";
    public static final String KEY_APP = "app";
    public static final String KEY_FORGOT_OTP = "forgot_otp";

    private final SharedPreferences sharedPreferences;

    @Inject
    public Pref(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getValue(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void setValue(String key, int value) {
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putInt(key, value);
        prefsPrivateEditor.apply();
    }

    public void setValue(String key, float value) {
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putFloat(key, value);
        prefsPrivateEditor.apply();
    }

    public int getValue(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public float getValue(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public void setValue(String key, long value) {
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putLong(key, value);
        prefsPrivateEditor.apply();
    }

    public long getValue(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void setValue(String key, String value) {
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString(key, value);
        prefsPrivateEditor.apply();
    }

    public boolean getValue(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setValue(String key, boolean value) {
        SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putBoolean(key, value);
        prefsPrivateEditor.apply();
    }

    public void removeValues(String... keys) {
        if (keys != null && keys.length > 0) {
            SharedPreferences.Editor prefsPrivateEditor = sharedPreferences.edit();
            for (String key : keys) {
                prefsPrivateEditor.remove(key);
            }
            prefsPrivateEditor.apply();
        }
    }

    public void clearPref() {
        sharedPreferences.edit().clear().apply();
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }
}