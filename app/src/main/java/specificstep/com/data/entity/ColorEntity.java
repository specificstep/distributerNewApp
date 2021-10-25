package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class ColorEntity {

    @SerializedName("name")
    String name;
    @SerializedName("value")
    String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
