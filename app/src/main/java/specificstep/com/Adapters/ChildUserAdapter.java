package specificstep.com.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import specificstep.com.Models.ChildUserModel;
import specificstep.com.utility.Utility;
import specificstep.com.R;

/**
 * Created by ubuntu on 3/5/17.
 */

public class ChildUserAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater = null;
    private List<ChildUserModel> models = null;

    public ChildUserAdapter(Context activity, List<ChildUserModel> _models) {
        context = activity;
        inflater = LayoutInflater.from(activity.getApplicationContext());
        models = _models;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowHolder rowHolder;
        if (convertView == null) {
            rowHolder = new RowHolder();
            convertView = inflater.inflate(R.layout.item_child_user, parent, false);
            rowHolder.txtFirmName = (TextView) convertView.findViewById(R.id.txt_Item_ChildUser_FirmName);
            rowHolder.txtBalance = (TextView) convertView.findViewById(R.id.txt_Item_ChildUser_Balance);
            rowHolder.txtMobileNumber = (TextView) convertView.findViewById(R.id.txt_Item_ChildUser_MobileNumber);
            rowHolder.txtUserType = (TextView) convertView.findViewById(R.id.txt_Item_ChildUser_Type);
            convertView.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) convertView.getTag();
        }

        rowHolder.txtFirmName.setText(models.get(position).firmName);
        rowHolder.txtMobileNumber.setText(models.get(position).phoneNo);
        rowHolder.txtUserType.setText(models.get(position).userType);
        rowHolder.txtBalance.setText(context.getResources().getString(R.string.currency_format, Utility.formatBigDecimalToString(new BigDecimal(models.get(position).balance))));

        return convertView;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public ChildUserModel getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ChildUserModel getData(int position) {
        return models.get(position);
    }

    private class RowHolder {
        private TextView txtFirmName, txtBalance, txtMobileNumber, txtUserType;
    }
}