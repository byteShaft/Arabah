package byteshaft.com.arabah.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by husnain on 1/27/17.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppGlobals.isSwitchOn()) {
            context.startService(new Intent(context, LocationService.class));
        }
    }
}
