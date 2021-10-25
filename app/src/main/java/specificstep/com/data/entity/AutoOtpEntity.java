package specificstep.com.data.entity;

public class AutoOtpEntity {

    String skip_otp;
    String default_otp;

    public String getSkip_otp() {
        return skip_otp;
    }

    public String getDefault_otp() {
        return default_otp;
    }

    public AutoOtpEntity() {
    }

    public void setSkip_otp(String skip_otp) {
        this.skip_otp = skip_otp;
    }

    public void setDefault_otp(String default_otp) {
        this.default_otp = default_otp;
    }
}
