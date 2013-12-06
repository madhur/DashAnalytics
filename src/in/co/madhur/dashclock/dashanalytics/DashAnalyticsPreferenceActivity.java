package in.co.madhur.dashclock.dashanalytics;

import in.co.madhur.dashclock.BasePreferenceActivity;
import in.co.madhur.dashclock.Consts;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.AppPreferences.ADSENSE_KEYS;
import in.co.madhur.dashclock.AppPreferences.ANALYTICS_KEYS;
import in.co.madhur.dashclock.AppPreferences.Keys;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class DashAnalyticsPreferenceActivity extends BasePreferenceActivity
{

	@Override
	protected void EnableDisablePreferences(boolean loading)
	{

		EnableDisablePreferences(loading, 4);

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

		prefKeys.clear();
		for (ANALYTICS_KEYS key : ANALYTICS_KEYS.values())
		{

			prefKeys.add(key.key);
		}

		prefKeys.add(Keys.SHOW_PROFILE.key);
		prefKeys.add(Keys.SHOW_ANALYTICS_LASTUPDATE.key);

		EnableDisablePreferences(true);

	}

	@Override
	protected void SetListeners()
	{
		super.SetListeners();

		findPreference(Keys.METRIC_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

		findPreference(Keys.PERIOD_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

	}

	@Override
	protected Drawable getIcon()
	{
		return this.getResources().getDrawable(R.drawable.ic_dashanalytics);
	}

}
