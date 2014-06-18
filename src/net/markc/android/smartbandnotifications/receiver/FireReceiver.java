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

package net.markc.android.smartbandnotifications.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;

import java.util.Locale;

import net.markc.android.smartbandnotifications.Constants;
import net.markc.android.smartbandnotifications.R;
import net.markc.android.smartbandnotifications.ui.EditActivity;
import net.markc.android.smartbandnotifications.ui.SettingsActivity;
import net.markc.android.smartbandnotifications.bundle.BundleScrubber;
import net.markc.android.smartbandnotifications.bundle.PluginBundleManager;


/**
 * This is the "fire" BroadcastReceiver for a Locale Plug-in setting.
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class FireReceiver extends BroadcastReceiver
{


	private static int NOTIFICATION_ID = 7567;
	
	/**
     * @param context {@inheritDoc}.
     * @param intent the incoming {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING} Intent. This
     *            should contain the {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} that was saved by
     *            {@link EditActivity} and later broadcast by Locale.
     */
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        /*
         * Always be strict on input parameters! A malicious third-party app could send a malformed Intent.
         */

        if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
        {
            if (Constants.IS_LOGGABLE)
            {
                Log.e(Constants.LOG_TAG,
                      String.format(Locale.US, "Received unexpected Intent action %s", intent.getAction())); //$NON-NLS-1$
            }
            return;
        }

        BundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(bundle);

        if (PluginBundleManager.isBundleValid(bundle))
        {
        	
        	final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    	       	
        	final Handler handler = new Handler();
            final int action = bundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_ACTION);
        	final int count = bundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_NOTIFICATION_COUNT);
        	final int interval = bundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_INTERVAL);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, new Intent(), 0);
            
            final NotificationCompat.Builder mBuilder = 
            		new NotificationCompat.Builder(context)
            		.setPriority(NotificationCompat.PRIORITY_MIN) 
            	    .setSmallIcon(R.drawable.ic_launcher)
            	    .setContentTitle(context.getString(R.string.app_name))
            	    .setContentIntent(pendingIntent); //Required on Gingerbread and below
            
            final Notification notification = mBuilder.build();
            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            // Trigger
			if (action == 1) {
			
				Runnable runnable = new Runnable() {
				    @Override
				    public void run() {
				    	int i = 0;
				    	while (i <= count || count == 0) {
				            final int value = i;
				            i++;
				            if (value == count && count != 0) {
				            	notificationManager.cancel(NOTIFICATION_ID);
				            } else {
				            	if (runNotify(value, handler, preferences, notificationManager, notification)) {
						            try {
						                Thread.sleep(interval);
						            } catch (InterruptedException e) {
						                e.printStackTrace();
						            }
				            	} else {
				            		notificationManager.cancel(NOTIFICATION_ID);
				            		break;
						        }
				            }
				        }
				    }
				};
				new Thread(runnable).start();
			// Cancel
			} else {
				setNotificationStatus(preferences, false);
			}
			
        } 
        
    }
    
	private synchronized boolean runNotify(int count, Handler handler, final SharedPreferences preferences, final NotificationManager notificationManager, final Notification notification) {
		if (count == 0 || getNotificationStatus(preferences)) {
        	handler.post(new Runnable() {
                @Override
                public void run() {
                		setNotificationStatus(preferences, true);
                		notificationManager.notify(NOTIFICATION_ID, notification);
                }
            });
        	return true;
    	}
		return false;
	}

    private void setNotificationStatus(SharedPreferences preferences, boolean allowed) {
    	SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("notification_allowed", allowed);
		editor.commit();
    }
    
    private boolean getNotificationStatus(SharedPreferences preferences) {
		return preferences.getBoolean("notification_allowed", true);
    }
    
    
    
}