package specificstep.com.ui.notification;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import specificstep.com.Database.NotificationTable;
import specificstep.com.GlobalClasses.Constants;
import specificstep.com.Models.NotificationModel;
import specificstep.com.utility.DateTime;

class NotificationPresenter implements NotificationContract.Presenter {

    private final NotificationContract.View view;
    private final NotificationTable notificationTable;
    private DisposableObserver<List<NotificationModel>> notificationListObservable;
    private DisposableObserver<Integer> notificationCountObservable;

    @Inject
    NotificationPresenter(NotificationContract.View view, NotificationTable notificationTable) {
        this.view = view;
        this.notificationTable = notificationTable;
    }

    @Override
    public void start() {
        view.registerNotificationBroadcastReceiver(Constants.ACTION_NOTIFICATION_UPDATE);
    }

    @Override
    public void stop() {
        view.unRegisterNotificationBroadcastReceiver();
    }

    @Override
    public void destroy() {
        if (notificationListObservable != null && !notificationListObservable.isDisposed()) {
            notificationListObservable.dispose();
        }
        if (notificationCountObservable != null && !notificationCountObservable.isDisposed()) {
            notificationCountObservable.dispose();
        }
    }

    @Override
    public void initialize() {
        onNotificationRefreshed();
    }

    @Override
    public void initializeWithId(int notificationId) {
        ArrayList<NotificationModel> notificationData = notificationTable.getNotificationData(String.valueOf(notificationId));
        if(notificationData != null && notificationData.size() > 0) {
            view.showNotificationDetailDialog(notificationData.get(0));
        }
        onNotificationRefreshed();
    }

    @Override
    public void onNotificationRefreshed() {
        fetchNotificationList();
        fetchNotificationCount();
    }

    @Override
    public void onNotificationItemClicked(NotificationModel notificationModel) {
        view.showNotificationDetailDialog(notificationModel);
    }

    @Override
    public void onNotificationPopupOkButtonClicked(NotificationModel notificationModel) {
        if (TextUtils.equals(notificationModel.readFlag, "0")) {

            notificationModel.readFlag = "1";
            notificationModel.readDateTime = DateTime.getCurrentDateTime();


            notificationTable.updateNotification(notificationModel, notificationModel.id);
            view.refreshNotificationListView();
            fetchNotificationCount();
        }
    }

    private void fetchNotificationCount() {
        notificationCountObservable = Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return notificationTable.getNumberOfNotificationRecord();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer value) {
                        view.updateNotificationCount(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void fetchNotificationList() {
        view.showLoadingView();
        notificationListObservable = Observable.fromCallable(new Callable<List<NotificationModel>>() {
            @Override
            public List<NotificationModel> call() throws Exception {
                return fetchNotificationsFromDatabase();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<List<NotificationModel>>() {
                    @Override
                    public void onNext(List<NotificationModel> value) {
                        view.hideLoadingView();
                        if (value.size() > 0) {
                            view.showNotificationListView();
                            view.setNotificationAdapter(value);
                            view.hideEmptyTextView();
                        } else {
                            view.hideNotificationListView();
                            view.showEmptyTextView();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideLoadingView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private List<NotificationModel> fetchNotificationsFromDatabase() {
        return notificationTable.getNotificationData_OrderBy();
    }
}
