package specificstep.com.utility;

import android.util.Log;

/**
 * Created by ubuntu on 23/3/17.
 */

public class LogMessage {
    private String TAG = "LogMessage";

    public LogMessage() {
    }

    public LogMessage(String tag) {
        TAG = tag;
    }

    public void d(String message) {
        Log.d(TAG, message);
//        int maxLogSize = 1000;
//        for(int i = 0; i <= message.length() / maxLogSize; i++) {
//            int start = i * maxLogSize;
//            int end = (i+1) * maxLogSize;
//            end = end > message.length() ? message.length() : end;
//            Log.v(TAG, "->" + message.substring(start, end));
//        }
    }

    public void e(String message) {
        Log.e(TAG, message);
    }

    public void i(String message) {
        Log.i(TAG, message);
    }

    public void v(String message) {
        Log.v(TAG, message);
    }

    public void d(int message) {
        Log.d(TAG, message + "");
    }

    public void e(int message) {
        Log.e(TAG, message + "");
    }

    public void i(int message) {
        Log.i(TAG, message + "");
    }

    public void v(int message) {
        Log.v(TAG, message + "");
    }
}
