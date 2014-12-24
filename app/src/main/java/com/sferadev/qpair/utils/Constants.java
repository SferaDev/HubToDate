package com.sferadev.qpair.utils;

import com.sferadev.qpair.R;

import static com.sferadev.qpair.App.getContext;

public class Constants {
    // Possible actions coming from Peer device inside Intent
    public static final String ACTION_CHANGE_IME = "com.sferadev.qpair.CHANGE_IME";
    public static final String ACTION_CALLBACK_FAILURE = "com.sferadev.qpair.CALLBACK_FAILURE";
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

    // Tag to identify extras within an intent
    public static final String EXTRA = "qpairExtra";

    public static final String EXTRA_LOCAL_VERSION = "/local/qpair/version";
    public static final String EXTRA_PEER_VERSION = "/peer/qpair/version";
    public static final String EXTRA_QPAIR_DEVICE_TYPE = "/local/qpair/device_type";
    public static final String EXTRA_QPAIR_IS_CONNECTED = "/local/qpair/is_connected";
    public static final String EXTRA_QPAIR_IS_ON = "/local/qpair/is_on";

    public static final String EXTRA_SCHEME_AUTHORITY = com.lge.qpair.api.r2.QPairConstants.PROPERTY_SCHEME_AUTHORITY;

    // Flag to launch as floating on supported ROMs
    public static final int FLAG_FLOATING_WINDOW = 0x00002000;

    // Keys to identify what's going on
    public static final String KEY_ALWAYS_RINGER = "alwaysRinger";
    public static final String KEY_ALWAYS_WIFI = "alwaysWifi";
    public static final String KEY_HAS_VOTED = "hasVoted";
    public static final String KEY_IS_COMMUNITY = "isCommunity";
    public static final String KEY_IS_CONNECTED = "isConnected";
    public static final String KEY_IS_ON = "isOn";
    public static final String KEY_IS_PHONE = "isPhone";
    public static final String KEY_LAST_APP = "lastApp";
    public static final String KEY_LAST_RINGER_MODE = "lastRingerMode";
    public static final String KEY_SYNC_APPS = "syncApps";
    public static final String KEY_SYNC_VOLUME = "syncVolume";
    public static final String KEY_SYNC_WIFI = "syncWifi";



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
}
