package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class SignInResponse extends BaseResponse {

    @SerializedName("mobile")
    String mobile;
    @SerializedName("user_name")
    String userName;
    @SerializedName("user_type")
    int userType;
    @SerializedName("state_id")
    int stateId;
    @SerializedName("state_name")
    String stateName;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
