package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class ParentUserEntity {

    @SerializedName("phone_no")
    String phoneNo;
    @SerializedName("firm_name")
    String firmName;
    @SerializedName("usertype")
    String userType;
    @SerializedName("first_name")
    String firstName;
    @SerializedName("last_name")
    String lastName;

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
