package specificstep.com.ui.firebase;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import specificstep.com.GlobalClasses.AppController;
import specificstep.com.R;

/**
 * Created by ubuntu on 6/2/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getBody());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String messageBody) {
        ((AppController) getApplication()).getNotificationUtil().sendNotification((Activity) getApplicationContext(),getString(R.string.recharge_notification), messageBody);
    }
}
