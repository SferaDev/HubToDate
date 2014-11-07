package com.sferadev.qpair.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.widget.Toast;

import static com.sferadev.qpair.App.getContext;

public class Utils {

    public static String ACTION_OPEN_ACTIVITY = "com.sferadev.qpair.OPEN_ACTIVITY";
    public static String ACTION_OPEN_PLAY_STORE = "com.sferadev.qpair.OPEN_PLAY_STORE";
    public static String ACTION_OPEN_URL = "com.sferadev.qpair.OPEN_URL";

    public static String EXTRA_URL = "url";
    public static String EXTRA_PACKAGE_NAME = "packageName";

    public static boolean IS_APP_ADVANCED = true; //TODO

    public static void createToast(String string) {
        Toast toast = Toast.makeText(getContext(), string, Toast.LENGTH_LONG);
        toast.show();
    }

    public static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void openActivity(String packageName) {
        Intent i = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        getContext().startActivity(i);
    }

    public static void openPlayStore(String packageName) {
        try {
            Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage("com.android.vending");
            launchIntent.setComponent(new ComponentName("com.android.vending", "com.google.android.finsky.activities.LaunchUrlHandlerActivity"));
            launchIntent.setData(Uri.parse("market://details?id=" + packageName));
            getContext().startActivity(launchIntent);
        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
            openURL("http://play.google.com/store/apps/details?id=" + packageName);
        }
    }

    public static void openURL(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
    }

    public static String getBatteryLevel() {
        Intent batteryIntent = getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) : 0;
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int percent = (level * 100) / scale;
        return String.valueOf(percent) + "%";
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}