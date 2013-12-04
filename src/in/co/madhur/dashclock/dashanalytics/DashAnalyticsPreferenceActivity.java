package in.co.madhur.dashclock.dashanalytics;

import in.co.madhur.dashclock.BasePreferenceActivity;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.AppPreferences.Keys;
import android.os.Bundle;
import android.preference.ListPreference;

public class DashAnalyticsPreferenceActivity extends BasePreferenceActivity
{
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		appPreferences = new AnalyticsPreferences(this);
		addPreferencesFromResource(R.xml.preference);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		UpdateLabel((ListPreference) findPreference(Keys.METRIC_ID.key), null);
		UpdateLabel((ListPreference) findPreference(Keys.PERIOD_ID.key), null);
	}

	@Override
	protected void SetListeners()
	{
		super.SetListeners();
		findPreference(Keys.METRIC_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

		findPreference(Keys.PERIOD_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

	}
	
	

}
