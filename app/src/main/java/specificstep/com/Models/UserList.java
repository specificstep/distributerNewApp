package specificstep.com.Models;

/**
 * Created by ubuntu on 13/1/17.
 */

public class UserList {
    String user_id;
    String user_name;
    String email;
    String phone_no;
    String usertype;
    String ParentUserId;
    // String firmName;

//    public String getFirmName() {
//        return firmName;
//    }

//    public void setFirmName(String firmName) {
//        this.firmName = firmName;
//    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    @Override
    public String toString() {
        return getUser_name() + " - " + getPhone_no();
    }

    public String getParentUserId() {
        return ParentUserId;
    }

    public void setParentUserId(String parentUserId) {
        ParentUserId = parentUserId;
    }
}
