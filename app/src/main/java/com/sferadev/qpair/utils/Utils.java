package com.sferadev.qpair.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.view.inputmethod.InputMethodManager;

import com.sferadev.qpair.R;
import com.sferadev.qpair.activity.AdminActivity;
import com.sferadev.qpair.receiver.AdminReceiver;

import java.util.List;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.Constants.ACTION_MEDIA;
import static com.sferadev.qpair.utils.Constants.ACTION_OPEN_ACTIVITY;
import static com.sferadev.qpair.utils.Constants.ACTION_SCREEN_OFF;
import static com.sferadev.qpair.utils.Constants.ACTION_SHOW_TOUCHES;
import static com.sferadev.qpair.utils.Constants.ACTION_UPDATE_BRIGHTNESS;
import static com.sferadev.qpair.utils.Constants.ACTION_UPDATE_CLIPBOARD;
import static com.sferadev.qpair.utils.Constants.KEY_LAST_RINGER_MODE;
import static com.sferadev.qpair.utils.Constants.assistOptions;
import static com.sferadev.qpair.utils.Constants.mediaOptions;
import static com.sferadev.qpair.utils.PreferenceUtils.getSystemPreference;
import static com.sferadev.qpair.utils.PreferenceUtils.setPreference;
import static com.sferadev.qpair.utils.PreferenceUtils.setSystemPreference;
import static com.sferadev.qpair.utils.QPairUtils.getQpairIntent;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;
import static com.sferadev.qpair.utils.UIUtils.createDialog;
import static com.sferadev.qpair.utils.UIUtils.createToast;


public class Utils {
    // Creation of the AssistDialog
    public static void createAssistDialog() {
        if (isQPairOn() && isConnected()) {
            createDialog(getContext().getString(R.string.app_name), assistOptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        // Case: Sync Current App
                        case 0:
                            getContext().bindService(getQpairIntent(),
                                    new sendBroadcastConnection(ACTION_OPEN_ACTIVITY,
                                            getForegroundApp()), 0);
                            break;
                        // Case: Sync Clipboard
                        case 1:
                            getContext().bindService(getQpairIntent(),
                                    new sendBroadcastConnection(ACTION_UPDATE_CLIPBOARD,
                                            getClipboardString()), 0);
                            break;
                        // Case: Sync Brightness
                        case 2:
                            getContext().bindService(getQpairIntent(),
                                    new sendBroadcastConnection(ACTION_UPDATE_BRIGHTNESS,
                                            getSystemPreference(android.provider.Settings.System.SCREEN_BRIGHTNESS)), 0);
                            break;
                        // Case: Turn Screen Off
                        case 3:
                            getContext().bindService(getQpairIntent(),
                                    new sendBroadcastConnection(ACTION_SCREEN_OFF,
                                            "screenOff"), 0);
                            break;
                        // Case: Show Touches
                        case 4:
                            getContext().bindService(getQpairIntent(),
                                    new sendBroadcastConnection(ACTION_SHOW_TOUCHES,
                                            "showTouches"), 0);
                            break;
                        // Case: Media
                        case 5:
                            createMediaDialog();
                            break;
                        // Default Case, should never happen
                        default:
                            createToast(getContext().getString(R.string.toast_option) + " " + which);
                            break;
                    }
                }
            });
        } else {
            openActivity(getContext().getString(R.string.qpair_package));
            createToast(getContext().getString(R.string.toast_not_connected));
        }
    }

    // Get Explicit from Implicit Intent, thanks Lollipop
    public static Intent createExplicitFromImplicitIntent(Intent implicitIntent) {
        List<ResolveInfo> resolveInfo = getContext().getPackageManager()
                .queryIntentServices(implicitIntent, 0);
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        return new Intent(implicitIntent).setComponent(component);
    }

    // Create a Media Dialog
    public static void createMediaDialog() {
        createDialog(getContext().getString(R.string.app_name), mediaOptions, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // Case: Play
                    case 0:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_MEDIA, "play"), 0);
                        break;
                    // Case: Pause
                    case 1:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_MEDIA, "pause"), 0);
                        break;
                    // Case: Stop
                    case 2:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_MEDIA, "stop"), 0);
                        break;
                    // Case: Next
                    case 3:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_MEDIA, "next"), 0);
                        break;
                    // Case: Previous
                    case 4:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_MEDIA, "previous"), 0);
                        break;
                    // Default Case, should never happen
                    default:
                        break;
                }
            }
        });
    }

    // Handle Music Intents
    public static void createMusicIntent(String command) {
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", command);
        getContext().sendBroadcast(intent);
    }

    //Do vibration for x ms
    public static void doVibrate(int time) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(time);
    }

    // Return Clipboard value
    public static String getClipboardString() {
        try {
            ClipboardManager clipboard = (ClipboardManager) getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            return clipboard.getPrimaryClip().getItemAt(0).getText().toString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get current running App, thanks again Lollipop
    @SuppressWarnings("deprecation")
    public static String getForegroundApp() {
        if (isBuildHigherThanVersion(Build.VERSION_CODES.LOLLIPOP)) {
            ActivityManager activityManager = (ActivityManager) getContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return appProcess.processName;
                }
            }
        } else {
            ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            return foregroundTaskInfo.topActivity.getPackageName();
        }
        return null;
    }

    // Return Owner's Full Name
    public static String getOwnerFullName() {
        Cursor query = getContext().getContentResolver()
                .query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        query.moveToFirst();
        String value = query.getString(query.getColumnIndex("display_name"));
        query.close();
        return value;
    }

    // Return Owner's Simple Name
    public static String getOwnerName() {
        Cursor query = getContext().getContentResolver()
                .query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        query.moveToFirst();
        String value[] = query.getString(query.getColumnIndex("display_name")).split(" ");
        query.close();
        return value[0];
    }

    // Check Whether Build Version is higher than x
    public static boolean isBuildHigherThanVersion(int version) {
        if (Build.VERSION.SDK_INT >= version) {
            return true;
        } else {
            return false;
        }
    }

    // Return whether if an App is installed
    public static boolean isPackageInstalled(String packageName) {
        try {
            getContext().getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    // Check whether the device screen is on or not
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        if (isBuildHigherThanVersion(Build.VERSION_CODES.KITKAT_WATCH)) {
            return powerManager.isInteractive();
        } else {
            return powerManager.isScreenOn();
        }
    }

    // Return whether if a Service is running
    public static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // These aren't the droids you're looking for...
    // Internal Joke from XDA:DevCon '14
    public static boolean isUserAGoat() throws InterruptedException {
        if (VERSION.RELEASE == "FirefoxOS") {
            // Wait 1h and 8m to finish talking about FirefoxOS
            Thread.sleep(68 * 60 * 1000);
            return true;
        } else {
            return false;
        }
    }

    // Open default activity upon packageName
    public static void openActivity(String packageName) {
        if (isPackageInstalled(packageName)) {
            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            getContext().startActivity(intent);
        } else {
            createToast(getContext().getString(R.string.toast_app_not_found));
        }
    }

    // Open Play Store upon packageName
    public static void openPlayStore(String packageName) {
        try {
            Intent launchIntent = getContext().getPackageManager()
                    .getLaunchIntentForPackage(getContext().getString(R.string.play_package));
            launchIntent.setComponent(new ComponentName(getContext()
                    .getString(R.string.play_package),
                    getContext().getString(R.string.play_intent_activity)));
            launchIntent.setData(Uri.parse(getContext().getString(R.string.play_market_scheme) + packageName));
            getContext().startActivity(launchIntent);
        } catch (android.content.ActivityNotFoundException e) {
            openURL(getContext().getString(R.string.play_url_scheme) + packageName);
            e.printStackTrace();
        }
    }

    // Open Website upon URL
    public static void openURL(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    // Set Clipboard value
    public static void setClipboardString(String value) {
        try {
            ClipboardManager clipboard = (ClipboardManager)
                    getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("simple text", value));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // Set Volume Ringer Mode
    public static void setRingerMode(int value) {
        if (value != -1) {
            AudioManager audioManager = (AudioManager) getContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(value);
        }
        setPreference(KEY_LAST_RINGER_MODE, value);
    }

    // Simulate Hardware Key
    public static void simulateKey(final int KeyCode) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Instrumentation instrumentation = new Instrumentation();
                    instrumentation.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    // Ask for an InputMethod change
    public static void switchIME() {
        InputMethodManager imeManager = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            imeManager.showInputMethodPicker();
        }
    }

    // Toggle WiFi state based on a Boolean value
    public static void switchWifi(boolean state) {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(state);
    }

    // Toggle Touches appearance
    public static void toggleShowTouches() {
        if (getSystemPreference("show_touches") == 1) {
            setSystemPreference("show_touches", 0);
        } else {
            setSystemPreference("show_touches", 1);
        }
    }

    // Toggle Touches appearance
    public static void toggleShowTouches(String value) {
        if (value.equals("false")) {
            setSystemPreference("show_touches", 0);
        } else {
            setSystemPreference("show_touches", 1);
        }
    }

    // Ask Device to turn its screen off
    public static void turnScreenOff() {
        DevicePolicyManager policyManager = (DevicePolicyManager) getContext()
                .getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName adminReceiver = new ComponentName(getContext(),
                AdminReceiver.class);

        if (policyManager.isAdminActive(adminReceiver)) {
            policyManager.lockNow();
        } else {
            getContext().startActivity(
                    new Intent(getContext(), AdminActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            createToast(getContext().getString(R.string.admin_failure));
        }
    }

    // Ask Device to uninstall App upon packageName
    public static void uninstallPackage(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package",
                packageName, null)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}