package specificstep.com.Models;

/**
 * Created by ubuntu on 3/5/17.
 */

public class ChildUserModel {
    public String id;
    public String userName;
    public String phoneNo;
    public String firmName;
    public String userType;
    public String ParentUserId;
    // public String balance;
    public float balance;
    public String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParentUserId() {
        return ParentUserId;
    }

    public void setParentUserId(String parentUserId) {
        ParentUserId = parentUserId;
    }
}
