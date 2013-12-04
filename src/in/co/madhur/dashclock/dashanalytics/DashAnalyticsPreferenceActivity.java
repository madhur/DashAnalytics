package in.co.madhur.dashclock.dashanalytics;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.keyboardsurfer.android.widget.crouton.Style.Builder;
import in.co.madhur.dashclock.BasePreferenceActivity;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.AppPreferences.ANALYTICS_KEYS;
import in.co.madhur.dashclock.AppPreferences.Keys;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.widget.Toast;

public class DashAnalyticsPreferenceActivity extends BasePreferenceActivity
{

	OnPreferenceChangeListener changeListener =new OnPreferenceChangeListener()
	{
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue)
		{
			return EnableDisablePreferences( preference,  newValue);
		}
	};
	
	private boolean EnableDisablePreferences(Preference preference, Object newValue)
	{
		
		
		int count = 0;
		if(appPreferences.getboolMetaData(Keys.SHOW_ANALYTICS_LASTUPDATE) && !preference.getKey().equals(Keys.SHOW_ANALYTICS_LASTUPDATE.key))
			count++;
		
		if(appPreferences.getboolMetaData(Keys.SHOW_PROFILE) && !preference.getKey().equals(Keys.SHOW_PROFILE.key))
			count++;
		
		for (ANALYTICS_KEYS key2 : ANALYTICS_KEYS.values())
		{
			if (appPreferences.getAnalyticProperty(key2))
			{
				if(preference.getKey().equals(key2.key))
					continue;
				count++;

			}
			
		}

		if (count >5)
		{
			Toast.makeText(getBaseContext(), "Maximum 5 additional attributes can be displayed including Last Updated time and Profile Name.", Toast.LENGTH_LONG).show();
			for (ANALYTICS_KEYS key2: ANALYTICS_KEYS.values())
			{
				if (!appPreferences.getAnalyticProperty(key2) && !preference.getKey().equals(key2.key))
				{
					findPreference(key2.key).setEnabled(false);

				}
				
			}
			
			if(!preference.getKey().equals(Keys.SHOW_PROFILE.key) && !appPreferences.getboolMetaData(Keys.SHOW_PROFILE))
				findPreference(Keys.SHOW_PROFILE.key).setEnabled(false);
			
			if(!preference.getKey().equals(Keys.SHOW_ANALYTICS_LASTUPDATE.key) && !appPreferences.getboolMetaData(Keys.SHOW_ANALYTICS_LASTUPDATE))
				findPreference(Keys.SHOW_ANALYTICS_LASTUPDATE.key).setEnabled(false);
			
			if((Boolean) newValue)
			{
				preference.setEnabled(false);
				return false;
			}
			else 
				return true;

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

		return true;
		
		
		
		
		
	}
	
	private void EnableDisablePreferences1()
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

		if (count >5)
		{
			Toast.makeText(getBaseContext(), "Maximum 5 additional attributes can be displayed including Last Updated time and Profile Name.", Toast.LENGTH_LONG).show();
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

		appPreferences = new AnalyticsPreferences(this);
		addPreferencesFromResource(R.xml.preference);
		
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		UpdateLabel((ListPreference) findPreference(Keys.METRIC_ID.key), null);
		UpdateLabel((ListPreference) findPreference(Keys.PERIOD_ID.key), null);
		
		EnableDisablePreferences1();
		this.appPreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener()
		{
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
			{
				EnableDisablePreferences1();
				
			}
		});
	}

	@Override
	protected void SetListeners()
	{
		super.SetListeners();
		
		findPreference(Keys.SHOW_PROFILE.key).setOnPreferenceChangeListener(changeListener);
		findPreference(Keys.SHOW_ANALYTICS_LASTUPDATE.key).setOnPreferenceChangeListener(changeListener);
		
		findPreference(Keys.METRIC_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

		findPreference(Keys.PERIOD_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);
		
		

		for (final ANALYTICS_KEYS key1 : ANALYTICS_KEYS.values())
		{
			findPreference(key1.key).setOnPreferenceChangeListener(changeListener);
			

		}

	}

}
