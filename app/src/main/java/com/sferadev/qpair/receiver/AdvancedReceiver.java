package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sferadev.qpair.R;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.Constants.EXTRA;
import static com.sferadev.qpair.utils.Constants.KEY_LAST_APP;
import static com.sferadev.qpair.utils.PreferenceUtils.setPreference;
import static com.sferadev.qpair.utils.PreferenceUtils.setSystemPreference;
import static com.sferadev.qpair.utils.UIUtils.createDialog;
import static com.sferadev.qpair.utils.UIUtils.createToast;
import static com.sferadev.qpair.utils.Utils.createMusicIntent;
import static com.sferadev.qpair.utils.Utils.doVibrate;
import static com.sferadev.qpair.utils.Utils.isPackageInstalled;
import static com.sferadev.qpair.utils.Utils.openActivity;
import static com.sferadev.qpair.utils.Utils.openPlayStore;
import static com.sferadev.qpair.utils.Utils.openURL;
import static com.sferadev.qpair.utils.Utils.setClipboardString;
import static com.sferadev.qpair.utils.Utils.setRingerMode;
import static com.sferadev.qpair.utils.Utils.switchIME;
import static com.sferadev.qpair.utils.Utils.switchWifi;
import static com.sferadev.qpair.utils.Utils.toggleShowTouches;
import static com.sferadev.qpair.utils.Utils.turnScreenOff;
import static com.sferadev.qpair.utils.Utils.uninstallPackage;

// Handle custom Broadcasts between QPair devices
public class AdvancedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            // Request to perform a Media Action
            case "com.sferadev.qpair.ACTION_MEDIA":
                createMusicIntent(intent.getStringExtra(EXTRA));
                break;
            // Request to update InputMethod
            case "com.sferadev.qpair.CHANGE_IME":
                switchIME();
                break;
            // Request to update WiFi status
            case "com.sferadev.qpair.CHANGE_WIFI":
                switchWifi(Boolean.parseBoolean(intent.getStringExtra(EXTRA)));
                break;
            // Request to update Volume status
            case "com.sferadev.qpair.CHANGE_RINGER_MODE":
                setRingerMode(Integer.valueOf(intent.getStringExtra(EXTRA)));
                break;
            // Request to create a Dialog with a message
            case "com.sferadev.qpair.CREATE_DIALOG":
                createDialog("HubToDate", intent.getStringExtra(EXTRA), null);
                break;
            // Request to open certain activity via packageName
            case "com.sferadev.qpair.OPEN_ACTIVITY":
                openActivity(intent.getStringExtra(EXTRA));
                break;
            // Request to open play store on certain listing via packageName
            case "com.sferadev.qpair.OPEN_PLAY_STORE":
                setPreference(KEY_LAST_APP, intent.getStringExtra(EXTRA));
                if (!isPackageInstalled(intent.getStringExtra(EXTRA))) {
                    openPlayStore(intent.getStringExtra(EXTRA));
                } else {
                    openActivity(intent.getStringExtra(EXTRA));
                }
                break;
            // Request to open a certain URL
            case "com.sferadev.qpair.OPEN_URL":
                openURL(intent.getStringExtra(EXTRA));
                break;
            // Request to power off screen
            case "com.sferadev.qpair.SCREEN_OFF":
                turnScreenOff();
                break;
            case "com.sferadev.qpair.SHOW_TOUCHES":
                if (intent.getStringExtra(EXTRA).equals("showTouches")) {
                    toggleShowTouches();
                } else {
                    toggleShowTouches(intent.getStringExtra(EXTRA));
                }
                break;
            // Request to update brightness with certain int value
            case "com.sferadev.qpair.UPDATE_BRIGHTNESS":
                setSystemPreference(android.provider.Settings.System.SCREEN_BRIGHTNESS,
                        Integer.parseInt(intent.getStringExtra(EXTRA)));
                break;
            // Request to update Clipboard String
            case "com.sferadev.qpair.UPDATE_CLIPBOARD":
                setClipboardString(intent.getStringExtra(EXTRA));
                doVibrate(150);
                createToast(getContext().getString(R.string.toast_clipboard) + " " + intent.getStringExtra(EXTRA));
                break;
            // Request to uninstall certain package via packageName
            case "com.sferadev.qpair.UNINSTALL_PACKAGE":
                setPreference(KEY_LAST_APP, intent.getStringExtra(EXTRA));
                if (isPackageInstalled(intent.getStringExtra(EXTRA))) {
                    uninstallPackage(intent.getStringExtra(EXTRA));
                } else {
                    createToast(getContext().getString(R.string.toast_uninstall_failed) + " " + intent.getStringExtra(EXTRA));
                }
                break;
            case "com.sferadev.qpair.VIBRATE":
                doVibrate(250);
                break;
            // Default case, this should never happen.
            default:
                createToast(intent.getAction());
        }
    }
}
