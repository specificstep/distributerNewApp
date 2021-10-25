package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class EncryptedResponse extends BaseResponse {

    @SerializedName("data")
    String data;

    @SerializedName("data2")
    String data2;

    public String getData2() {
        return data2;
    }

    public String getData() {
        return data;
    }

}
