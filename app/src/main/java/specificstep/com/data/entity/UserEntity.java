package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class UserEntity {

    @SerializedName("id")
    String id;
    @SerializedName("username")
    String userName;
    @SerializedName("phone_no")
    String phoneNo;
    @SerializedName("email")
    String email;
    @SerializedName("firm_name")
    String firmName;
    @SerializedName("usertype")
    String userType;
    @SerializedName("balance")
    String balance;
    @SerializedName("ParentUserId")
    String ParentUserId;

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public String getFirmName() {
        return firmName;
    }

    public String getUserType() {
        return userType;
    }

    public String getBalance() {
        return balance;
    }

    public String getParentUserId() {
        return ParentUserId;
    }
}
