package specificstep.com.GlobalClasses;

/**
 * Created by ubuntu on 12/1/17.
 */

public class URL {

    public static String base_url = "http://fast.recharge.website/webservices/"; //zulan recharge url

 //public static String register = base_url + "register";
    public static String company = base_url + "company";
    public static String product = base_url + "product";
    public static String state = base_url + "state";
    public static String login = base_url + "login";
    public static String forgot_password = base_url + "forgotpassword";
    public static String forgot_password_otp = base_url + "forgototp";
    public static String balance = base_url + "balance";
    public static String setting = base_url + "setting";

    public static String child_user = base_url + "getchild";
    public static String add_balance = base_url + "addbalancenew";
    public static String cash_users = base_url + "getchilduser";
    public static String cash_summary = base_url + "cashsummarynew";

    //for purchase user
    public static String purchase_user_schemetype = base_url + "purchasetype";
    public static String purchase_user_schemename = base_url + "purchaseidscheme";
    public static String purchase_user_submit = base_url + "purchaseid";

    //for Add User
    public static String add_user_type = base_url + "usertype";
    public static String add_user_scheme = base_url + "userscheme";
    public static String add_user_register = base_url + "userregister";

    public static String changePassword = base_url + "changepass";

    // 2017_05_02 - get parent user details URL
    public static String GET_PARENT_USER_DETAILS = base_url + "getparent";

    // 2017_05_09 - reverse payment
    public static String GET_REVERSE_PAYMENT = base_url + "reverse";

    public static String accountLedger = base_url + "accounts";

   public static String roleType = base_url + "cashsummarynew/role";

   // 2018_12_25 - credit debit note
   public static String walletType = base_url + "wallets";
   public static String crdrNote = base_url + "crdrnote";

   // 2019_2_20 - DMT Transaction List
   public static String transactionList = base_url + "dmt/transactionlists";

    //2020_6_23 - Payment Request
    public static String addcompanybank = base_url + "companybank";
    public static String addbank = base_url + "bank";
    public static String addpaymentrequest = base_url + "paymentrequest";

    //2020_6_23 - Payment Request List
    public static String paymentRequestList = base_url + "paymentreqlist";

}
