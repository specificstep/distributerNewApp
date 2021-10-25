package specificstep.com.ui.base;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.List;

import specificstep.com.R;

public class BaseFragment extends Fragment {

    public void showDialog(String title, String message) {
        showDialog(title, message, null);
    }

    public void showDialog(String title, String message, final DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onClick(dialogInterface, i);
                        }
                    }
                }).create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });
        dialog.show();
    }

    public void showInvalidAccessTokenPopup() {
        if(getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showInvalidAccessTokenPopup();
        }
    }

    public void replaceFragment(int containerViewId, Fragment fragment) {
        this.replaceFragment(containerViewId, fragment);
    }

}
