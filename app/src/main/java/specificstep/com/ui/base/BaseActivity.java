package specificstep.com.ui.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

import javax.inject.Inject;

import specificstep.com.GlobalClasses.Constants;
import specificstep.com.R;
import specificstep.com.data.source.local.Pref;
import specificstep.com.ui.signIn.SignInActivity;
import specificstep.com.ui.signup.SignUpActivity;

public class BaseActivity extends AppCompatActivity {

    @Inject
    Pref pref;

    private BroadcastReceiver mInvalidAccessTokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_INVALID_ACCESS_TOKEN.equals(intent.getAction())) {
                //Clear preferences and logout user
                clearUserDataAndLogout();
            }
        }
    };

    private void clearUserDataAndLogout() {
        pref.clearPref();
        pref.removeValues(Pref.KEY_IS_LOGGED_IN);
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Replace a {@link Fragment} to this activity's layout
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    public void replaceFragment(int containerViewId, Fragment fragment) {
        this.replaceFragment(containerViewId, fragment, false);
    }

    /**
     * Replace a {@link Fragment} to this activity's layout
     *
     * @param containerViewId The container view to where add the fragment
     * @param fragment        The fragment to be added
     * @param addToBackStack  boolean to add fragment to back stack.
     */
    protected void replaceFragment(int containerViewId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.toString());
        }
        fragmentTransaction.commit();
    }

    public void clearBackStack(int containerViewId) {
        getSupportFragmentManager().popBackStack(containerViewId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void showInfoDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });
        dialog.show();
    }

    public void showInvalidAccessTokenPopup() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.invalid_access_token))
                .setCancelable(false)
                .setMessage(getString(R.string.invalid_token_message))
                .setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logoutUser();
                    }
                }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });
        dialog.show();
    }

    public void logoutUser() {
        /*Intent intent = new Intent(Constants.ACTION_INVALID_ACCESS_TOKEN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/
        /*Intent intent = new Intent(this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        setResult(Activity.RESULT_CANCELED);
        pref.clearPref();
        finish();*/
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        setResult(Activity.RESULT_CANCELED);
        //pref.clearPref();
        pref.setValue(Pref.KEY_IS_OTP_VERIFIED, true);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerInValidTokenBroadcastReceiver();
    }

    private void registerInValidTokenBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        mInvalidAccessTokenReceiver,
                        new IntentFilter(Constants.ACTION_INVALID_ACCESS_TOKEN));
    }

    @Override
    protected void onDestroy() {
        unRegisterInValidTokenBroadcastReceiver();
        super.onDestroy();
    }


    private void unRegisterInValidTokenBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mInvalidAccessTokenReceiver);
    }

    /*@Override
    public void onBackPressed() {

        List fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;
        for(Object f : fragmentList) {
            if(f instanceof BaseFragment) {
                handled = ((BaseFragment)f).onBackPressed();

                if(handled) {
                    break;
                }
            }
        }

        if(!handled) {
            super.onBackPressed();
        }
    }*/

}
