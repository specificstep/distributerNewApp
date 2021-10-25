package specificstep.com.data.source.remote;

import com.google.firebase.iid.FirebaseInstanceId;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import specificstep.com.Database.ChildUserTable;
import specificstep.com.Database.DatabaseHelper;
import specificstep.com.GlobalClasses.EncryptionUtil;
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
import specificstep.com.data.net.RestApi;
import specificstep.com.data.source.UserDataStore;
import specificstep.com.data.source.local.Pref;
import specificstep.com.interactors.UserDataMapper;

@Singleton
public class CloudUserDataStore implements UserDataStore {

    private RestApi restApi;
    private DatabaseHelper databaseHelper;
    private Pref pref;
    private UserDataMapper userDataMapper;
    private EncryptionUtil encryptionUtil;
    private ChildUserTable childUserTable;

    @Inject
    CloudUserDataStore(RestApi restApi, DatabaseHelper databaseHelper, Pref pref,
                       UserDataMapper userDataMapper,
                       EncryptionUtil encryptionUtil,
                       ChildUserTable childUserTable) {
        this.restApi = restApi;
        this.databaseHelper = databaseHelper;
        this.pref = pref;
        this.userDataMapper = userDataMapper;
        this.encryptionUtil = encryptionUtil;
        this.childUserTable = childUserTable;
    }

    @Override
    public Observable<BaseResponse> signUp(final String userName, final int userType) {
        pref.removeValues(Pref.KEY_OPT_CODE, Pref.KEY_USER_ID, Pref.KEY_USER_NAME);
        return restApi.signUp(userName, userType);
    }

    /*@Override
    public Observable<AutoOtpResponse> autoOtp(final String userName, final int userType) {
        pref.removeValues(Pref.KEY_OPT_CODE, Pref.KEY_USER_ID, Pref.KEY_USER_NAME);
        return restApi.autoOtp(userName, userType);
    }*/

    @Override
    public Observable<List<AutoOtpEntity>> autoOtp(final String userName, final int userType) {
        return restApi.autoOtp(userName, userType)
                .map(new Function<List<AutoOtpEntity>, List<AutoOtpEntity>>() {
                    @Override
                    public List<AutoOtpEntity> apply(List<AutoOtpEntity> cashSummaryEntities) throws Exception {
                        return userDataMapper.mapAutoOtpList(cashSummaryEntities);
                    }
                });
    }


    @Override
    public Observable<SignInResponse> login(String userName, final String password, int userType, final boolean rememberMe, String mac_address, String otp_code, String app) {
        return restApi.login(userName, password, userType, mac_address, otp_code, app).doOnNext(new Consumer<SignInResponse>() {
            @Override
            public void accept(SignInResponse baseResponse) throws Exception {
                if (rememberMe) {
                    pref.setValue(Pref.KEY_PASSWORD, encryptionUtil.encrypt(password));
                }
                pref.setValue(Pref.KEY_REMEMBER_PASSWORD, rememberMe);
                pref.setValue(Pref.KEY_IS_LOGGED_IN, true);
                pref.setValue(Pref.KEY_MOBILE, baseResponse.getMobile());
                pref.setValue(Pref.KEY_USER_TYPE, baseResponse.getUserType());
            }
        });
    }

    @Override
    public Observable<BaseResponse> forgotpassword(final String userName, String mac_address, final String otp_code, String app) {
        return restApi.forgotpassword(userName, mac_address, otp_code, app).doOnNext(new Consumer<BaseResponse>() {
            @Override
            public void accept(BaseResponse baseResponse) throws Exception {
                pref.setValue(Pref.KEY_USER_NAME, userName);
                pref.setValue(Pref.KEY_OPT_CODE, otp_code);
            }
        });
    }

    @Override
    public Observable<BaseResponse> forgotchnagepassword(String username, String otp_code, String mac_address, String app, String forgot_otp, String password, String oldPassword) {
        return restApi.forgotchnagepassword(username, otp_code, mac_address, app, forgot_otp, password, oldPassword).doOnNext(new Consumer<BaseResponse>() {
            @Override
            public void accept(BaseResponse baseResponse) throws Exception {
                pref.removeValues(
                        Pref.KEY_IS_LOGGED_IN,
                        Pref.KEY_LAST_UPDATE_DATE,
                        Pref.KEY_PASSWORD,
                        Pref.KEY_REMEMBER_PASSWORD);
            }
        });
    }


    @Override
    public Observable<BaseResponse> verifyOTP(final String userName, final String otp, final int userType) {
        return restApi.verifyOTP(userName, otp, userType)
                .doOnNext(new Consumer<OtpVerifyResponse>() {
                    @Override
                    public void accept(OtpVerifyResponse otpVerifyResponse) throws Exception {
                        //Save details to database
                        databaseHelper.deleteDefaultSettings();
                        databaseHelper.addDefaultSettings(
                                otpVerifyResponse.getUserId(),
                                otpVerifyResponse.getStateId(),
                                otpVerifyResponse.getStateName());
                        databaseHelper.deleteUsersDetail();
                        databaseHelper.addUserDetails(otpVerifyResponse.getUserId(),
                                otp,
                                userName,
                                FirebaseInstanceId.getInstance().getToken(),
                                otpVerifyResponse.getName(),
                                "0");
                        pref.setValue(Pref.KEY_USER_ID, otpVerifyResponse.getUserId());
                        pref.setValue(Pref.KEY_USER_NAME, userName);
                        pref.setValue(Pref.KEY_USER_TYPE, userType);
                        pref.setValue(Pref.KEY_OPT_CODE, otp);
                        pref.setValue(Pref.KEY_USER_FNAME, otpVerifyResponse.getName());
                        pref.setValue(Pref.KEY_IS_OTP_VERIFIED, true);
                    }
                }).map(new Function<OtpVerifyResponse, BaseResponse>() {
                    @Override
                    public BaseResponse apply(OtpVerifyResponse otpVerifyResponse) throws Exception {
                        return otpVerifyResponse;
                    }
                });
    }

    @Override
    public Observable<ForgotPasswordResponse> verifyForgotOTP(final String username, final String otp_code, final String mac_address, final String app, final String forgot_otp) {
        return restApi.verifyForgotOTP(username, otp_code, mac_address, app, forgot_otp).doOnNext(new Consumer<ForgotPasswordResponse>() {
            @Override
            public void accept(ForgotPasswordResponse baseResponse) throws Exception {
                pref.setValue(Pref.KEY_USER_NAME, username);
                pref.setValue(Pref.KEY_OPT_CODE, otp_code);
            }
        });
    }

    @Override
    public Observable<List<Company>> getCompanyList(final int service) {
        return restApi.getCompanyList(service).map(new Function<List<CompanyEntity>, List<Company>>() {
            @Override
            public List<Company> apply(List<CompanyEntity> companyEntities) throws Exception {
                return userDataMapper.transform(companyEntities);
            }
        }).doOnNext(new Consumer<List<Company>>() {
            @Override
            public void accept(List<Company> companyList) throws Exception {
                for (Company company : companyList) {
                    company.setService_type(String.valueOf(service));
                }
                databaseHelper.deleteCompanyDetail(String.valueOf(service));
                databaseHelper.addCompanysDetails(companyList);
            }
        });
    }

    @Override
    public Observable<List<Product>> getProducts(final int service) {
        return restApi.getProducts(service)
                .map(new Function<List<ProductEntity>, List<Product>>() {
                    @Override
                    public List<Product> apply(List<ProductEntity> productEntities) throws Exception {
                        return userDataMapper.transformProducts(productEntities);
                    }
                })
                .doOnNext(new Consumer<List<Product>>() {
                    @Override
                    public void accept(List<Product> productList) throws Exception {
                        for (Product product : productList) {
                            product.setService_type(String.valueOf(service));
                        }
                        databaseHelper.deleteProductDetail(String.valueOf(service));
                        databaseHelper.addProductsDetails(productList);
                    }
                });
    }

    @Override
    public Observable<List<State>> getStates(int service) {
        return restApi.getStates(service)
                .map(new Function<List<StateEntity>, List<State>>() {
                    @Override
                    public List<State> apply(List<StateEntity> stateEntities) throws Exception {
                        return userDataMapper.transformStates(stateEntities);
                    }
                })
                .doOnNext(new Consumer<List<State>>() {
                    @Override
                    public void accept(List<State> stateList) throws Exception {
                        databaseHelper.deleteStateDetail();
                        databaseHelper.addStatesDetails(stateList);
                    }
                });
    }

    @Override
    public Observable<List<Color>> getSettings() {
        return restApi.getSettings().map(new Function<SettingEntity, List<Color>>() {
            @Override
            public List<Color> apply(SettingEntity settingEntity) throws Exception {
                return userDataMapper.transformColors(settingEntity.getColors());
            }
        }).doOnNext(new Consumer<List<Color>>() {
            @Override
            public void accept(List<Color> colorList) throws Exception {
                databaseHelper.deleteStatusColor();
                databaseHelper.addColors(colorList);
            }
        });
    }

    @Override
    public Observable<List<UserList>> getChildUsers(String userName, int userType) {
        return restApi.getChildUsers(userName,userType)
                .doOnNext(new Consumer<List<UserEntity>>() {
                    @Override
                    public void accept(List<UserEntity> userLists) throws Exception {
                        if(userLists.size() > 0) {
                            System.out.println("Child data size: " + userLists.size());
                            childUserTable.delete_All();
                            for (UserEntity userList : userLists) {
                                ChildUserModel childUserModel = userDataMapper.map(userList);
                                childUserTable.insert(childUserModel);
                            }
                        }
                    }
                })
                .map(new Function<List<UserEntity>, List<UserList>>() {
                    @Override
                    public List<UserList> apply(List<UserEntity> userEntities) throws Exception {
                        return userDataMapper.transformUsers(userEntities);
                    }
                })
                .doAfterNext(new Consumer<List<UserList>>() {
                    @Override
                    public void accept(List<UserList> userEntities) throws Exception {
                        if(userEntities != null && userEntities.size() > 0) {
                            databaseHelper.deleteUserListDetail();
                            databaseHelper.addUserListDetails(userEntities);
                        }

                    }
                });
    }

    @Override
    public Observable<BigDecimal> getBalance() {
        return restApi.getBalance().doOnNext(new Consumer<BigDecimal>() {
            @Override
            public void accept(BigDecimal bigDecimal) throws Exception {
                if(bigDecimal != null) {
                    pref.setValue(Pref.KEY_BALANCE, bigDecimal.toString());
                }
            }
        });
    }

    @Override
    public Observable<ChildUserModel> getUser(String email) {
        return restApi.getUserByEmail(email).map(new Function<List<UserEntity>, ChildUserModel>() {
            @Override
            public ChildUserModel apply(List<UserEntity> userEntities) throws Exception {
                return userDataMapper.mapUsers(userEntities);
            }
        });
    }

    @Override
    public Observable<String> addBalance(String userId, String amount, String lati, String lng) {
        return restApi.addBalance(userId, amount, lati, lng);
    }

    @Override
    public Observable<List<CashSummaryModel>> getCashSummary(String userId, int userType, String fromDate, String toDate) {
        return restApi.getCashSummary(userId, userType, fromDate, toDate)
                .map(new Function<List<CashSummaryEntity>, List<CashSummaryModel>>() {
                    @Override
                    public List<CashSummaryModel> apply(List<CashSummaryEntity> cashSummaryEntities) throws Exception {
                        return userDataMapper.mapCashSummaryList(cashSummaryEntities);
                    }
                });
    }

    @Override
    public Observable<BaseResponse> changePassword(String password, String oldPassword) {
        return restApi.changePassword(password, oldPassword).doOnNext(new Consumer<BaseResponse>() {
            @Override
            public void accept(BaseResponse baseResponse) throws Exception {
                pref.removeValues(
                        Pref.KEY_IS_LOGGED_IN,
                        Pref.KEY_LAST_UPDATE_DATE,
                        Pref.KEY_PASSWORD,
                        Pref.KEY_REMEMBER_PASSWORD);
            }
        });
    }

    @Override
    public Observable<ParentUser> getParentUser() {
        return restApi.getParentUser().map(new Function<ParentUserResponse, ParentUser>() {
            @Override
            public ParentUser apply(ParentUserResponse parentUserResponse) throws Exception {
                return userDataMapper.mapParentUserEntity(parentUserResponse);
            }
        });
    }
}
