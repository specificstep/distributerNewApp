package specificstep.com.utility;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by ubuntu on 9/3/17.
 */

public class Utility {
    private static int time;
    // public static final boolean isDeveloperPreview = true;
    public static String getString(EditText edt) {
        return edt.getText().toString().trim();
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String formatBigDecimalToString(BigDecimal bigDecimal) {
        if(bigDecimal == null) return "0";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("0.##");
        format.setDecimalFormatSymbols(symbols);
        return format.format(bigDecimal.floatValue());
    }
}
