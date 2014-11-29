package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sferadev.qpair.R;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.Utils.EXTRA;
import static com.sferadev.qpair.utils.Utils.KEY_LAST_APP;
import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.isPackageInstalled;
import static com.sferadev.qpair.utils.Utils.openActivity;
import static com.sferadev.qpair.utils.Utils.openPlayStore;
import static com.sferadev.qpair.utils.Utils.openURL;
import static com.sferadev.qpair.utils.Utils.setBrightnessLevel;
import static com.sferadev.qpair.utils.Utils.setClipboardString;
import static com.sferadev.qpair.utils.Utils.setPreferences;
import static com.sferadev.qpair.utils.Utils.setRingerMode;
import static com.sferadev.qpair.utils.Utils.switchIME;
import static com.sferadev.qpair.utils.Utils.switchWifi;
import static com.sferadev.qpair.utils.Utils.turnScreenOff;
import static com.sferadev.qpair.utils.Utils.uninstallPackage;

public class AdvancedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "com.sferadev.qpair.CHANGE_IME":
                switchIME();
                break;
            case "com.sferadev.qpair.CHANGE_WIFI":
                switchWifi(Boolean.parseBoolean(intent.getStringExtra(EXTRA)));
                break;
            case "com.sferadev.qpair.CHANGE_RINGER_MODE":
                setRingerMode(Integer.valueOf(intent.getStringExtra(EXTRA)));
                break;
            case "com.sferadev.qpair.CREATE_DIALOG":
                createDialog("HubToDate", intent.getStringExtra(EXTRA), null);
                break;
            case "com.sferadev.qpair.OPEN_ACTIVITY":
                openActivity(intent.getStringExtra(EXTRA));
                break;
            case "com.sferadev.qpair.OPEN_PLAY_STORE":
                setPreferences(KEY_LAST_APP, intent.getStringExtra(EXTRA));
                if (!isPackageInstalled(intent.getStringExtra(EXTRA))) {
                    openPlayStore(intent.getStringExtra(EXTRA));
                } else {
                    openActivity(intent.getStringExtra(EXTRA));
                }
                break;
            case "com.sferadev.qpair.OPEN_URL":
                openURL(intent.getStringExtra(EXTRA));
                break;
            case "com.sferadev.qpair.SCREEN_OFF":
                turnScreenOff();
                break;
            case "com.sferadev.qpair.UPDATE_BRIGHTNESS":
                setBrightnessLevel(Integer.parseInt(intent.getStringExtra(EXTRA)));
                createToast(getContext().getString(R.string.toast_brightness) + intent.getStringExtra(EXTRA));
                break;
            case "com.sferadev.qpair.UPDATE_CLIPBOARD":
                setClipboardString(intent.getStringExtra(EXTRA));
                createToast(getContext().getString(R.string.toast_clipboard) + intent.getStringExtra(EXTRA));
                break;
            case "com.sferadev.qpair.UNINSTALL_PACKAGE":
                setPreferences(KEY_LAST_APP, intent.getStringExtra(EXTRA));
                if (isPackageInstalled(intent.getStringExtra(EXTRA))) {
                    uninstallPackage(intent.getStringExtra(EXTRA));
                } else {
                    createToast(getContext().getString(R.string.toast_uninstall_failed) + intent.getStringExtra(EXTRA));
                }
                break;
            default:
                createToast(intent.getAction());
        }
    }
}
