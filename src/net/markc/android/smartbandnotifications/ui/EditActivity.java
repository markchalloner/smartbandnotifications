/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package net.markc.android.smartbandnotifications.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import net.markc.android.smartbandnotifications.R;
import net.markc.android.smartbandnotifications.bundle.BundleScrubber;
import net.markc.android.smartbandnotifications.bundle.PluginBundleManager;

/**
 * This is the "Edit" activity for a Locale Plug-in.
 * <p>
 * This Activity can be started in one of two states:
 * <ul>
 * <li>New plug-in instance: The Activity's Intent will not contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE}.</li>
 * <li>Old plug-in instance: The Activity's Intent will contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} from a previously saved plug-in instance that the
 * user is editing.</li>
 * </ul>
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_EDIT_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class EditActivity extends AbstractPluginActivity
{

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        BundleScrubber.scrub(getIntent());

        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(localeBundle);

        addPreferencesFromResource(R.xml.pref_edit);
        
        final ListPreference actionPreference = (ListPreference) findPreference("action");
        final EditTextPreference countPreference = (EditTextPreference) findPreference("count");
        final EditTextPreference intervalPreference = (EditTextPreference) findPreference("interval");
               
        actionPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        	public boolean onPreferenceChange(Preference preference, Object newValue) {
            	final int val = Integer.parseInt(newValue.toString());
            	setPreferenceEnabled(countPreference, intervalPreference, val);
        		return true;
        	}
        });
        
        //bindPreferenceSummaryToValue(actionPreference);
        //bindPreferenceSummaryToValue(countPreference);
        //bindPreferenceSummaryToValue(intervalPreference);
        
        if (null == savedInstanceState)
        {
            if (PluginBundleManager.isBundleValid(localeBundle))
            {
                
            	final int action = localeBundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_ACTION);
            	final int count = localeBundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_NOTIFICATION_COUNT);
            	final int interval = localeBundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_INTERVAL);
            	
            	setIntegerPreference("count", count);
            	setIntegerPreference("interval", interval);
            	
            	//triggerPreferenceSummaryToValue(actionPreference);
            	//triggerPreferenceSummaryToValue(countPreference);
            	//triggerPreferenceSummaryToValue(intervalPreference);
            	//Log.e("SmartBand Notifications", "Action: " + action);
            	setPreferenceEnabled(countPreference, intervalPreference, action);
            	
            	
            }
        }
    }
    
    private static void setPreferenceEnabled(Preference countPreference, Preference intervalPreference, int val) {
    	if (val == 1) {
			countPreference.setEnabled(true);
			intervalPreference.setEnabled(true);
		} else { 
			countPreference.setEnabled(false);
			intervalPreference.setEnabled(false);
		}
    }

	private static void bindPreferenceSummaryToValue(Preference preference) {
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
	}
	
	private static void triggerPreferenceSummaryToValue(Preference preference) {
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
			preference,
			PreferenceManager.getDefaultSharedPreferences(
				preference.getContext()).getString(preference.getKey(),
						""
			)
		);
	}
	
	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			} else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);

				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone
								.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	public int getIntegerPreference(String key, int def) {
	// TODO Auto-generated method stub
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	    return Integer.parseInt(preferences.getString(key, def + ""));
	}
	
	public void setIntegerPreference(String key, int value){
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putString(key, value + "");
	    editor.commit();
	}



    @Override
    public void finish()
    {
        if (!isCanceled())
        {
       	
        	// Action
        	final int action_default = 1;
        	final int action = getIntegerPreference("action", action_default);
        	
        	// Count
        	final int count_default = 0;
        	int count_num = getIntegerPreference("count", count_default);
        	String count_text = count_num + "";
        	if (count_num <= 0) {
        		count_num = 0;
        		count_text = "infinite";
        	}
        	final int count = count_num;
        	
        	// Interval
        	final int interval_default = 2000;
        	final int interval_min = 1000;
        	int interval_num = getIntegerPreference("interval", interval_default);
        	if (interval_num < interval_min) {
        		interval_num = interval_min;
        	}
        	final int interval = interval_num;
        	
        	// Blurb
        	String blurb = "";
        	if (action == 1) {
        		blurb = "Trigger " + count_text + " notifications with " + interval + " ms between each.";
        	} else {
        		blurb = "Cancel all notifications.";
        	}
            
        	// Return
        	final Intent resultIntent = new Intent();
			final Bundle resultBundle = PluginBundleManager.generateBundle(getApplicationContext(), action, count, interval);
			resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
			resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, blurb);
			setResult(RESULT_OK, resultIntent);
			
        }

        super.finish();
    }

}