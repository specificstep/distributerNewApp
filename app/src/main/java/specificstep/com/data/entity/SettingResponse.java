package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SettingResponse {

    @SerializedName("color")
    List<ColorEntity> colorList;

    public List<ColorEntity> getColors() {
        return colorList;
    }
}
