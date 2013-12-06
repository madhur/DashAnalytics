package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.AppPreferences.ADSENSE_KEYS;
import in.co.madhur.dashclock.BasePreferenceActivity;
import in.co.madhur.dashclock.Consts;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.AppPreferences.Keys;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class DashAdSensePreferenceActivity extends BasePreferenceActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		
		PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(Consts.ADSENSE_PREFERENCE_NAME);
        prefMgr.setSharedPreferencesMode(Context.MODE_PRIVATE);
        
		appPreferences = new AdSensePreferences(this);
		addPreferencesFromResource(R.xml.adsense_preference);


		UpdateLabel((ListPreference) findPreference(Keys.ADSENSE_PERIOD_ID.key), null);
		
		this.appPreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
	
	}


	@Override
	protected void SetListeners()
	{
		super.SetListeners();

		findPreference(Keys.ADSENSE_PERIOD_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

	}

	
	@Override
	protected void EnableDisablePreferences(boolean loading)
	{
		
		
		int count = 0;
		if(appPreferences.getboolMetaData(Keys.SHOW_ADSENSE_LASTUPDATE) )
			count++;
		
		for (ADSENSE_KEYS key2 : ADSENSE_KEYS.values())
		{
			if (appPreferences.getAdsenseProperty(key2))
			{
				count++;

			}
		}

		if (count > 4)
		{
			if(!loading)
				Toast.makeText(getBaseContext(), getString(R.string.max_attributes_message), Toast.LENGTH_LONG).show();
			
			for (ADSENSE_KEYS key2: ADSENSE_KEYS.values())
			{
				if (!appPreferences.getAdsenseProperty(key2))
				{
					findPreference(key2.key).setEnabled(false);

				}
				
			}
			
			if(!appPreferences.getboolMetaData(Keys.SHOW_ADSENSE_LASTUPDATE))
				findPreference(Keys.SHOW_ADSENSE_LASTUPDATE.key).setEnabled(false);
			

		}
		else
		{
			for (ADSENSE_KEYS key2 : ADSENSE_KEYS.values())
			{
				if (!appPreferences.getAdsenseProperty(key2))
				{
					findPreference(key2.key).setEnabled(true);

				}
				
			}
			
			findPreference(Keys.SHOW_ADSENSE_LASTUPDATE.key).setEnabled(true);
			
		}

	}

	@Override
	protected Drawable getIcon()
	{
		return this.getResources().getDrawable(R.drawable.ic_dashadsense);
	}

}
