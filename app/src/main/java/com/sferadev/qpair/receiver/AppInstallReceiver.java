package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.sferadev.qpair.R;
import com.sferadev.qpair.service.ShakeService;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.Constants.ACTION_OPEN_PLAY_STORE;
import static com.sferadev.qpair.utils.Constants.ACTION_UNINSTALL_PACKAGE;
import static com.sferadev.qpair.utils.Constants.KEY_LAST_APP;
import static com.sferadev.qpair.utils.Constants.KEY_SYNC_APPS;
import static com.sferadev.qpair.utils.PreferenceUtils.getPreference;
import static com.sferadev.qpair.utils.PreferenceUtils.setPreference;
import static com.sferadev.qpair.utils.QPairUtils.getQpairIntent;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;
import static com.sferadev.qpair.utils.UIUtils.createDialog;
import static com.sferadev.qpair.utils.UIUtils.createToast;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;

// Receiver of App Installs or Uninstalls.
public class AppInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, final Intent intent) {
        // Ensure our service is On
        if (!isServiceRunning(ShakeService.class)) {
            Intent serviceIntent = new Intent(getContext(), ShakeService.class);
            getContext().startService(serviceIntent);
        }

        if (isQPairOn() && isConnected() && getPreference(KEY_SYNC_APPS, true)) {
            switch (intent.getAction()) {
                // Whether the action is that a new App is installed
                case "android.intent.action.PACKAGE_ADDED":
                    // Get the packageName
                    final String[] dataPackageAdded = intent.getData().toString().split(":");
                    // If the app doesn't turns to be installed by Play Store return
                    if (getContext().getPackageManager().getInstallerPackageName(dataPackageAdded[1]) == null
                            || !getContext().getPackageManager().getInstallerPackageName(dataPackageAdded[1]).equals(getContext().getString(R.string.play_package))) {
                        createToast(getContext().getString(R.string.toast_app_not_supported));
                        setPreference(KEY_LAST_APP, dataPackageAdded[1]);
                        break;
                    }
                    // If we have not recently played with this app ask if he wants to install on Peer
                    if (!dataPackageAdded[1].equals(getPreference(KEY_LAST_APP, null))) {
                        setPreference(KEY_LAST_APP, dataPackageAdded[1]);
                        createDialog(getContext().getString(R.string.dialog_install), getContext().getString(R.string.dialog_install_description), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getContext().bindService(getQpairIntent(),
                                        new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, dataPackageAdded[1]), 0);
                            }
                        }, null);
                    }
                    break;
                // Whether the action is that an old App is uninstalled
                case "android.intent.action.PACKAGE_REMOVED":
                    // Get the packageName
                    final String[] dataPackageRemoved = intent.getData().toString().split(":");
                    // If we have not recently played with this app ask if he wants to uninstall on Peer
                    if (!dataPackageRemoved[1].equals(getPreference(KEY_LAST_APP, null))) {
                        setPreference(KEY_LAST_APP, dataPackageRemoved[1]);
                        createDialog(getContext().getString(R.string.dialog_uninstall), getContext().getString(R.string.dialog_uninstall_description), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getContext().bindService(getQpairIntent(),
                                        new sendBroadcastConnection(ACTION_UNINSTALL_PACKAGE, dataPackageRemoved[1]), 0);
                            }
                        }, null);
                    }
                    break;
                // Default case, this should never happen.
                default:
                    createToast(getContext().getString(R.string.toast_intent) + " " + intent.getAction());
                    break;
            }
        }
    }
}