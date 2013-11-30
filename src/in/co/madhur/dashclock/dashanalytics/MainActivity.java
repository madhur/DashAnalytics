package in.co.madhur.dashclock.dashanalytics;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.BaseActivity;
import in.co.madhur.dashclock.MyBaseAdapter;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.GNewProfile;
import in.co.madhur.dashclock.API.GProfile;
import in.co.madhur.dashclock.API.GProperty;
import in.co.madhur.dashclock.AppPreferences.Keys;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
//import com.google.common.collect.ListMultimap;
import android.app.ActionBar.OnNavigationListener;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends BaseActivity

{

	private Analytics analytics_service;

	//ListMultimap<GProperty, GProfile> propertiesMap;

	OnNavigationListener listNavigator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		appPreferences = new AnalyticsPreferences(this);

		List<String> scopes = new ArrayList<String>();

		scopes.add(AnalyticsScopes.ANALYTICS_READONLY);

		credential = GoogleAccountCredential.usingOAuth2(this, scopes);
		if (TextUtils.isEmpty(appPreferences.getUserName()))
		{
			try
			{

				startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			}
			catch (ActivityNotFoundException e)
			{

				Toast.makeText(this, getString(R.string.gps_missing), Toast.LENGTH_LONG).show();

				return;
			}

		}
		else
		{
			setNavigationList(appPreferences.getUserName());
		}
	}

	@Override
	protected void PersistPreferences(GNewProfile newProfile, String accountEmail)
	{
		if (newProfile != null)
		{
			appPreferences.setMetadataMultiple(newProfile.getAccountId(), newProfile.getAccountName(), newProfile.getPropertyId(), newProfile.getPropertyName(), newProfile.getProfileId(), newProfile.getProfileName(), accountEmail);
		}

	}
	
	protected void UpdateSelectionPreferences()
	{
		String accountId = appPreferences.getMetadata(Keys.ACCOUNT_ID);
		String propertyId = appPreferences.getMetadata(Keys.PROPERTY_ID);
		String profileId = appPreferences.getMetadata(Keys.PROFILE_ID);
		String metricId = appPreferences.getMetadata(Keys.METRIC_ID);
		String periodId = appPreferences.getMetadata(Keys.PERIOD_ID);

		if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(propertyId)
				&& !TextUtils.isEmpty(profileId)
				&& !TextUtils.isEmpty(metricId) && !TextUtils.isEmpty(periodId))
		{
			int position = GProfile.getItemPositionByProfileId(acProfiles, profileId);
			if (position != -1)
			{

				listView.setItemChecked(position, true);
			}

		}
	}

	

	@Override
	protected Analytics getService(GoogleAccountCredential credential)
	{
		return new Analytics.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(getString(R.string.app_name)).build();

	}

	@Override
	protected void setService()
	{
		analytics_service = getService(credential);

	}

	@Override
	protected void setServiceObject()
	{
		((AnalyticsDataService) mService).analytics_service = analytics_service;

	}

	@Override
	protected MyBaseAdapter getListAdater(ArrayList<GNewProfile> acProfiles, Context baseActivity)
	{
		return new MyAdapter(acProfiles, baseActivity);
	}

}
