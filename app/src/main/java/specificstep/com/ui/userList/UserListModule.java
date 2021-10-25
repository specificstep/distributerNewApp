package specificstep.com.ui.userList;

import dagger.Module;
import dagger.Provides;

@Module
class UserListModule {

    private final UserListContract.View view;

    UserListModule(UserListContract.View view) {
        this.view = view;
    }

    @Provides
    UserListContract.View providesUserListView() {
        return view;
    }

    @Provides
    UserListContract.Presenter providesUserListPresenter(UserListPresenter presenter) {
        return presenter;
    }
}
