package chevalier.vladimir.gmail.com.helpmaster.entities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by chevalier on 06.08.17.
 */

public class FlagAccess {
//    public static boolean networkAccess = false;

    public static boolean checkNetWorkAccess(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        } else {
            return activeNetwork.isConnectedOrConnecting();
        }
//        if(null == activeNetwork.isConnectedOrConnecting())
    }
    public static String NAME_USERAPP;
    public static String MAIL_CURRENT_USERAPP;
}
