package specificstep.com.ui.PaymentRequest;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.CheckConnection;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.MCrypt;
import specificstep.com.GlobalClasses.TransparentProgressDialog;
import specificstep.com.GlobalClasses.URL;
import specificstep.com.Models.AccountLedgerModel;
import specificstep.com.Models.CashbookModel;
import specificstep.com.Models.Color;
import specificstep.com.Models.DMTPaymentListModel;
import specificstep.com.Models.Default;
import specificstep.com.Models.PaymentRequestListModel;
import specificstep.com.Models.Recharge;
import specificstep.com.Models.User;
import specificstep.com.Models.WalletsModel;
import specificstep.com.R;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.home.HomeContract;
import specificstep.com.utility.InternetUtil;
import specificstep.com.utility.LogMessage;
import specificstep.com.utility.Utility;

public class PaymentRequestDetail extends BaseFragment {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private final int SUCCESS = 1, ERROR = 2, SUCCESS_COMPLAIN = 3,
            SUCCESS_REASON = 4, SUCCESS_WALLET_LIST = 5;
    ImageButton back, wallet;
    ImageView img;
    ImageView imgStatus, img1;
    TextView txtRemark, txtDate, txtAmount, txtTitle, txtCompany,
            txtSender, txtTransactionId, txtOperatorId, txtTitleText;

    //receipt
    ImageView imgReceipt;
    ImageView imgStatusReceipt, imgReceipt1;
    TextView txtRemarkReceipt, txtDateReceipt, txtAmountReceipt,
            txtCompanyReceipt, txtSenderReceipt, txtTransactionIdReceipt,
            txtOperatorIdReceipt, txtTitleTextReceipt;

    LinearLayout lnrDownload;
    //PaymentRequestListModel paymentRequestModel;
    ArrayList<Color> colorArrayList;
    String _color_name, color_value;
    String from, receipt_type;
    List<Default> userList;
    DatabaseHelper databaseHelper;
    LinearLayout lnrComplain;
    private TransparentProgressDialog transparentProgressDialog;
    ArrayList<User> userArrayList;

    //DMT detail
    LinearLayout lnrRecSearch;
    //dmt receipt detail
    LinearLayout lnrRecSearchReceipt;

    LinearLayout lnrListDetail;
    private int start = 0, end = 10;

    BottomSheetDialog alertDialogBuilder;
    //complain thread
    ListView recyclerViewComplain;
    String paymentId = "", paymentTo = "", paymentType = "", receipt = "",
            payment_time = "";
    //multi wallet 27-5-2019
    ArrayList<WalletsModel> walletsModelList;
    ArrayList<String> walletsList;
    WalletsModel walletsModel;
    ArrayList<String> menuWallet;
    private String strMacAddress, strUserName, strOtpCode, strRegistrationDateTime;
    View view;
    String str;
    PaymentRequestListModel paymentRequestModel;
    private HomeContract.HomeDelegate homeDelegate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeContract.HomeDelegate) {
            homeDelegate = (HomeContract.HomeDelegate) context;
        }
    }

    @Override
    public void onDetach() {
        homeDelegate = null;
        super.onDetach();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (homeDelegate != null) {
            homeDelegate.setToolBarTitle(getString(R.string.payment_request_detail));
        }
    }

    public PaymentRequestDetail() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payment_request_detail, container, false);
        str = getArguments().getString("detail");
        paymentRequestModel = new Gson().fromJson(str, PaymentRequestListModel.class);

        initialize();
        try {

            //lnrDmt.setVisibility(View.GONE);
            lnrRecSearch.setVisibility(View.VISIBLE);

            //txtTitle.setText("Payment Request Detail");
            txtTitleText.setText("Payment Request Detail");
            //paymentRequestModel = getIntent().getExtras().getParcelable("classdata");

            if (!paymentRequestModel.getRemark().equals("null")) {
                txtTransactionId.setText("Remark: " + paymentRequestModel.getRemark());
                txtTransactionId.setVisibility(View.VISIBLE);
            } else {
                txtTransactionId.setVisibility(View.GONE);
            }
            if (!paymentRequestModel.getDeposit_bank().equals("null")) {
                txtCompany.setText(paymentRequestModel.getDeposit_bank());
                txtCompany.setVisibility(View.VISIBLE);
            } else {
                txtCompany.setVisibility(View.GONE);
            }

            if (!paymentRequestModel.getAdmin_remark().equals("null")) {
                txtSender.setText(paymentRequestModel.getAdmin_remark());
                txtSender.setVisibility(View.VISIBLE);
            } else {
                txtSender.setVisibility(View.GONE);
            }

            if (!paymentRequestModel.getWallet_name().equals("null")) {
                txtOperatorId.setText(paymentRequestModel.getWallet_name());
                txtOperatorId.setVisibility(View.VISIBLE);
            } else {
                txtOperatorId.setVisibility(View.GONE);
            }

            lnrComplain.setVisibility(View.GONE);
            txtRemark.setVisibility(View.GONE);

            if (!paymentRequestModel.getAmount().equals("null")) {
                txtAmount.setText(getResources().getString(R.string.Rs) + " " + paymentRequestModel.getAmount());
                txtAmount.setVisibility(View.VISIBLE);
            } else {
                txtAmount.setVisibility(View.GONE);
            }

            img1.setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);

            txtDate.setText(Constants.commonDateFormate(paymentRequestModel.getDatetime(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy hh:mm aa"));

            lnrDownload.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    paymentId = paymentRequestModel.getRemark();
                    paymentTo = paymentRequestModel.getDeposit_bank();
                    paymentType = "normal";
                    receipt = "Payment Request Receipt";
                    payment_time = Constants.commonDateFormate(paymentRequestModel.getDatetime(), "yyyy-MM-dd HH:mm:ss", "HH_mm_ss");
                    if (Build.VERSION.SDK_INT >= 23) {
                        readContactPermission(paymentRequestModel.getDeposit_bank(), paymentRequestModel.getRemark() + payment_time, "normal", receipt);
                    } else {
                        takeScreenshot(paymentRequestModel.getDeposit_bank(), paymentRequestModel.getRemark() + payment_time, "normal", receipt);
                    }
                }
            });

            databaseHelper = new DatabaseHelper(getActivity());
            colorArrayList = databaseHelper.getAllColors();
            /*Set color of recharge status*/
            if (paymentRequestModel.getStatus().equalsIgnoreCase("success")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("success")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        txtAmount.setTextColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                imgStatus.setColorFilter(android.graphics.Color.parseColor(color_value));
                lnrDownload.setVisibility(View.VISIBLE);
            } else if (paymentRequestModel.getStatus().equalsIgnoreCase("pending")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("pending")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        txtAmount.setTextColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                imgStatus.setColorFilter(android.graphics.Color.parseColor(color_value));
                lnrDownload.setVisibility(View.GONE);
            } else if (paymentRequestModel.getStatus().equalsIgnoreCase("failure")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("failure")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        txtAmount.setTextColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_uncheck));
                imgStatus.setColorFilter(android.graphics.Color.parseColor(color_value));
                lnrDownload.setVisibility(View.GONE);
            }
            /* [START] - recharge_status":"Credit" */
            else if (paymentRequestModel.getStatus().equalsIgnoreCase("credit")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("credit")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        txtAmount.setTextColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                imgStatus.setColorFilter(android.graphics.Color.parseColor(color_value));
                lnrDownload.setVisibility(View.GONE);
            }

            //Receipt detail
            txtTitleTextReceipt.setText("Payment Request Detail");
            //paymentRequestModel = getIntent().getExtras().getParcelable("classdata");

            if (!paymentRequestModel.getRemark().equals("null")) {
                txtTransactionIdReceipt.setText("Transaction Id: " + paymentRequestModel.getRemark());
                txtTransactionIdReceipt.setVisibility(View.VISIBLE);
            } else {
                txtTransactionIdReceipt.setVisibility(View.GONE);
            }
            if (!paymentRequestModel.getDeposit_bank().equals("null")) {
                txtCompanyReceipt.setText(paymentRequestModel.getDeposit_bank());
                txtCompanyReceipt.setVisibility(View.VISIBLE);
            } else {
                txtCompanyReceipt.setVisibility(View.GONE);
            }

            if (!paymentRequestModel.getAdmin_remark().equals("null")) {
                txtSenderReceipt.setText(paymentRequestModel.getAdmin_remark());
                txtSenderReceipt.setVisibility(View.VISIBLE);
            } else {
                txtSenderReceipt.setVisibility(View.GONE);
            }

            if (!paymentRequestModel.getWallet_name().equals("null")) {
                txtOperatorIdReceipt.setText(paymentRequestModel.getWallet_name());
                txtOperatorIdReceipt.setVisibility(View.VISIBLE);
            } else {
                txtOperatorIdReceipt.setVisibility(View.GONE);
            }

            txtRemarkReceipt.setVisibility(View.GONE);

            if (!paymentRequestModel.getAmount().equals("null")) {
                txtAmountReceipt.setText(getResources().getString(R.string.Rs) + " " + paymentRequestModel.getAmount());
                txtAmountReceipt.setVisibility(View.VISIBLE);
            } else {
                txtAmountReceipt.setVisibility(View.GONE);
            }

            txtDateReceipt.setText(Constants.commonDateFormate(paymentRequestModel.getDatetime(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy hh:mm aa"));
            databaseHelper = new DatabaseHelper(getActivity());
            colorArrayList = databaseHelper.getAllColors();
            /*Set color of recharge status*/
            if (paymentRequestModel.getStatus().equalsIgnoreCase("success")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("success")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        txtAmountReceipt.setTextColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                imgStatusReceipt.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                imgStatusReceipt.setColorFilter(android.graphics.Color.parseColor(color_value));
            } else if (paymentRequestModel.getStatus().equalsIgnoreCase("pending")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("pending")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        txtAmountReceipt.setTextColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                imgStatusReceipt.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                imgStatusReceipt.setColorFilter(android.graphics.Color.parseColor(color_value));
            } else if (paymentRequestModel.getStatus().equalsIgnoreCase("failure")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("failure")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        txtAmountReceipt.setTextColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                imgStatusReceipt.setImageDrawable(getResources().getDrawable(R.drawable.ic_uncheck));
                imgStatusReceipt.setColorFilter(android.graphics.Color.parseColor(color_value));
            }
            /* [START] - recharge_status":"Credit" */
            else if (paymentRequestModel.getStatus().equalsIgnoreCase("credit")) {
                for (int i = 0; i < colorArrayList.size(); i++) {
                    _color_name = colorArrayList.get(i).getColor_name();
                    if (_color_name.contains("credit")) {
                        color_value = colorArrayList.get(i).getColo_value();
                        txtAmountReceipt.setTextColor(android.graphics.Color.parseColor(color_value));
                    }
                }
                imgStatusReceipt.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                imgStatusReceipt.setColorFilter(android.graphics.Color.parseColor(color_value));
            }
            // [END]
            //}

        } catch (Exception e) {
            System.out.println("List Detail Error: " + e.toString());
        }

        return view;
    }

    public void initialize() {

        lnrListDetail = (LinearLayout) view.findViewById(R.id.lnrListDetail);
        img = (ImageView) view.findViewById(R.id.imgDetail);
        img1 = (ImageView) view.findViewById(R.id.imgDetail1);
        imgStatus = (ImageView) view.findViewById(R.id.imgRechargeStatusIcon);
        txtRemark = (TextView) view.findViewById(R.id.txtDetailRemark);
        txtDate = (TextView) view.findViewById(R.id.txtDetailDate);
        txtAmount = (TextView) view.findViewById(R.id.txtDetailAmount);
        txtCompany = (TextView) view.findViewById(R.id.txtDetailCompany);
        txtSender = (TextView) view.findViewById(R.id.txtDetailSenderName);
        txtTransactionId = (TextView) view.findViewById(R.id.txtDetailTransactionId);
        txtOperatorId = (TextView) view.findViewById(R.id.txtDetailOperatorId);
        txtTitleText = (TextView) view.findViewById(R.id.txtDetailTitleText);
        lnrComplain = (LinearLayout) view.findViewById(R.id.lnrComplain);
        lnrDownload = (LinearLayout) view.findViewById(R.id.lnrDetailDownload);

        //receipt
        imgReceipt = (ImageView) view.findViewById(R.id.imgDetailRecept);
        imgReceipt1 = (ImageView) view.findViewById(R.id.imgDetailRecept1);
        imgStatusReceipt = (ImageView) view.findViewById(R.id.imgRechargeStatusIconRecept);
        txtRemarkReceipt = (TextView) view.findViewById(R.id.txtDetailRemarkRecept);
        txtDateReceipt = (TextView) view.findViewById(R.id.txtDetailDateRecept);
        txtAmountReceipt = (TextView) view.findViewById(R.id.txtDetailAmountRecept);
        txtCompanyReceipt = (TextView) view.findViewById(R.id.txtDetailCompanyRecept);
        txtSenderReceipt = (TextView) view.findViewById(R.id.txtDetailSenderNameRecept);
        txtTransactionIdReceipt = (TextView) view.findViewById(R.id.txtDetailTransactionIdRecept);
        txtOperatorIdReceipt = (TextView) view.findViewById(R.id.txtDetailOperatorIdRecept);
        txtTitleTextReceipt = (TextView) view.findViewById(R.id.txtDetailTitleTextRecept);

        databaseHelper = new DatabaseHelper(getActivity());
        userArrayList = new ArrayList<User>();
        userArrayList = databaseHelper.getUserDetail();
        // Store user information in variables
        strMacAddress = userArrayList.get(0).getDevice_id();
        strUserName = userArrayList.get(0).getUser_name();
        strOtpCode = userArrayList.get(0).getOtp_code();
        strRegistrationDateTime = userArrayList.get(0).getReg_date();

        //dmt detail
        lnrRecSearch = (LinearLayout) view.findViewById(R.id.lnrRecSearchDetail);
        //dmt receipt detail
        lnrRecSearchReceipt = (LinearLayout) view.findViewById(R.id.lnrRecSearchDetailRecept);
        alertDialogBuilder = new BottomSheetDialog(getActivity());

        //complain thread list
        recyclerViewComplain = (ListView) view.findViewById(R.id.lstListDetailComplain);
        //lnrComplainThread = (LinearLayout) findViewById(R.id.lnrDetailComplain);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void readContactPermission(String mobile, String payment_id, String type, String receipt) {
        System.out.println("Checking permission.");
        // BEGIN_INCLUDE(READ_CONTACTS)
        // Check if the READ_CONTACTS permission is already available.
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Phone state permission has not been granted.
            requestReadContactPermission();
        } else {
            // Read SMS permissions is already available, show the camera preview.
            System.out.println("Read contact permission has already been granted.");
            takeScreenshot(mobile, payment_id, type, receipt);
        }
        // END_INCLUDE(READ_PHONE_STATE)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            System.out.println("Received response for Read SMS permission request.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Read SMS permission has been granted
                System.out.println("Write external permission has now been granted.");
                // Ask user for grand READ_PHONE_STATE permission.
                readContactPermission(paymentTo, paymentId + payment_time, paymentType, receipt);
            } else {
                System.out.println("Write external permission was NOT granted.");
                Toast.makeText(getActivity(), "Please grant the permission to download your receipt.",Toast.LENGTH_LONG).show();
                // again force fully prompt to user for grand permission.
                //readContactPermission();
            }
            // END_INCLUDE(permission_result)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestReadContactPermission() {
        System.out.println("Read phone state permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(READ_PHONE_STATE)
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            System.out.println("Displaying READ_CONTACTS permission rationale to provide additional context.");
            // Force fully user to grand permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // READ_CONTACTS permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        // END_INCLUDE(READ_PHONE_STATE)
    }

    Bitmap bitmap;

    View v1 = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void takeScreenshot(String mobile, String payment_id, String type, String receipt) {
        final String folder_name = "/" + Constants.changeAppName(getActivity()) + "/" + receipt + "/";
        try {
            File mydir = new File(Environment.getExternalStorageDirectory() + folder_name);
            if (!mydir.exists())
                mydir.mkdirs();
            else
                System.out.println("error: dir. already exists");
            //if (type.equals("normal")) {
                v1 = view.findViewById(R.id.lnrRecSearchDetailRecept);
            /*} else {
                v1 = findViewById(R.id.lnrDmtDetailReceipt);// get ur root view id
            }
            lnrDmtFeesReceipt.setVisibility(View.GONE);*/
            v1.setDrawingCacheEnabled(true);
            v1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v1.layout(0, 0, v1.getMeasuredWidth(), v1.getMeasuredHeight());
            v1.buildDrawingCache(true);
            v1.setDrawingCacheBackgroundColor(getResources().getColor(R.color.colorWhite));
            bitmap = loadBitmapFromView(v1, v1.getWidth(), v1.getHeight());
            v1.setDrawingCacheEnabled(false);


            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(v1.getWidth(), v1.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();

            Paint paint = new Paint();
            canvas.drawPaint(paint);

            bitmap = Bitmap.createScaledBitmap(bitmap, v1.getWidth(), v1.getHeight(), true);

            paint.setColor(android.graphics.Color.BLUE);
            canvas.drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);


            //save image in gallery
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
            final File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + folder_name + File.separator + payment_id + ".pdf");
            if (f.exists()) {
//                openImage(f);
                openGeneratedPDF(f.getPath());
            } else {
                document.writeTo(new FileOutputStream(f));
                document.close();
                Snackbar snackbar = Snackbar
                        .make(lnrListDetail, "Payment Receipt download successfully.", Snackbar.LENGTH_LONG)
                        .setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openGeneratedPDF(f.getPath());
                            }
                        });
                View view = snackbar.getView();
                TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
                tv.setTextColor(android.graphics.Color.WHITE);
                snackbar.show();
            }
        } catch (Exception e) {
            System.out.println("Download Receipt Error: " + e.toString());
            Toast.makeText(getActivity(), "Payment Receipt download fail.", Toast.LENGTH_LONG).show();
        }
    }

    private void openGeneratedPDF(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT <= 19) {
            target.setDataAndType(Uri.fromFile(file), "application/pdf");
        } else {
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(getActivity(), Constants.APP_PACKAGE_NAME + ".provider", file);
            target.setDataAndType(uri, "application/pdf");
        }
        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);
        return b;
    }

}
