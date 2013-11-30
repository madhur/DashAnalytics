package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.AppPreferences;
import in.co.madhur.dashclock.BaseActivity;
import in.co.madhur.dashclock.Connection;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.AccountResult;
import in.co.madhur.dashclock.API.GNewProfile;
import in.co.madhur.dashclock.API.GProfile;
import in.co.madhur.dashclock.API.GProperty;
import in.co.madhur.dashclock.AppPreferences.Keys;
import in.co.madhur.dashclock.dashanalytics.AnalyticsDataService;
import in.co.madhur.dashclock.dashanalytics.AnalyticsPreferences;
import in.co.madhur.dashclock.dashanalytics.DashAnalyticsPreferenceActivity;
import in.co.madhur.dashclock.dashanalytics.MyAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSenseScopes;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;

import com.google.common.collect.ListMultimap;
import com.squareup.otto.Subscribe;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity

{
	private AdSense adsense_service;
	
	
	ListMultimap<GProperty, GProfile> propertiesMap;

	OnNavigationListener listNavigator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		appPreferences=new AdSensePreferences(this);
		
		

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
	protected void setNavigationList(String accountName)
	{
		super.setNavigationList(accountName);
		
		ArrayList<String> navItems = getAccountsList();
		navItems.add("Add Account");
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActionBar().getThemedContext(), R.layout.spinner_item, navItems);
		adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

		
		if (accountName != null)
		{
			int index = navItems.indexOf(accountName);
			if (index != -1)
				getActionBar().setSelectedNavigationItem(index);
			else
				Log.e(App.TAG, "acount not found");
		}
		
		
		
		getActionBar().setListNavigationCallbacks(adapter, new OnNavigationListener()
		{

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId)
			{
				String selItem = adapter.getItem(itemPosition);

				if (selItem.equals("Add Account"))
				{
					startAddGoogleAccountIntent();
					return true;
				}

				if (getAccountsList().size() >= itemPosition)
				{
					if (App.LOCAL_LOGV)
						Log.v(App.TAG, "Fetching accounts for:"
								+ getAccountsList().get(itemPosition));

					credential.setSelectedAccountName(getAccountsList().get(itemPosition));

					setService();
					if (adsense_service == null)
						Log.e(App.TAG, "analytics service is null");

					if (mBound)
						unbindService(mConnection);

					getAccounts();
				}

				return true;
			}
		});

	}
	
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
	protected void setServiceObject()
	{
		((AdsenseDataService)mService).adsense_service=adsense_service;
		
	}



	@Override
	protected Object getService(GoogleAccountCredential credential2)
	{
		AdSense adsense = new AdSense.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(getString(R.string.app_name)).build();

		return adsense;
	}



	@Override
	protected void setService()
	{
		adsense_service=(AdSense) getService(credential);
		
	}

	



	
}
