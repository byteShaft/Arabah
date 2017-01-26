package byteshaft.com.arabah.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;

/**
 * Created by husnain on 1/24/17.
 */

public class Helpers {

    public static void AlertDialogWithPositiveNegativeFunctions(
            Context context, String title, String message, String positiveButtonText,
            String negativeButtonText, final Runnable listenerYes, final Runnable listenerNo) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listenerYes.run();
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerNo.run();
                    }
                })
                .show();
    }

    public static boolean locationEnabled() {
        LocationManager lm = (LocationManager) AppGlobals.getContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }
}
