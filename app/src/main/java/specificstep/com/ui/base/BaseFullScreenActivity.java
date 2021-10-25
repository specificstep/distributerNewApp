package specificstep.com.ui.base;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import specificstep.com.R;

public abstract class BaseFullScreenActivity<ContentFragment extends Fragment> extends BaseActivity {
    protected Integer getLayoutId() {
        return R.layout.activity_base_full_screen;
    }

    protected Integer getFragmentContainerId() {
        return R.id.fragment_container;
    }

    public abstract ContentFragment getFragmentContent();

    public abstract void injectDependencies(ContentFragment fragment);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment == null) {
            fragment = getFragmentContent();
            replaceFragment(getFragmentContainerId(), fragment);
        }
        try {
            injectDependencies((ContentFragment) fragment);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
