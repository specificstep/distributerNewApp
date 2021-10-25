package specificstep.com.data.utils;

public enum UserType {
    DISTRIBUTOR(2),DEALER(7), RETAILER(4), RESELLER(3), SELF(0);
    int type;

    UserType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


}
