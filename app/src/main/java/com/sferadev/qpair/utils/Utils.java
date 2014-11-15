package com.sferadev.qpair.utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.sferadev.qpair.App;

import java.util.List;

import static com.sferadev.qpair.App.getContext;

public class Utils {

    public static String ACTION_OPEN_ACTIVITY = "com.sferadev.qpair.OPEN_ACTIVITY";
    public static String ACTION_OPEN_PLAY_STORE = "com.sferadev.qpair.OPEN_PLAY_STORE";
    public static String ACTION_OPEN_URL = "com.sferadev.qpair.OPEN_URL";
    public static String ACTION_CHANGE_IME = "com.sferadev.qpair.CHANGE_IME";
    public static String ACTION_CHANGE_WIFI = "com.sferadev.qpair.CHANGE_WIFI";
    public static String ACTION_CHANGE_RINGER_MODE = "com.sferadev.qpair.CHANGE_RINGER_MODE";

    public static String EXTRA_URL = "url";
    public static String EXTRA_PACKAGE_NAME = "packageName";
    public static String EXTRA_WIFI_STATE = "wifiState";
    public static String EXTRA_RINGER_MODE = "ringerMode";

    public static String KEY_IS_PHONE = "isPhone";
    public static String KEY_IS_ON = "isOn";
    public static String KEY_IS_CONNECTED = "isConnected";
    public static String KEY_LAST_APP = "lastApp";
    public static String KEY_LAST_RINGER_MODE = "lastRingerMode";

    public static boolean isAdvanced = true; //TODO

    public static void createToast(String string) {
        Toast toast = Toast.makeText(getContext(), string, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void createDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(App.getContext())
                .setTitle(title)
                .setMessage(message)
                .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public static void setPreferences(String key, String value) {
        SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public static void setPreferences(String key, boolean value) {
        SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit();
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public static void setPreferences(String key, int value) {
        SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit();
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public static String getPreferences(String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getString(key, defaultValue);
    }

    public static boolean getPreferences(String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(key, defaultValue);
    }

    public static int getPreferences(String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(key, defaultValue);
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    public static String getForegroundApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //TODO: You're screwed!
        } else {
            ActivityManager am = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            return foregroundTaskInfo.topActivity.getPackageName();
        }
        return null;
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

    public static void switchIME() {
        InputMethodManager imeManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            imeManager.showInputMethodPicker();
        }
    }

    public static void switchWifi(boolean state) {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(state);
    }

    public static void setRingerMode(int value) {
        if (value != -1) {
            AudioManager audioManager = (AudioManager) App.getContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(value);
        }
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