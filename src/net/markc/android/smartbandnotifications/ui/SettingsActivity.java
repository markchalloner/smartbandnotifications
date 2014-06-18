package net.markc.android.smartbandnotifications.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import net.markc.android.smartbandnotifications.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.pref_settings);
		
		
		
		
		
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor editor = preferences.edit();
		
		final CheckBoxPreference enabledPreference = (CheckBoxPreference) findPreference("enabled");
               
		enabledPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference preference, Object newValue) {
        		//editor.putBoolean("enabled", newValue.toString().equals("true"));
        		Log.e("SmartBand Settings", "Enabled: " + preferences.getBoolean("enabled", false));
        		editor.commit();
        		Log.e("SmartBand Settings", "Enabled: " + preferences.getBoolean("enabled", false));
        		Log.e("SmartBand Settings", "----");
            	//final int val = Integer.parseInt(newValue.toString());
            	//setPreferenceEnabled(countPreference, intervalPreference, val);
        		return true;
        	}
        });
		  
		

	}

}
