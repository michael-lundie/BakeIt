package io.lundie.michael.bakeit.utilities;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.inject.Inject;

public class AppUtils {

    Application context;
    @Inject
    public AppUtils(Application application) {
        this.context = application;
    }

    /**
     * Checks to make sure the smart phone has access to the internet.
     * @return boolean
     */
    public boolean checkNetworkAccess() {
        ConnectivityManager connMgr =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Check the connectivity manager is not null first to avoid NPE.
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            // Returns true or false depending on connectivity status.
            return networkInfo != null && networkInfo.isConnected();
        }
        //Connectivity manager is null so returning false.
        return false;
    }

    public static String replaceNumberedDescription(String descriptionString) {
        if(!descriptionString.isEmpty()) {

            String substring = descriptionString.substring(0, 5);
            int removeCharacters = 0;

            for (int i = 0; i < substring.length(); i++) {
                String c = Character.toString(substring.charAt(i));
                if (c.matches("[0-9\\s.]")) {
                    removeCharacters++;
                }
            }return descriptionString.substring(removeCharacters, descriptionString.length());
        } return null;
    }
}