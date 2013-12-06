package in.co.madhur.dashclock.dashadsense;

import java.util.ArrayList;

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

		

		this.appPreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		UpdateLabel((ListPreference) findPreference(Keys.ADSENSE_PERIOD_ID.key), null);

		prefKeys.clear();
		for (ADSENSE_KEYS key : ADSENSE_KEYS.values())
		{

			prefKeys.add(key.key);
		}

		prefKeys.add(Keys.SHOW_ADSENSE_LASTUPDATE.key);

		EnableDisablePreferences(true);

	}

	@Override
	protected void SetListeners()
	{
		super.SetListeners();

		findPreference(Keys.ADSENSE_PERIOD_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

	}

	@Override
	protected Drawable getIcon()
	{
		return this.getResources().getDrawable(R.drawable.ic_dashadsense);
	}

	@Override
	protected void EnableDisablePreferences(boolean loading)
	{
		EnableDisablePreferences(loading, 4);

	}

}
