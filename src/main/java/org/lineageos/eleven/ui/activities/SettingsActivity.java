/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.lineageos.eleven.ui.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import org.lineageos.eleven.R;
import org.lineageos.eleven.ui.fragments.PreferenceFragment;
import org.lineageos.eleven.utils.MusicUtils;
import org.lineageos.eleven.utils.PreferenceUtils;

/**
 * Settings.
 *
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class SettingsActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener{

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        // Fade it in
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Calculate ActionBar height
        TypedValue value = new TypedValue();
        int height = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true)){
            height = TypedValue.complexToDimensionPixelSize(value.data,
                    getResources().getDisplayMetrics());
        }
        
        // Set the layout
        setContentView(R.layout.activity_settings);

        findViewById(R.id.activity_pref_content).setPadding(0, height, 0, 0);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.prefToolbar);
        setSupportActionBar(mToolBar);

        // Theme the toolbar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setTitle(getString(R.string.menu_settings));
        final int actionBarColor = ContextCompat.getColor(this, R.color.header_action_bar_color);
        ColorDrawable actionBarBackground = new ColorDrawable(actionBarColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        	mToolBar.setBackground(actionBarBackground);
        } else {
        	mToolBar.setBackgroundDrawable(actionBarBackground);
        }

        PreferenceUtils.getInstance(this).setOnSharedPreferenceChangeListener(this);

        // set the background on the root view
        getWindow().getDecorView().getRootView().setBackgroundColor(
        		ContextCompat.getColor(this, R.color.background_color));
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
            .replace(R.id.activity_pref_content, new PreferenceFragment())
            .commit(); 	
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        if (key.equals(PreferenceUtils.SHOW_VISUALIZER) &&
                sharedPreferences.getBoolean(key, false) && !PreferenceUtils.canRecordAudio(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PreferenceUtils.requestRecordAudio(this);    		   
            }
        } if (key.equals(PreferenceUtils.SHAKE_TO_PLAY)) {
            MusicUtils.setShakeToPlayEnabled(sharedPreferences.getBoolean(key, false));
        } else if (key.equals(PreferenceUtils.SHOW_ALBUM_ART_ON_LOCKSCREEN)) {
            MusicUtils.setShowAlbumArtOnLockscreen(sharedPreferences.getBoolean(key, true));
        }
    }
}
