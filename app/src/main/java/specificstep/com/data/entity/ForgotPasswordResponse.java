package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse {

    @SerializedName("status")
    int status = -1;
    @SerializedName("msg")
    String message;
    @SerializedName("password")
    int password;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getPassword() {
        return password;
    }

}
