package specificstep.com.data.entity;

import java.util.List;

public class AutoOtpResponse {

    int status;
    String msg;
    List<AutoOtpEntity> data;

    public AutoOtpResponse() {
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public List<AutoOtpEntity> getData() {
        return data;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(List<AutoOtpEntity> data) {
        this.data = data;
    }
}
