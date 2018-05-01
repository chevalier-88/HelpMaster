package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import chevalier.vladimir.gmail.com.helpmaster.ui.HomeActivity;

public class HelpMasterReceiver extends BroadcastReceiver {
    public HelpMasterReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, HelpMasterServiceNotification.class);
        context.startService(intent1);
    }
}
