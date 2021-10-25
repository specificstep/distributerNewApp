package specificstep.com.Models;

import java.util.List;

public class ParentUser {
    private String firstName, lastName, phoneNumber, userType, firmName;
    private List<ParentUserModel> parentUsers;

    public List<ParentUserModel> getParentUsers() {
        return parentUsers;
    }

    public void setParentUsers(List<ParentUserModel> parentUsers) {
        this.parentUsers = parentUsers;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
