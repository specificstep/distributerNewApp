package specificstep.com.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.NotificationModel;
import specificstep.com.R;
import specificstep.com.utility.DateTime;

public class NotificationAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private List<NotificationModel> models = null;
    private Context context;

    public NotificationAdapter(Context activity, List<NotificationModel> _models) {
        context = activity;
        inflater = LayoutInflater.from(activity.getApplicationContext());
        models = _models;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowHolder rowHolder;
        if (convertView == null) {
            rowHolder = new RowHolder();
            convertView = inflater.inflate(R.layout.adapter_notification, parent, false);
            rowHolder.txtId = (TextView) convertView.findViewById(R.id.txt_Item_Notification_Id);
            rowHolder.txtMessage = (TextView) convertView.findViewById(R.id.txt_Item_Notification_Message);
            rowHolder.txtTitle = (TextView) convertView.findViewById(R.id.txt_Item_Notification_Title);
            rowHolder.txtDateTime = (TextView) convertView.findViewById(R.id.txt_Item_Notification_DateTime);
            rowHolder.txtReadDateTime = (TextView) convertView.findViewById(R.id.txt_Item_Notification_ReadDateTime);
            rowHolder.imgTick = (ImageView) convertView.findViewById(R.id.img_Notification_MessageRead);
            convertView.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) convertView.getTag();
        }

        rowHolder.txtId.setText(models.get(position).id);
        rowHolder.txtTitle.setText(models.get(position).title);
        if (TextUtils.equals(models.get(position).readFlag, "1")) {
            rowHolder.imgTick.setVisibility(View.VISIBLE);
            rowHolder.txtReadDateTime.setVisibility(View.VISIBLE);
            rowHolder.txtReadDateTime.setText(context.getString(R.string.read_format, DateTime.getFormattedDate(models.get(position).readDateTime, DateTime.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT, Constants.DATE_TIME_FORMAT)));
        } else {
            rowHolder.imgTick.setVisibility(View.GONE);
            rowHolder.txtReadDateTime.setVisibility(View.GONE);
        }

        /* [START] - Display message in proper format
            recharge notification message = 9925049355 - Success/Failure/Credit, Aircel, Flexi,
            123465789798987, 2017-03-27 06:00:00
            Simple message = App installed successfully and shortcut created.
         */
        String formattedMessage;
        String originalMessage = models.get(position).message;
        String notificationDate = "";
        try {
            // Check message contains mobile number or not
            if (originalMessage.contains("-")) {
                formattedMessage = "Number : " + originalMessage.substring(0, originalMessage.indexOf("-")) + "\n";
                originalMessage = originalMessage.substring(originalMessage.indexOf("-") + 1, originalMessage.length());
                // Check message contains comma separated value or not
                if (originalMessage.contains(",")) {
                    // Convert comma separated value in list
                    List<String> notificationMessageItems = Arrays.asList(originalMessage.split("\\s*,\\s*"));
                    // check list contains all value or not
                    if (notificationMessageItems.size() == 5) {
                        // get all value from list
                        formattedMessage += "Transaction Id : " + notificationMessageItems.get(3) + "\n";
                        formattedMessage += "Status : " + notificationMessageItems.get(0) + "\n";
                        formattedMessage += "Company : " + notificationMessageItems.get(1) + "\n";
                        formattedMessage += "Product : " + notificationMessageItems.get(2);
                        // formattedMessage += "Date Time : " + notificationMessageItems.get(4);
                        notificationDate = notificationMessageItems.get(4);
                    } else {
                        formattedMessage += getOriginalMessage(originalMessage);
                    }
                } else {
                    formattedMessage += getOriginalMessage(originalMessage);
                }
            } else {
                formattedMessage = getOriginalMessage(originalMessage);
            }
        } catch (Exception ex) {
            Log.d("Notification Adapter", "Error while format notification message");
            Log.d("Notification Adapter", "Error : " + ex.getMessage());
            ex.printStackTrace();
            formattedMessage = originalMessage;
            notificationDate = "";
        }
        // [END]

        rowHolder.txtMessage.setText(formattedMessage);
        if (TextUtils.isEmpty(notificationDate))
            rowHolder.txtDateTime.setText(context.getString(R.string.date_time_format, DateTime.getFormattedDate(models.get(position).receiveDateTime, DateTime.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT, Constants.DATE_TIME_FORMAT)));
        else
            rowHolder.txtDateTime.setText(context.getString(R.string.date_time_format, DateTime.getFormattedDate(notificationDate, DateTime.YYYY_MM_DD_HH_MM_SS_DATE_FORMAT, Constants.DATE_TIME_FORMAT)));

        return convertView;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public NotificationModel getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String getOriginalMessage(String message) {
        String originalMessage = message;
        try {
            if (originalMessage.trim().length() > 0) {
                if (originalMessage.toLowerCase().contains("payment id")) {
                    String formattedMessage = originalMessage.substring(0, originalMessage.toLowerCase().indexOf("payment id"));
                    formattedMessage += "\n" + originalMessage.substring(originalMessage.toLowerCase().indexOf("payment id"), originalMessage.length());
                    originalMessage = formattedMessage;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            originalMessage = message;
        }
        Log.d("Notification", "Formatted Message : " + originalMessage);
        return originalMessage;
    }

    private class RowHolder {
        private TextView txtId, txtMessage, txtTitle, txtDateTime, txtReadDateTime;
        private ImageView imgTick;
    }
}
