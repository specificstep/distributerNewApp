package specificstep.com.ui.notification;

import dagger.Module;
import dagger.Provides;

@Module
class NotificationModule {

    private final NotificationContract.View view;

    NotificationModule(NotificationContract.View view) {
        this.view = view;
    }

    @Provides
    NotificationContract.View providesNotificationView() {
        return view;
    }

    @Provides
    NotificationContract.Presenter providesNotificationPresenter(NotificationPresenter presenter) {
        return presenter;
    }
}
