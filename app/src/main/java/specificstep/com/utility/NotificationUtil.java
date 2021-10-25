package specificstep.com.utility;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import specificstep.com.Database.NotificationTable;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.NotificationModel;
import specificstep.com.data.source.local.Pref;
import specificstep.com.ui.home.Flow;
import specificstep.com.ui.home.HomeActivity;
import specificstep.com.ui.splash.SplashActivity;

/**
 * Created by ubuntu on 27/3/17.
 */

@Singleton
public class NotificationUtil {
    // Sets an ID for the notification
    private static final int mNotificationId = 0;
    private final NotificationTable notificationTable;
    private final Pref pref;
    private Context context;
    private NotificationManager mNotificationManager = null;

    @Inject
    public NotificationUtil(Context context, NotificationTable notificationTable, Pref pref) {
        this.context = context;
        this.notificationTable = notificationTable;
        this.pref = pref;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void sendNotification(Activity activity, String title, String message) {
        // --- START --- save recharge message.
        NotificationModel model = new NotificationModel();
        model.title = title;
        model.message = message;
        model.receiveDateTime = DateTime.getCurrentDateTime();
        model.saveDateTime = DateTime.getCurrentDateTime();
        model.readFlag = "0";
        model.readDateTime = "";
        Log.d("Notification", "title : " + model.title + "Message : " + model.message);
        new NotificationTable(context).addNotificationData(model);

        ArrayList<NotificationModel> notificationModels = notificationTable.getLastNotificationData();
        int lastId = -1;
        if (notificationModels.size() > 0) {
            NotificationModel notificationModel = notificationModels.get(0);
            try {
                lastId = Integer.parseInt(notificationModel.id);
                Log.d("Notification", "Last id : " + lastId);
            } catch (Exception ex) {
                Log.d("Notification", "Error while parse id");
                ex.printStackTrace();
                lastId = -1;
            }
        }

        /* [START] - Create intent for start activity
        - Check if application verify and login success then open notification activity or open splash screen */

        // [END]

        /* [START] - Create notification builder */
        androidx.core.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(Constants.chaneIcon(activity));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        }

        mBuilder.setContentTitle(title)
                .setContentText(message);
        // Dismiss notification after action has been clicked
        mBuilder.setAutoCancel(true);
        // [END]

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());

        Intent resultIntent;
        if (pref.getValue(Pref.KEY_IS_LOGGED_IN, false)) {
            // Creates an explicit intent for an Activity in your app
            resultIntent = new Intent(context, HomeActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra(HomeActivity.EXTRA_FLOW, Flow.NOTIFICATION);
            resultIntent.putExtra(HomeActivity.EXTRA_NOTIFICATION_ID, lastId);
            stackBuilder.addParentStack(HomeActivity.class);

        } else {
            // Creates an explicit intent for an Activity in your app
            resultIntent = new Intent(context, SplashActivity.class);
        }

        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mNotificationManager.notify(lastNotification, mBuilder.build());
        mNotificationManager.notify(mNotificationId, mBuilder.build());

        Intent intent1 = new Intent(Constants.ACTION_NOTIFICATION_UPDATE);
        context.sendBroadcast(intent1);
    }

    public void cancelNotification() {
        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        try {
            // mNotificationManager.cancel(notificationId);
            // mNotificationManager.cancelAll();
            mNotificationManager.cancel(mNotificationId);
        } catch (Exception ex) {
            Log.e("Notification", "Error in cancel notification");
            Log.e("Notification", "Error : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
