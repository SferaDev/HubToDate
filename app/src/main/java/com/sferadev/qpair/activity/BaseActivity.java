package com.sferadev.qpair.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sferadev.qpair.R;

import static com.sferadev.qpair.utils.Utils.openURL;

// BaseActivity that provides a common standard among the project.
public abstract class BaseActivity extends ActionBarActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_community:
                openURL(getString(R.string.community_url));
                return true;
            case R.id.menu_translate:
                openURL(getString(R.string.translate_url));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract int getLayoutResource();
}