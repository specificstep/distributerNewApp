package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductResponse {

    @SerializedName("product")
    List<ProductEntity> productList;

    public List<ProductEntity> getProductList() {
        return productList;
    }
}
