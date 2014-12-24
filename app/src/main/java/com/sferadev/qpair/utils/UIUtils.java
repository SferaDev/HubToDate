package com.sferadev.qpair.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.sferadev.qpair.R;

import static com.sferadev.qpair.App.getContext;

public class UIUtils {
    public static View myDialogView = null;

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

    // Input Dialog Creation
    public static void createInputDialog(String title, OnClickListener positiveListener,
                                         OnClickListener negativeListener) {
        LayoutInflater factory = LayoutInflater.from(getContext());
        myDialogView = factory.inflate(R.layout.input_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext(),
                android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
                .setTitle(title)
                .setView(myDialogView)
                .setPositiveButton(getContext().getString(android.R.string.yes), positiveListener)
                .setNegativeButton(getContext().getString(android.R.string.no), negativeListener)
                .create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    // Snackbar Creation
    public static void createSnackbar(Activity activity, String string) {
        Snackbar.with(getContext())
                .text(string)
                .show(activity);
    }

    // Snackbar Creation
    public static void createSnackbar(Activity activity, String string,
                                      String action, ActionClickListener listener) {
        Snackbar.with(getContext())
                .text(string)
                .actionLabel(action)
                .actionListener(listener)
                .show(activity);
    }

    // Toast Creation
    public static void createToast(String string) {
        Toast toast = Toast.makeText(getContext(), string, Toast.LENGTH_LONG);
        toast.show();
    }
}
