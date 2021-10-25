package specificstep.com.ui.dashboard;

import android.os.Bundle;

import javax.inject.Inject;

import specificstep.com.GlobalClasses.AppController;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.ui.base.ToolBarActivity;
import specificstep.com.ui.dashboard.DaggerDashboardComponent;

public class DashboardActivity extends ToolBarActivity<DashboardFragment> {

    @Inject
    DashboardPresenter presenter;

    @Override
    public DashboardFragment getFragmentContent() {
        return new DashboardFragment();
    }

    @Override
    public void injectDependencies(DashboardFragment fragment) {
        DaggerDashboardComponent.builder()
                .applicationComponent(((AppController)getApplication()).getApplicationComponent())
                .dashboardModule(new DashboardModule(fragment))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent().getBooleanExtra("crash", false)) {
            //Toast.makeText(this, "App restarted after crash", Toast.LENGTH_SHORT).show();
            Constants.APP_PACKAGE_NAME = getApplicationContext().getPackageName();
        }
*/
        toolbar.setLogo(Constants.changeActionbarLogo(DashboardActivity.this));
        toolbar.setTitle(Constants.changeAppName(DashboardActivity.this));
        setSupportActionBar(toolbar);
    }
}
