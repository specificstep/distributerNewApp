package specificstep.com.data.mapper;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import specificstep.com.data.entity.AutoOtpEntity;
import specificstep.com.data.entity.AutoOtpResponse;
import specificstep.com.data.entity.BalanceResponse;
import specificstep.com.data.entity.CashSummaryEntity;
import specificstep.com.data.entity.CashSummaryResponse;
import specificstep.com.data.entity.ChildUserResponse;
import specificstep.com.data.entity.CompanyEntity;
import specificstep.com.data.entity.CompanyResponse;
import specificstep.com.data.entity.EncryptedResponse;
import specificstep.com.data.entity.ForgotPasswordResponse;
import specificstep.com.data.entity.OtpVerifyResponse;
import specificstep.com.data.entity.ParentUserResponse;
import specificstep.com.data.entity.ProductEntity;
import specificstep.com.data.entity.ProductResponse;
import specificstep.com.data.entity.SettingEntity;
import specificstep.com.data.entity.SignInResponse;
import specificstep.com.data.entity.StateEntity;
import specificstep.com.data.entity.StateResponse;


@Singleton
public class UserJsonDataMapper {

    private Gson gson;

    @Inject
    public UserJsonDataMapper() {
        gson = new Gson();
    }

    public EncryptedResponse parseBaseResponse(String response) {
        return gson.fromJson(response, EncryptedResponse.class);
    }

    public AutoOtpResponse parseAutoOtpFullResponse(String response) {
        return gson.fromJson(response, AutoOtpResponse.class);
    }

    public List<AutoOtpEntity> parseAutoOtpResponse(String response) {
        return gson.fromJson(response, AutoOtpResponse.class).getData();
    }

    public ForgotPasswordResponse parseBaseForgotResponse(String response) {
        return gson.fromJson(response, ForgotPasswordResponse.class);
    }

    public OtpVerifyResponse parseOtpVerificationResponse(String response) {
        return gson.fromJson(response, OtpVerifyResponse.class);
    }

    public List<CompanyEntity> parseCompanyResponse(String response) {
        CompanyResponse companyResponse = gson.fromJson(response, CompanyResponse.class);
        return companyResponse.getCompanyList();
    }

    public EncryptedResponse transformEncryptedResponse(String response) {
        return gson.fromJson(response, EncryptedResponse.class);
    }

    public List<ProductEntity> parseProductResponse(String response) {
        ProductResponse productResponse = gson.fromJson(response, ProductResponse.class);
        return productResponse.getProductList();
    }

    public List<StateEntity> parseStateResponse(String response) {
        StateResponse stateResponse = gson.fromJson(response, StateResponse.class);
        return stateResponse.getStateList();
    }

    public SettingEntity parseSettingResponse(String response) {
        return gson.fromJson(response, SettingEntity.class);
    }

    public ChildUserResponse parseChildUserResponse(String response) {
        return gson.fromJson(response, ChildUserResponse.class);
    }

    public BalanceResponse parseBalanceResponse(String response) {
        return gson.fromJson(response, BalanceResponse.class);
    }

    public List<CashSummaryEntity> parseCashSummaryResponse(String response) {
        return gson.fromJson(response, CashSummaryResponse.class).getData();
    }

    public ParentUserResponse parseParentUserResponse(String response) {
        return gson.fromJson(response, ParentUserResponse.class);
    }

    public String fetchPaymentId(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getString("data");
    }

    public SignInResponse transformSignInResponse(String response) {
        return gson.fromJson(response, SignInResponse.class);
    }
}
