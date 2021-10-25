package specificstep.com.GlobalClasses;

import android.util.Base64;

import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;
import javax.inject.Singleton;

import specificstep.com.data.source.local.Pref;

@Singleton
public class EncryptionUtil {

    private Pref pref;

    @Inject
    public EncryptionUtil(Pref pref) {
        this.pref = pref;
    }

    public String decrypt(String response) {
        String user_id = pref.getValue(Pref.KEY_USER_ID, "");
        MCrypt mCrypt = new MCrypt(user_id, FirebaseInstanceId.getInstance().getToken());
        String decrypted_response = null;
        byte[] decrypted_bytes = Base64.decode(response, Base64.DEFAULT);
        try {
            decrypted_response = new String(mCrypt.decrypt(MCrypt.bytesToHex(decrypted_bytes)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted_response;

    }

    public String encrypt(String text) {
        String user_id = pref.getValue(Pref.KEY_USER_ID, "");
        MCrypt mCrypt = new MCrypt(user_id, FirebaseInstanceId.getInstance().getToken());
        try {
            byte[] encrypted_bytes = mCrypt.encrypt(text);
            return Base64.encodeToString(encrypted_bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }
}
