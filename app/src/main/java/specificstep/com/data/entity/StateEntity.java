package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class StateEntity {

    @SerializedName("circle_id")
    String circleId;
    @SerializedName("circle_name")
    String circleName;

    public String getCircleId() {
        return circleId;
    }

    public String getCircleName() {
        return circleName;
    }
}
