package specificstep.com.exceptions;

import android.content.Context;

import com.google.common.base.Strings;

import specificstep.com.R;
import specificstep.com.data.exceptions.InternalServerException;
import specificstep.com.data.exceptions.InvalidAccessTokenException;
import specificstep.com.data.exceptions.InvalidOTPException;
import specificstep.com.data.exceptions.InvalidUserNameException;
import specificstep.com.data.exceptions.NetworkConnectionException;
import specificstep.com.data.exceptions.SignInException;

public class ErrorMessageFactory {


    private ErrorMessageFactory() {
        //empty
    }


    public static String create(Context context, Exception exception) {
        String message;
        if (Strings.isNullOrEmpty(exception.getMessage())) {
            message = context.getString(R.string.exception_message_generic);
        } else {
            message = exception.getMessage();
        }

        if (exception instanceof NetworkConnectionException) {
            message = context.getString(R.string.message_no_intenet);
        } else if (exception instanceof InvalidUserNameException) {
            message = context.getString(R.string.exception_message_username_not_exist);
        } else if (exception instanceof InvalidOTPException) {
            message = context.getString(R.string.exception_message_invalid_otp);
        } else if (exception instanceof SignInException) {
            message = context.getString(R.string.exception_message_invalid_username_password);
        } else if (exception instanceof InvalidAccessTokenException) {
            message = context.getString(R.string.exception_message_invalid_access_token);
        }else if(exception instanceof InternalServerException) {
            message = context.getString(R.string.exception_message_internal_server_error);
        }

        return message;
    }
}
