package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChildUserResponse {

    @SerializedName("userdata")
    List<UserEntity> userEntities;

    public List<UserEntity> getUserEntities() {
        return userEntities;
    }
}
