package specificstep.com.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import specificstep.com.Models.ParentUserModel;
import specificstep.com.R;

public class ParentUserAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private List<ParentUserModel> models = null;
    private Context context;

    public ParentUserAdapter(Context activity, List<ParentUserModel> _models) {
        inflater = LayoutInflater.from(activity.getApplicationContext());
        models = _models;
        this.context = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowHolder rowHolder;
        if (convertView == null) {
            rowHolder = new RowHolder();
            convertView = inflater.inflate(R.layout.item_parent_user, parent, false);
            rowHolder.txtFirmName = (TextView) convertView.findViewById(R.id.txt_Item_ParentUser_FirmName);
            rowHolder.txtMobileNumber = (TextView) convertView.findViewById(R.id.txt_Item_ParentUser_MobileNumber);
            rowHolder.txtUserType = (TextView) convertView.findViewById(R.id.txt_Item_ParentUser_UserType);
            rowHolder.txtName = (TextView) convertView.findViewById(R.id.txt_Item_ParentUser_Name);
            convertView.setTag(rowHolder);
        } else {
            rowHolder = (RowHolder) convertView.getTag();
        }

        ParentUserModel parentUserModel = models.get(position);

        rowHolder.txtFirmName.setText(context.getString(R.string.firm_name_format, parentUserModel.getFirmName()));
        rowHolder.txtMobileNumber.setText(context.getString(R.string.mobile_number_format, parentUserModel.getMobileNumber()));
        rowHolder.txtUserType.setText(context.getString(R.string.user_type_format, parentUserModel.getUserType()));
        rowHolder.txtName.setText(context.getString(R.string.name_format, parentUserModel.getName()));

        return convertView;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ParentUserModel getData(int position) {
        return models.get(position);
    }

    private class RowHolder {
        private TextView txtFirmName, txtMobileNumber, txtUserType, txtName;
    }
}