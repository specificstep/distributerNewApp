package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

public class ProductEntity {

    @SerializedName("id")
    String id;
    @SerializedName("product_name")
    String name;
    @SerializedName("company_id")
    String companyId;
    @SerializedName("logo")
    String logo;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getLogo() {
        return logo;
    }
}
