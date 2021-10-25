package specificstep.com.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.Models.DMTPaymentListModel;
import specificstep.com.Models.User;
import specificstep.com.R;

public class DMTPaymentListAdapter extends RecyclerView.Adapter<DMTPaymentListAdapter.MyViewHolder>{

    List<DMTPaymentListModel> dataSet;
    public Context context;
    ArrayList<User> userArrayList;
    DatabaseHelper databaseHelper;
    private final int SUCCESS = 1, ERROR = 2;
    private TransparentProgressDialog transparentProgressDialog;
    private AlertDialog alertDialog_3;
    public static int position;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtBeneficiaryName, txtSenderName, /*txtBeneficiaryMobile, txtSenderMobile,
                txtSenderAltMobile, txtSenderEmail,*/ txtTransId, /*txtVendorId,*/ txtAmount,
                txtTransactionId, /*txtSenderId, txtProviderId,*/ txtBank, /*txtApiBrid,*/ txtTransactionStatus,
                txtDate;

        LinearLayout lnrBeneficiaryName, lnrSenderName, /*lnrBeneficiaryMobile, lnrSenderMobile,
                lnrSenderAltMobile, lnrSenderEmail,*/ lnrTransId, /*lnrVendorId,*/ lnrAmount,
                lnrTransactionId, /*lnrSenderId, lnrProviderId,*/ lnrBank, /*lnrApiBrid,*/ lnrTransactionStatus,
                lnrDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.txtBeneficiaryName = (TextView) itemView.findViewById(R.id.txtDMTPaymentListBeneficiaryName);
            this.txtSenderName = (TextView) itemView.findViewById(R.id.txtDMTPaymentListSenderName);
            /*this.txtBeneficiaryMobile = (TextView) itemView.findViewById(R.id.txtDMTPaymentListBeneficiaryMobile);
            this.txtSenderMobile = (TextView) itemView.findViewById(R.id.txtDMTPaymentListSenderMobile);
            this.txtSenderAltMobile = (TextView) itemView.findViewById(R.id.txtDMTPaymentListSenderAltMobile);
            this.txtSenderEmail = (TextView) itemView.findViewById(R.id.txtDMTPaymentListSenderEmail);*/
            this.txtTransId = (TextView) itemView.findViewById(R.id.txtDMTPaymentListTransId);
            /*this.txtSenderId = (TextView) itemView.findViewById(R.id.txtDMTPaymentListSenderId);
            this.txtVendorId = (TextView) itemView.findViewById(R.id.txtDMTPaymentListVenderId);*/
            this.txtAmount = (TextView) itemView.findViewById(R.id.txtDMTPaymentListAmount);
            this.txtTransactionId = (TextView) itemView.findViewById(R.id.txtDMTPaymentListTransactionId);
            //this.txtProviderId = (TextView) itemView.findViewById(R.id.txtDMTPaymentListProviderId);
            this.txtBank = (TextView) itemView.findViewById(R.id.txtDMTPaymentListBank);
            //this.txtApiBrid = (TextView) itemView.findViewById(R.id.txtDMTPaymentListApiBrid);
            this.txtTransactionStatus = (TextView) itemView.findViewById(R.id.txtDMTPaymentListTransactionStatus);
            this.txtDate = (TextView) itemView.findViewById(R.id.txtDMTPaymentListAddDate);


            this.lnrBeneficiaryName = (LinearLayout) itemView.findViewById(R.id.lnrBeneficiaryName);
            this.lnrSenderName = (LinearLayout) itemView.findViewById(R.id.lnrSenderName);
            /*this.lnrBeneficiaryMobile = (LinearLayout) itemView.findViewById(R.id.lnrBeneficiaryMobile);
            this.lnrSenderMobile = (LinearLayout) itemView.findViewById(R.id.lnrSenderMobile);
            this.lnrSenderAltMobile = (LinearLayout) itemView.findViewById(R.id.lnrSenderAltMobile);
            this.lnrSenderEmail = (LinearLayout) itemView.findViewById(R.id.lnrSenderEmail);*/
            this.lnrTransId = (LinearLayout) itemView.findViewById(R.id.lnrTransId);
            /*this.lnrSenderId = (LinearLayout) itemView.findViewById(R.id.lnrSenderId);
            this.lnrVendorId = (LinearLayout) itemView.findViewById(R.id.lnrVenderId);*/
            this.lnrAmount = (LinearLayout) itemView.findViewById(R.id.lnrAmount);
            this.lnrTransactionId = (LinearLayout) itemView.findViewById(R.id.lnrTransactionId);
            //this.lnrProviderId = (LinearLayout) itemView.findViewById(R.id.lnrProviderId);
            this.lnrBank = (LinearLayout) itemView.findViewById(R.id.lnrBank);
            //this.lnrApiBrid = (LinearLayout) itemView.findViewById(R.id.lnrApiBrid);
            this.lnrTransactionStatus = (LinearLayout) itemView.findViewById(R.id.lnrTransactionStatus);
            this.lnrDate = (LinearLayout) itemView.findViewById(R.id.lnrDate);


        }
    }

    public DMTPaymentListAdapter(Context con, List<DMTPaymentListModel> data) {
        this.context = con;
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_payment_list, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        holder.txtBeneficiaryName.setText(dataSet.get(listPosition).getBeneficiary_mobile_number() + "(" + dataSet.get(listPosition).getBeneficiary_first_name() + " " + dataSet.get(listPosition).getBeneficiary_last_name() + ")");
        holder.txtSenderName.setText(dataSet.get(listPosition).getSender_mobilenumber() + "(" + dataSet.get(listPosition).getSender_firstname() + " " + dataSet.get(listPosition).getSender_lastname() + ")");
        if(!dataSet.get(listPosition).getTrans_id().equals("null")) {
            holder.txtTransId.setText(dataSet.get(listPosition).getTrans_id());
            holder.lnrTransId.setVisibility(View.VISIBLE);
        } else {
            holder.lnrTransId.setVisibility(View.GONE);
        }
        if(!dataSet.get(listPosition).getAmount().equals("null")) {
            holder.txtAmount.setText(Constants.addRsSymbol((Activity) context,dataSet.get(listPosition).getAmount()));
            holder.lnrAmount.setVisibility(View.VISIBLE);
        } else {
            holder.lnrAmount.setVisibility(View.GONE);
        }
        if(!dataSet.get(listPosition).getTransaction_id().equals("null")) {
            holder.txtTransactionId.setText(dataSet.get(listPosition).getTransaction_id());
            holder.lnrTransactionId.setVisibility(View.VISIBLE);
        } else {
            holder.lnrTransactionId.setVisibility(View.GONE);
        }
        if(!dataSet.get(listPosition).getBank().equals("null")) {
            holder.txtBank.setText(dataSet.get(listPosition).getBank());
            holder.lnrBank.setVisibility(View.VISIBLE);
        } else {
            holder.lnrBank.setVisibility(View.GONE);
        }
        if(!dataSet.get(listPosition).getTransaction_status().equals("null")) {
            if(dataSet.get(listPosition).getTransaction_status().equals("1")) {
                holder.txtTransactionStatus.setText("Successful");
                holder.txtTransactionStatus.setTextColor(context.getResources().getColor(R.color.colorGreen));
            } else {
                holder.txtTransactionStatus.setText("Fail");
                holder.txtTransactionStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
            }
            holder.lnrTransactionStatus.setVisibility(View.VISIBLE);
        } else {
            holder.lnrTransactionStatus.setVisibility(View.GONE);
        }
        if(!dataSet.get(listPosition).getAdd_date().equals("null")) {
            holder.txtDate.setText(Constants.parseDateToddMMyyyy(dataSet.get(listPosition).getAdd_date()));
            holder.lnrDate.setVisibility(View.VISIBLE);
        } else {
            holder.lnrDate.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
