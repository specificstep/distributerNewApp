package specificstep.com.data.entity;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class BalanceResponse extends BaseResponse {
    @SerializedName("balance")
    String balance;

    public BigDecimal getBalance() {
        try {
            return new BigDecimal(balance);
        } catch (Exception e) {
            e.printStackTrace();
            return new BigDecimal(0);
        }
    }
}
