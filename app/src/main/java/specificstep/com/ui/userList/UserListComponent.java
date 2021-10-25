package specificstep.com.ui.userList;

import dagger.Component;
import specificstep.com.di.FragmentScoped;
import specificstep.com.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = UserListModule.class)
public interface UserListComponent {
    void inject(UserListFragment fragment);
}
