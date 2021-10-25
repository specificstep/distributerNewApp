package specificstep.com.data.net;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Observable;
import specificstep.com.data.entity.AutoOtpEntity;
import specificstep.com.data.entity.BaseResponse;
import specificstep.com.data.entity.CashSummaryEntity;
import specificstep.com.data.entity.CompanyEntity;
import specificstep.com.data.entity.ForgotPasswordResponse;
import specificstep.com.data.entity.OtpVerifyResponse;
import specificstep.com.data.entity.ParentUserResponse;
import specificstep.com.data.entity.ProductEntity;
import specificstep.com.data.entity.SettingEntity;
import specificstep.com.data.entity.SignInResponse;
import specificstep.com.data.entity.StateEntity;
import specificstep.com.data.entity.UserEntity;

public interface RestApi {

    Observable<BaseResponse> signUp(String userName, int userType);

    Observable<List<AutoOtpEntity>> autoOtp(String userName, int userType);

    Observable<OtpVerifyResponse> verifyOTP(String userName, String otp, int userType);

    Observable<ForgotPasswordResponse> verifyForgotOTP(String username, String otp_code, String mac_address, String app, String forgot_otp);

    Observable<List<CompanyEntity>> getCompanyList(int service);

    Observable<List<ProductEntity>> getProducts(int service);

    Observable<List<StateEntity>> getStates(int service);

    Observable<SettingEntity> getSettings();

    Observable<List<UserEntity>> getChildUsers(String userName, int userType);

    Observable<SignInResponse> login(String userName, String password, int userType, String mac_address, String otp_code, String app);

    Observable<BaseResponse> forgotpassword(String userName, String mac_address, String otp_code, String app);

    Observable<BigDecimal> getBalance();

    Observable<List<UserEntity>> getUserByEmail(String email);

    Observable<String> addBalance(String userId, String amount, String lati, String lng);

    Observable<List<CashSummaryEntity>> getCashSummary(String userId, int userType, String fromDate, String toDate);

    Observable<BaseResponse> changePassword(String password, String oldPassword);

    Observable<BaseResponse> forgotchnagepassword(String username, String otp_code, String mac_address, String app, String forgot_otp, String password, String oldPassword);

    Observable<ParentUserResponse> getParentUser();
}
