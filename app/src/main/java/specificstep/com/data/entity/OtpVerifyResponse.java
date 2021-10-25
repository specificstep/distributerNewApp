package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class OtpVerifyResponse extends BaseResponse {

    @SerializedName("user_id")
    String userId;
    @SerializedName("state_id")
    String stateId;
    @SerializedName("state_name")
    String stateName;
    @SerializedName("name")
    String name;

    public String getUserId() {
        return userId;
    }

    public String getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public String getName() {
        return name;
    }
}
