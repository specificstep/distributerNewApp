package specificstep.com.data.source;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import specificstep.com.Models.CashSummaryModel;
import specificstep.com.Models.ChildUserModel;
import specificstep.com.Models.Color;
import specificstep.com.Models.Company;
import specificstep.com.Models.ParentUser;
import specificstep.com.Models.Product;
import specificstep.com.Models.State;
import specificstep.com.Models.UserList;
import specificstep.com.data.entity.AutoOtpEntity;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.data.entity.ForgotPasswordResponse;
import specificstep.com.data.entity.SignInResponse;
import specificstep.com.interactors.repositories.UserRepository;

@Singleton
public class UserDataRepository implements UserRepository {

    private UserDataStore userDataStore;

    @Inject
    public UserDataRepository(UserDataStore dataStore) {
        this.userDataStore = dataStore;
    }

    @Override
    public Observable<BaseResponse> signUp(String userName, int userType) {
        return userDataStore.signUp(userName, userType);
    }

    @Override
    public Observable<List<AutoOtpEntity>> autoOtp(String userName, int userType) {
        return userDataStore.autoOtp(userName, userType);
    }

    @Override
    public Observable<BaseResponse> login(String userName, String password, int userType, boolean rememberMe, String mac_address, String otp_code, String app) {
        return userDataStore.login(userName, password, userType, rememberMe, mac_address, otp_code, app).map(new Function<SignInResponse, BaseResponse>() {
            @Override
            public BaseResponse apply(SignInResponse signInResponse) throws Exception {
                return signInResponse;
            }
        });
    }

    @Override
    public Observable<BaseResponse> forgotpassword(String userName, String mac_address, String otp_code, String app) {
        return userDataStore.forgotpassword(userName, mac_address, otp_code, app).map(new Function<BaseResponse,BaseResponse>() {
            @Override
            public BaseResponse apply(BaseResponse signInResponse) throws Exception {
                return signInResponse;
            }
        });
    }

    @Override
    public Observable<BaseResponse> forgotchnagepassword(String username, String otp_code, String mac_address, String app, String forgot_otp, String password, String oldPassword) {
        return userDataStore.forgotchnagepassword(username, otp_code, mac_address, app, forgot_otp, password, oldPassword).map(new Function<BaseResponse,BaseResponse>() {
            @Override
            public BaseResponse apply(BaseResponse signInResponse) throws Exception {
                return signInResponse;
            }
        });
    }

    @Override
    public Observable<BaseResponse> verifyOTP(String userName, String otp, int userType) {
        return userDataStore.verifyOTP(userName, otp, userType);
    }

    @Override
    public Observable<ForgotPasswordResponse> verifyForgotOTP(String username, String otp_code, String mac_address, String app, String forgot_otp) {
        return userDataStore.verifyForgotOTP(username, otp_code, mac_address, app, forgot_otp);
    }

    @Override
    public Observable<List<Company>> getCompanyList(int service) {
        return userDataStore.getCompanyList(service);
    }

    @Override
    public Observable<List<Product>> getProducts(int service) {
        return userDataStore.getProducts(service);
    }

    @Override
    public Observable<List<State>> getStates(int service) {
        return userDataStore.getStates(service);
    }

    @Override
    public Observable<List<Color>> getSettings() {
        return userDataStore.getSettings();
    }

    @Override
    public Observable<List<UserList>> getChildUsers(String userName, int userType) {
        return userDataStore.getChildUsers(userName,userType);
    }

    @Override
    public Observable<BigDecimal> getBalance() {
        return userDataStore.getBalance();
    }

    @Override
    public Observable<ChildUserModel> getChildUser(String email) {
        return userDataStore.getUser(email);
    }

    @Override
    public Observable<String> addBalance(String userId, String amount, String lati, String lng) {
        return userDataStore.addBalance(userId, amount, lati, lng);
    }

    @Override
    public Observable<List<CashSummaryModel>> getCashSummary(String userId, int userType, String fromDate, String toDate) {
        return userDataStore.getCashSummary(userId, userType, fromDate, toDate);
    }

    @Override
    public Observable<BaseResponse> changePassword(String password, String oldPassword) {
        return userDataStore.changePassword(password, oldPassword);
    }

    @Override
    public Observable<ParentUser> getParentUsers() {
        return userDataStore.getParentUser();
    }
}
