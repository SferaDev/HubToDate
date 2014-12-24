package com.sferadev.qpair.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.sferadev.qpair.R;

import static com.sferadev.qpair.utils.Constants.EXTRA;

public class TaskerActivity extends PreferenceActivity {

    final Intent resultIntent = new Intent();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.tasker);
        getPreferenceScreen().findPreference("key_tasker_option").setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object object) {
                        resultIntent.putExtra(EXTRA, object.toString());
                        resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, object.toString());
                        finish();
                        return true;
                    }
                }
        );
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, resultIntent);
        super.finish();
    }

}