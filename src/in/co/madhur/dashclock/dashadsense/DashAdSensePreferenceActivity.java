package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.BasePreferenceActivity;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.AppPreferences.Keys;
import android.os.Bundle;
import android.preference.ListPreference;

public class DashAdSensePreferenceActivity extends BasePreferenceActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		appPreferences = new AdSensePreferences(this);
		addPreferencesFromResource(R.xml.adsense_preference);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		UpdateLabel((ListPreference) findPreference(Keys.PERIOD_ID.key), null);
	}


	@Override
	protected void SetListeners()
	{

		findPreference(Keys.PERIOD_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

	}

}
