package in.co.madhur.dashclock.dashanalytics;

import in.co.madhur.dashclock.BasePreferenceActivity;
import in.co.madhur.dashclock.Consts;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.AppPreferences.ANALYTICS_KEYS;
import in.co.madhur.dashclock.AppPreferences.Keys;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class DashAnalyticsPreferenceActivity extends BasePreferenceActivity
{
	
	@Override
	protected void EnableDisablePreferences()
	{
		
		
		int count = 0;
		if(appPreferences.getboolMetaData(Keys.SHOW_ANALYTICS_LASTUPDATE) )
			count++;
		
		if(appPreferences.getboolMetaData(Keys.SHOW_PROFILE))
			count++;
		
		for (ANALYTICS_KEYS key2 : ANALYTICS_KEYS.values())
		{
			if (appPreferences.getAnalyticProperty(key2))
			{
				count++;

			}
		}

		if (count >4)
		{
			Toast.makeText(getBaseContext(), "Upto 5 Additional attributes can be displayed", Toast.LENGTH_LONG).show();
			for (ANALYTICS_KEYS key2: ANALYTICS_KEYS.values())
			{
				if (!appPreferences.getAnalyticProperty(key2))
				{
					findPreference(key2.key).setEnabled(false);

				}
				
			}
			
			if(!appPreferences.getboolMetaData(Keys.SHOW_PROFILE))
				findPreference(Keys.SHOW_PROFILE.key).setEnabled(false);
			
			if(!appPreferences.getboolMetaData(Keys.SHOW_ANALYTICS_LASTUPDATE))
				findPreference(Keys.SHOW_ANALYTICS_LASTUPDATE.key).setEnabled(false);
			

		}
		else
		{
			for (ANALYTICS_KEYS key2 : ANALYTICS_KEYS.values())
			{
				if (!appPreferences.getAnalyticProperty(key2))
				{
					findPreference(key2.key).setEnabled(true);

				}
				
			}
			
			findPreference(Keys.SHOW_PROFILE.key).setEnabled(true);
			
			findPreference(Keys.SHOW_ANALYTICS_LASTUPDATE.key).setEnabled(true);
			
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(Consts.ANALYTICS_PREFERENCE_NAME);
        prefMgr.setSharedPreferencesMode(Context.MODE_PRIVATE);

		appPreferences = new AnalyticsPreferences(this);
		addPreferencesFromResource(R.xml.preference);
		
		this.appPreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
		
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		UpdateLabel((ListPreference) findPreference(Keys.METRIC_ID.key), null);
		UpdateLabel((ListPreference) findPreference(Keys.PERIOD_ID.key), null);
		
		
		
	}

}
