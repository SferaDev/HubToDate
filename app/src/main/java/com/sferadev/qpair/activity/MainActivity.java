package com.sferadev.qpair.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.sferadev.qpair.R;
import com.sferadev.qpair.egg.EggActivity;
import com.sferadev.qpair.egg.LGEggActivity;
import com.sferadev.qpair.service.ShakeService;
import com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar.OnProgressChangeListener;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.QPairUtils.getQpairIntent;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isPhone;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.isR2D2;
import static com.sferadev.qpair.utils.Utils.ACTION_CHANGE_WIFI;
import static com.sferadev.qpair.utils.Utils.ACTION_OPEN_URL;
import static com.sferadev.qpair.utils.Utils.ACTION_SCREEN_OFF;
import static com.sferadev.qpair.utils.Utils.ACTION_SHOW_TOUCHES;
import static com.sferadev.qpair.utils.Utils.ACTION_UPDATE_BRIGHTNESS;
import static com.sferadev.qpair.utils.Utils.ACTION_UPDATE_CLIPBOARD;
import static com.sferadev.qpair.utils.Utils.EXTRA;
import static com.sferadev.qpair.utils.Utils.FLAG_FLOATING_WINDOW;
import static com.sferadev.qpair.utils.Utils.KEY_IS_COMMUNITY;
import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createInputDialog;
import static com.sferadev.qpair.utils.Utils.createSnackbar;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getOwnerName;
import static com.sferadev.qpair.utils.Utils.getPreference;
import static com.sferadev.qpair.utils.Utils.getSystemPreference;
import static com.sferadev.qpair.utils.Utils.isPackageInstalled;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;
import static com.sferadev.qpair.utils.Utils.myDialogView;
import static com.sferadev.qpair.utils.Utils.openPlayStore;
import static com.sferadev.qpair.utils.Utils.openURL;
import static com.sferadev.qpair.utils.Utils.setPreference;

// MainActivity that handles the creation of main UI elements
public class MainActivity extends BaseActivity {
    private Activity myActivity = this;
    protected int eggTaps = 0;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Some important stuff before UI is finally loaded
        checkQPairIsInstalled();
        checkVersion();
        showCommunitySnackBar();
        loadService();
        updateWelcome();

        // Handle the Slide Up menu
        DiscreteSeekBar brightnessSlider = (DiscreteSeekBar) findViewById(R.id.slide_brightness);
        brightnessSlider.setMax(255);
        brightnessSlider.setProgress(getSystemPreference(android.provider.Settings.System.SCREEN_BRIGHTNESS));
        brightnessSlider.setOnProgressChangeListener(new OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (isQPairOn() && isConnected()) {
                    getContext().bindService(getQpairIntent(),
                            new sendBroadcastConnection(ACTION_UPDATE_BRIGHTNESS, EXTRA,
                                    value), 0);
                } else {
                    createToast(getString(R.string.toast_not_connected));
                }
            }
        });

        SwitchCompat wifiSwitch = (SwitchCompat) findViewById(R.id.slide_wifi);
        wifiSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isQPairOn() && isConnected()) {
                    getContext().bindService(getQpairIntent(),
                            new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA,
                                    String.valueOf(isChecked)), 0);
                } else {
                    createToast(getString(R.string.toast_not_connected));
                }
            }
        });

        SwitchCompat touchesSwitch = (SwitchCompat) findViewById(R.id.slide_touches);
        touchesSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isQPairOn() && isConnected()) {
                    getContext().bindService(getQpairIntent(),
                            new sendBroadcastConnection(ACTION_SHOW_TOUCHES, EXTRA,
                                    String.valueOf(isChecked)), 0);
                } else {
                    createToast(getString(R.string.toast_not_connected));
                }
            }
        });

        String[] data = {
                getString(R.string.slide_clipboard),
                getString(R.string.slide_url),
                getString(R.string.slide_screen)
        };

        ListView listView = (ListView) findViewById(R.id.slide_list);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.slide_item, android.R.id.text1, data));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        createInputDialog(getString(R.string.slide_clipboard), new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText inputText = (EditText) myDialogView.findViewById(R.id.dialog_input);
                                getContext().bindService(getQpairIntent(),
                                        new sendBroadcastConnection(ACTION_UPDATE_CLIPBOARD, EXTRA,
                                                inputText.getText().toString()), 0);
                            }
                        }, null);
                        break;
                    case 1:
                        createInputDialog(getString(R.string.slide_url), new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText inputText = (EditText) myDialogView.findViewById(R.id.dialog_input);
                                getContext().bindService(getQpairIntent(),
                                        new sendBroadcastConnection(ACTION_OPEN_URL, EXTRA,
                                                inputText.getText().toString()), 0);
                            }
                        }, null);
                        break;
                    case 2:
                        getContext().bindService(getQpairIntent(),
                                new sendBroadcastConnection(ACTION_SCREEN_OFF, EXTRA,
                                        "screenOff"), 0);
                        break;
                }
            }
        });

        // Handle the Easter Egg
        CardView welcomeCard = (CardView) findViewById(R.id.main_hello);
        welcomeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eggTaps > 7) {
                    eggTaps = 0;
                    if (isPackageInstalled(getString(R.string.qpair_package)) && !isPhone()) {
                        Intent intent = new Intent(getContext(), LGEggActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(getString(R.string.qslide_extra), true);
                        getContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), EggActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(FLAG_FLOATING_WINDOW);
                        getContext().startActivity(intent);
                    }
                } else {
                    createToast("Tap, tap, tap!");
                    eggTaps++;
                }
            }
        });
    }

    // Check if User has QPair installed and show a nice dialog!
    private void checkQPairIsInstalled() {
        try {
            if (!isPackageInstalled(getString(R.string.qpair_package))) {
                createDialog(getString(R.string.dialog_qpair_not_installed), getString(R.string.dialog_qpair_not_installed_description), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPlayStore(getString(R.string.qpair_package));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Check if User is in r2 and show a nice dialog telling them to update
    private void checkVersion() {
        try {
            if (!isR2D2()) {
                createDialog(getString(R.string.dialog_update_qpair), getString(R.string.dialog_update_qpair_description), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCommunitySnackBar() {
        if (!getPreference(KEY_IS_COMMUNITY, false)) {
            createSnackbar(this, getString(R.string.dialog_community_description), "GO", new ActionClickListener() {
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    setPreference(KEY_IS_COMMUNITY, true);
                    openURL(getString(R.string.community_url));
                }
            });
        }
    }

    // Load Shake Service if off
    private void loadService() {
        if (!isServiceRunning(ShakeService.class)) {
            Intent intent = new Intent(this, ShakeService.class);
            this.startService(intent);
        }
    }


    // Get the name of the device Owner and display it on a Card Text
    private void updateWelcome() {
        try {
            if (getOwnerName() != null) {
                TextView welcomeText = (TextView) findViewById(R.id.info_text_1);
                welcomeText.setText(getString(R.string.welcome) + " " + getOwnerName() + "!");
            }
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
