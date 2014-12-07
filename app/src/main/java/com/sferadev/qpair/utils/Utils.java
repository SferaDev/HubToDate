package com.sferadev.qpair.utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.sferadev.qpair.R;
import com.sferadev.qpair.activity.AdminActivity;
import com.sferadev.qpair.receiver.AdminReceiver;

import java.util.List;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.QPairUtils.getQpairIntent;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;

// These aren't the droids you're looking for...
public class Utils {
    public static final String ACTION_CHANGE_IME = "com.sferadev.qpair.CHANGE_IME";
    public static final String ACTION_CHANGE_RINGER_MODE = "com.sferadev.qpair.CHANGE_RINGER_MODE";
    public static final String ACTION_CHANGE_WIFI = "com.sferadev.qpair.CHANGE_WIFI";
    public static final String ACTION_CREATE_DIALOG = "com.sferadev.qpair.CREATE_DIALOG";
    public static final String ACTION_MEDIA = "com.sferadev.qpair.ACTION_MEDIA";
    public static final String ACTION_OPEN_ACTIVITY = "com.sferadev.qpair.OPEN_ACTIVITY";
    public static final String ACTION_OPEN_PLAY_STORE = "com.sferadev.qpair.OPEN_PLAY_STORE";
    public static final String ACTION_OPEN_URL = "com.sferadev.qpair.OPEN_URL";
    public static final String ACTION_SCREEN_OFF = "com.sferadev.qpair.SCREEN_OFF";
    public static final String ACTION_SHOW_TOUCHES = "com.sferadev.qpair.SHOW_TOUCHES";
    public static final String ACTION_UNINSTALL_PACKAGE = "com.sferadev.qpair.UNINSTALL_PACKAGE";
    public static final String ACTION_UPDATE_BRIGHTNESS = "com.sferadev.qpair.UPDATE_BRIGHTNESS";
    public static final String ACTION_UPDATE_CLIPBOARD = "com.sferadev.qpair.UPDATE_CLIPBOARD";
    public static final String ACTION_VIBRATE = "com.sferadev.qpair.VIBRATE";

    public static final String EXTRA = "qpairExtra";

    public static final String KEY_ALWAYS_RINGER = "alwaysRinger";
    public static final String KEY_ALWAYS_WIFI = "alwaysWifi";
    public static final String KEY_IS_CONNECTED = "isConnected";
    public static final String KEY_IS_ON = "isOn";
    public static final String KEY_IS_PHONE = "isPhone";
    public static final String KEY_LAST_APP = "lastApp";
    public static final String KEY_LAST_RINGER_MODE = "lastRingerMode";

    // Options that appear in the AssistDialog
    public static String[] assistOptions = {
            getContext().getString(R.string.array_assist_sync_app),
            getContext().getString(R.string.array_assist_sync_clipboard),
            getContext().getString(R.string.array_assist_sync_brightness),
            getContext().getString(R.string.array_assist_screen_off),
            getContext().getString(R.string.array_assist_show_touches),
            getContext().getString(R.string.array_assist_media)
    };

    // Options that appear in the MediaDialog
    public static String[] mediaOptions = {
            getContext().getString(R.string.array_media_play),
            getContext().getString(R.string.array_media_pause),
            getContext().getString(R.string.array_media_stop),
            getContext().getString(R.string.array_media_next),
            getContext().getString(R.string.array_media_previous)
    };

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
                                    new sendBroadcastConnection(ACTION_OPEN_ACTIVITY, EXTRA,
                                            getForegroundApp()), 0);
                            break;
                        // Case: Sync Clipboard
                        case 1:
                            getContext().bindService(getQpairIntent(),
                                    new sendBroadcastConnection(ACTION_UPDATE_CLIPBOARD, EXTRA,
                                            getClipboardString()), 0);
                            break;
                        // Case: Sync Brightness
                        case 2:
                            getContext().bindService(getQpairIntent(),
                                    new sendBroadcastConnection(ACTION_UPDATE_BRIGHTNESS, EXTRA,
                                            getSystemPreference(android.provider.Settings.System.SCREEN_BRIGHTNESS)), 0);
                            break;
                        // Case: Turn Screen Off
                        case 3:
                            getContext().bindService(getQpairIntent(),
                                    new sendBroadcastConnection(ACTION_SCREEN_OFF, EXTRA,
                                            "screenOff"), 0);
                            break;
                        // Case: Show Touches
                        case 4:
                            getContext().bindService(getQpairIntent(),
                                    new sendBroadcastConnection(ACTION_SHOW_TOUCHES, EXTRA,
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

    // Dialog Creation with simple message and positive button
    public static void createDialog(String title, String message,
                                    DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(getContext(),
                android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getContext().getString(android.R.string.ok), listener)
                .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    // Dialog Creation with simple message, positive and negative button
    public static void createDialog(String title, String message,
                                    DialogInterface.OnClickListener positiveListener,
                                    DialogInterface.OnClickListener negativeListener) {
        AlertDialog dialog = new AlertDialog.Builder(getContext(),
                android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getContext().getString(android.R.string.yes), positiveListener)
                .setNegativeButton(getContext().getString(android.R.string.no), negativeListener)
                .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    // Dialog Creation with simple message, positive, negative and neutral button
    public static void createDialog(String title, String message,
                                    DialogInterface.OnClickListener positiveListener,
                                    DialogInterface.OnClickListener negativeListener,
                                    OnClickListener neutralListener) {
        AlertDialog dialog = new AlertDialog.Builder(getContext(),
                android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getContext().getString(android.R.string.yes), positiveListener)
                .setNegativeButton(getContext().getString(android.R.string.no), negativeListener)
                .setNeutralButton(getContext().getString(R.string.always), neutralListener)
                .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    // Dialog Creation with options as main content
    public static void createDialog(String title, String itemOptions[],
                                    DialogInterface.OnClickListener clickListener) {
        AlertDialog dialog = new AlertDialog.Builder(getContext(),
                android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
                .setTitle(title)
                .setItems(itemOptions, clickListener)
                .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    // Get Explicit from Implicit Intent, thanks Lollipop
    public static Intent createExplicitFromImplicitIntent(Intent implicitIntent) {
        List<ResolveInfo> resolveInfo = getContext().getPackageManager().queryIntentServices(implicitIntent, 0);
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
                                new sendBroadcastConnection(ACTION_MEDIA, EXTRA, "play"), 0);
                        break;
                    // Case: Pause
                    case 1:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_MEDIA, EXTRA, "pause"), 0);
                        break;
                    // Case: Stop
                    case 2:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_MEDIA, EXTRA, "stop"), 0);
                        break;
                    // Case: Next
                    case 3:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_MEDIA, EXTRA, "next"), 0);
                        break;
                    // Case: Previous
                    case 4:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_MEDIA, EXTRA, "previous"), 0);
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
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command" , command);
        getContext().sendBroadcast(i);
    }

    // Toast Creation
    public static void createToast(String string) {
        Toast toast = Toast.makeText(getContext(), string, Toast.LENGTH_LONG);
        toast.show();
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
    public static String getForegroundApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) getContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return appProcess.processName;
                }
            }
        } else {
            @SuppressWarnings("deprecation")
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

    // Get String Preference
    public static String getPreferences(String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getString(key, defaultValue);
    }

    // Get Boolean Preference
    public static boolean getPreferences(String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(key, defaultValue);
    }

    // Get Integer Preference
    public static int getPreferences(String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(key, defaultValue);
    }

    // Return System Preference
    public static int getSystemPreference(String preferenceName) {
        try {
            return android.provider.Settings.System.getInt(
                    getContext().getContentResolver(), preferenceName);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
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
    public static boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
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

    public static boolean isUserAGoat() throws InterruptedException {
        if (VERSION.RELEASE == "FirefoxOS") {
            // Wait 1h and 8m to finish talking about FirefoxOS
            Thread.sleep(68*60*1000);
            return true;
        } else {
            return false;
        }
    }

    // Open default activity upon packageName
    public static void openActivity(String packageName) {
        if (isPackageInstalled(packageName)) {
            Intent i = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            getContext().startActivity(i);
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
                    .getString(R.string.play_package), getContext().getString(R.string.play_intent_activity)));
            launchIntent.setData(Uri.parse(getContext().getString(R.string.play_market_scheme) + packageName));
            getContext().startActivity(launchIntent);
        } catch (android.content.ActivityNotFoundException e) {
            openURL(getContext().getString(R.string.play_url_scheme) + packageName);
            e.printStackTrace();
        }
    }

    // Open Website upon URL
    public static void openURL(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
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

    // Set String Preference
    public static void setPreferences(String key, String value) {
        SharedPreferences.Editor mEditor = PreferenceManager
                .getDefaultSharedPreferences(getContext()).edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }

    // Set Boolean Preference
    public static void setPreferences(String key, boolean value) {
        SharedPreferences.Editor mEditor = PreferenceManager
                .getDefaultSharedPreferences(getContext()).edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    // Set Integer Preference
    public static void setPreferences(String key, int value) {
        SharedPreferences.Editor mEditor = PreferenceManager
                .getDefaultSharedPreferences(getContext()).edit();
        mEditor.putInt(key, value);
        mEditor.apply();
    }

    // Set Volume Ringer Mode
    public static void setRingerMode(int value) {
        if (value != -1) {
            AudioManager audioManager = (AudioManager) getContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(value);
        }
        setPreferences(KEY_LAST_RINGER_MODE, value);
    }

    // Set System Preference
    public static void setSystemPreference(String preferenceName, int value) {
        android.provider.Settings.System.putInt(getContext().getContentResolver(),
                preferenceName, value);
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

    // Ask for a InputMethod change
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
    
    public static void toggleShowTouches() {
        if (getSystemPreference("show_touches") == 1) {
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