package specificstep.com.ui.base;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import specificstep.com.R;

public abstract class ToolBarActivity<ContentFragment extends Fragment> extends BaseActivity {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    protected Integer getLayoutId() {
        return R.layout.activity_toolbar;
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
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
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
