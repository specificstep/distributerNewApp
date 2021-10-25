package specificstep.com.data.net.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import specificstep.com.data.entity.BaseResponse;

public interface RechargeService {

    @FormUrlEncoded
    @POST("register")
    Call<String> signUp(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("skipotp")
    Call<String> autoOtp(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("register")
    Call<String> verifyOtp(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgototp")
    Call<String> verifyForgotOtp(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("company")
    Call<String> getCompanyList(@Field("service") int service);

    @FormUrlEncoded
    @POST("product")
    Call<String> getProducts(@Field("service") int service);

    @FormUrlEncoded
    @POST("state")
    Call<String> getStates(@Field("service") int service);

    @FormUrlEncoded
    @POST("setting")
    Call<String> getSettings(@Field("service") int service);

    @FormUrlEncoded
    @POST("login")
    Call<String> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgotpassword")
    Call<BaseResponse> forgotpassword(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("getchilduser")
    Call<String> getChildUsers(@Field("service") Map<String, String> params);

    @FormUrlEncoded
    @POST("balance")
    Call<String> getBalance(@Field("service") int service);

    @FormUrlEncoded
    @POST("getchild")
    Call<String> getUser(@Field("key") String email);

    @FormUrlEncoded
    @POST("addbalancenew")
    Call<String> addBalance(@Field("userid") String userId, @Field("amount") String amount, @Field("lati") String lati, @Field("long") String lng);

    @FormUrlEncoded
    @POST("cashsummarynew")
    Call<String> getCashSummary(@Field("userid") String userId, @Field("user_type") int userType, @Field("from_date") String fromDate, @Field("to_date") String toDate);

    @FormUrlEncoded
    @POST("changepass")
    Call<String> changePassword(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("newpass")
    Call<String> forgotChangePassword(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("getparent")
    Call<String> getParentUsers(@Field("service") int service);
}
