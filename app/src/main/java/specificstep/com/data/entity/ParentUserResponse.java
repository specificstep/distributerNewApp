package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParentUserResponse {

    @SerializedName("userdata")
    List<ParentUserEntity> userEntities;
    @SerializedName("details")
    ParentUserEntity parentUserEntity;

    public ParentUserEntity getParentUserEntity() {
        return parentUserEntity;
    }

    public void setParentUserEntity(ParentUserEntity parentUserEntity) {
        this.parentUserEntity = parentUserEntity;
    }

    public List<ParentUserEntity> getUserEntities() {
        return userEntities;
    }
}
