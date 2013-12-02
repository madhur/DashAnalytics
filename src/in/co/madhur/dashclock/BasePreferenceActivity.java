package in.co.madhur.dashclock;

import com.google.android.apps.dashclock.configuration.AppChooserPreference;

import in.co.madhur.dashclock.AppPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.TextUtils;
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
		String packageName = "dummy.xx.name";

		PackageManager pm = this.getPackageManager();

		Resources r = getResources();
		ApplicationInfo content = null;
		try
		{
			content = pm.getApplicationInfo(packageName, 0);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		final String appName = pm.getApplicationLabel(content).toString();

		CharSequence intentSummary = AppChooserPreference.getDisplayValue(this, "pref string");
		getPreferenceScreen().findPreference("click_intent").setSummary(TextUtils.isEmpty(intentSummary)
				|| intentSummary.equals(r.getString(R.string.pref_shortcut_default)) ? appName
				: intentSummary);
		
		getPreferenceScreen().findPreference("click_intent").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
		{

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				CharSequence intentSummary = AppChooserPreference.getDisplayValue(getBaseContext(), newValue.toString());
				getPreferenceScreen().findPreference("click_intent").setSummary(TextUtils.isEmpty(intentSummary)
						|| intentSummary.equals(getResources().getString(R.string.pref_shortcut_default)) ? appName
						: intentSummary);
				return true;
			}

		});
	}

}
