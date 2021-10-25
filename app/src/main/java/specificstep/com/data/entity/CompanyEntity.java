package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class CompanyEntity {
    @SerializedName("id")
    String id;
    @SerializedName("company_name")
    String name;
    @SerializedName("logo")
    String logo;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }
}
