package specificstep.com.ui.splash;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.ExecutionException;

import specificstep.com.GlobalClasses.Constants;
import specificstep.com.GlobalClasses.VersionChecker;
import specificstep.com.R;
import specificstep.com.ui.base.BaseFragment;
import specificstep.com.ui.dashboard.DashboardActivity;
import specificstep.com.ui.signIn.SignInActivity;
import specificstep.com.ui.signup.SignUpActivity;


public class SplashFragment extends BaseFragment implements SplashContract.View/*, LocationListener */{

    private SplashContract.Presenter presenter;


    private Handler handler;

    @Override
    public void setPresenter(SplashContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        System.out.println("Package name: " + getContext().getPackageName());
        Constants.APP_PACKAGE_NAME = getContext().getPackageName();

        //set background as per package name
        //Constants.chaneBackground(SplashActivity.this,(LinearLayout) findViewById(R.id.lnrSplash));

        //set icon as per package name
        Constants.chaneIcon(getActivity(),(ImageView) view.findViewById(R.id.imageView));

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("FCM token: " + refreshedToken);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();
        presenter.initialize(getActivity());
    }

    @Override
    public void scheduleTimeout(long milliSeconds) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Constants.checkInternet(getActivity())) {
                    getCurrentVersion();
                } else {
                    presenter.onTimeoutCompleted();
                }
            }
        }, milliSeconds);
    }

    @Override
    public void startMainScreen() {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!getActivity().isFinishing()) {
                        Intent intent = new Intent(getActivity(), DashboardActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
        }

    }

    @Override
    public void startLoginScreen() {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!getActivity().isFinishing()) {
                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
        }
    }

    @Override
    public void startSignUpScreen() {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!getActivity().isFinishing()) {
                        Intent intent = new Intent(getActivity(), SignUpActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
        }
    }

    @Override
    public Context context() {
        return getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void createFirstTimeNotification() {
        androidx.core.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(Constants.chaneIcon(getActivity()))
                        .setContentTitle(Constants.changeAppName(getActivity()))
                        .setContentText("App installed successfully and shortcut created.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        }
        // Dismiss notification after action has been clicked
        mBuilder.setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getActivity(), SplashActivity.class);
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SplashActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void showErrorDialog(@StringRes int strResId) {
        showDialog(getString(R.string.error), getString(strResId), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        handler = new Handler();
        presenter.initialize(getActivity());

    }

    String currentVersion, latestVersion;
    Dialog dialog;
    public void getCurrentVersion(){
        PackageManager pm = getActivity().getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo =  pm.getPackageInfo(getActivity().getPackageName(),0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = String.valueOf(pInfo.versionName);

        try {
            VersionChecker versionChecker = new VersionChecker();
            latestVersion = versionChecker.execute().get();
            if(latestVersion!=null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)){
                    /*if(!isFinishing()){*/ //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                    showUpdateDialog();
                    //}
                } else {
                    presenter.onTimeoutCompleted();
                }
            }
            else
                presenter.onTimeoutCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
            presenter.onTimeoutCompleted();
        } catch (ExecutionException e) {
            e.printStackTrace();
            presenter.onTimeoutCompleted();
        }

    }

    private void showUpdateDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("A New Update is Available");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=" + Constants.APP_PACKAGE_NAME)));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //presenter.onTimeoutCompleted();
                getActivity().finish();
                getActivity().moveTaskToBack(true);
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();
    }
}
