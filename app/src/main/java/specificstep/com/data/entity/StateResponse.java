package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StateResponse {

    @SerializedName("state")
    List<StateEntity> stateList;

    public List<StateEntity> getStateList() {
        return stateList;
    }
}
