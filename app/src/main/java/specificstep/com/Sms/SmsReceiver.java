package specificstep.com.Sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import specificstep.com.GlobalClasses.Constants;

/**
 * Created by programmer044 on 20/02/17.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;
    private String TAG = SmsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle data = intent.getExtras();
            Constants constants = new Constants();

            Object[] pdus = (Object[]) data.get("pdus");

            for (int i = 0; i < pdus.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                String sender = smsMessage.getDisplayOriginatingAddress();
                Log.d(TAG, "sender : " + sender);
                String[] separated = sender.split("-");
                Log.d(TAG, "separated : " + separated);
                Log.d(TAG, "smsMessage : " + smsMessage.getMessageBody());
                Log.d(TAG, "separated.length : " + separated.length);
                try {
                    if (separated.length > 1) {
                        if (separated[1].compareTo(constants.SENDER_ID) == 0) {
                            Log.d(TAG, "separated[0] : " + separated[0]);
                            Log.d(TAG, "separated[1] : " + separated[1]);
                            //You must check here if the sender is your provider and not another one with same text.
                            String messageBody = smsMessage.getMessageBody();
                            Log.d(TAG, "messageBody : " + messageBody);
                            //Pass on the text to our listener.
                            if (!mListener.equals(null))
                                mListener.messageReceived(messageBody);
                        }
                    }
                    // Log.d(TAG, "separated[0] : " + separated[0]);
                }
                catch (Exception ex) {
                    Log.d(TAG, "Error in message receiver : " + ex.toString());
                    ex.printStackTrace();
                }
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "Error while receive message");
            Log.e(TAG, "Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public static void unBindListener() {
        mListener = null;
    }
}
