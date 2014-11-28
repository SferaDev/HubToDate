package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.App;
import com.sferadev.qpair.R;
import com.sferadev.qpair.service.ShakeService;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;
import static com.sferadev.qpair.utils.Utils.ACTION_OPEN_PLAY_STORE;
import static com.sferadev.qpair.utils.Utils.EXTRA;
import static com.sferadev.qpair.utils.Utils.KEY_LAST_APP;
import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createExplicitFromImplicitIntent;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getPreferences;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;
import static com.sferadev.qpair.utils.Utils.setPreferences;

public class AppInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, final Intent intent) {

        if (!isServiceRunning(ShakeService.class)) {
            Intent serviceIntent = new Intent(getContext(), ShakeService.class);
            getContext().startService(serviceIntent);
        }

        if (isQPairOn() && isConnected()) {
            final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
            switch (intent.getAction()) {
                case "android.intent.action.PACKAGE_ADDED":
                    final String[] dataPackageAdded = intent.getData().toString().split(":");
                    if (getContext().getPackageManager().getInstallerPackageName(dataPackageAdded[1]) == null || !getContext().getPackageManager().getInstallerPackageName(dataPackageAdded[1]).equals(getContext().getString(R.string.play_package))) {
                        createToast(getContext().getString(R.string.toast_app_not_supported));
                        break;
                    }
                    createDialog(getContext().getString(R.string.dialog_install), getContext().getString(R.string.dialog_install_description), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dataPackageAdded[1] != getPreferences(KEY_LAST_APP, null)) {
                                setPreferences(KEY_LAST_APP, dataPackageAdded[1]);
                                getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA, dataPackageAdded[1]), 0);
                            }
                        }
                    }, null);
                    break;
                case "android.intent.action.PACKAGE_REMOVED":
                    final String[] dataPackageRemoved = intent.getData().toString().split(":");
                    createDialog(getContext().getString(R.string.dialog_uninstall), getContext().getString(R.string.dialog_uninstall_description), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dataPackageRemoved[1] != getPreferences(KEY_LAST_APP, null)) {
                                setPreferences(KEY_LAST_APP, dataPackageRemoved[1]);
                                getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA, dataPackageRemoved[1]), 0);
                            }
                        }
                    }, null);
                    break;
                default:
                    createToast(getContext().getString(R.string.toast_intent) + intent.getAction());
                    break;
            }
        }
    }
}