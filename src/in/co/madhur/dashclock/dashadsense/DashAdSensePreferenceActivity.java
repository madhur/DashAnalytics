package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.BasePreferenceActivity;
import in.co.madhur.dashclock.Consts;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.AppPreferences.Keys;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

public class DashAdSensePreferenceActivity extends BasePreferenceActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(Consts.ADSENSE_PREFERENCE_NAME);
        prefMgr.setSharedPreferencesMode(MODE_WORLD_READABLE);

		appPreferences = new AdSensePreferences(this);
		addPreferencesFromResource(R.xml.adsense_preference);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		UpdateLabel((ListPreference) findPreference(Keys.ADSENSE_PERIOD_ID.key), null);
	}


	@Override
	protected void SetListeners()
	{
		super.SetListeners();

		findPreference(Keys.ADSENSE_PERIOD_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

	}

}
