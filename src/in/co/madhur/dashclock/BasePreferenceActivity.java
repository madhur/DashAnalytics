package in.co.madhur.dashclock;

import com.google.android.apps.dashclock.configuration.AppChooserPreference;

import in.co.madhur.dashclock.AppPreferences;
import in.co.madhur.dashclock.AppPreferences.Keys;
import in.co.madhur.dashclock.dashadsense.DashAdSensePreferenceActivity;
import in.co.madhur.dashclock.dashanalytics.DashAnalyticsPreferenceActivity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.inputmethodservice.Keyboard.Key;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

public abstract class BasePreferenceActivity extends PreferenceActivity
{
	protected AppPreferences appPreferences;

	protected final OnPreferenceChangeListener listPreferenceChangeListerner = new OnPreferenceChangeListener()
	{

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue)
		{
			UpdateLabel((ListPreference) preference, newValue.toString());
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				break;
		}
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		SetListeners();

	}

	protected void UpdateLabel(ListPreference listPreference, String newValue)
	{

		if (newValue == null)
		{
			newValue = listPreference.getValue();
		}

		int index = listPreference.findIndexOfValue(newValue);
		if (index != -1)
		{
			newValue = (String) listPreference.getEntries()[index];
			listPreference.setTitle(newValue);
		}

	}

	protected void SetListeners()
	{

		final String intentKey;
		Keys clickIntentKey;
		if (this instanceof DashAnalyticsPreferenceActivity)
		{
			intentKey = Keys.ANALYTICS_CLICK_INTENT.key;
			clickIntentKey=Keys.ANALYTICS_CLICK_INTENT;
		}

		else 
		{
			intentKey = Keys.ADSENSE_CLICK_INTENT.key;
			clickIntentKey=Keys.ADSENSE_CLICK_INTENT;
		}

		CharSequence intentSummary = AppChooserPreference.getDisplayValue(this, appPreferences.getMetadata(clickIntentKey));
		getPreferenceScreen().findPreference(intentKey).setSummary(TextUtils.isEmpty(intentSummary)
				|| intentSummary.equals(getString(R.string.pref_shortcut_default)) ? ""
				: intentSummary);

		getPreferenceScreen().findPreference(intentKey).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
		{

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				CharSequence intentSummary = AppChooserPreference.getDisplayValue(getBaseContext(), newValue.toString());
				getPreferenceScreen().findPreference(intentKey).setSummary(TextUtils.isEmpty(intentSummary)
						|| intentSummary.equals(getResources().getString(R.string.pref_shortcut_default)) ? ""
						: intentSummary);
				return true;
			}

		});
	}

}
