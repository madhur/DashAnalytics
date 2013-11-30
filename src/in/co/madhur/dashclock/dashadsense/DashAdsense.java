package in.co.madhur.dashclock.dashadsense;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.AppPreferences;
import in.co.madhur.dashclock.Connection;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.APIResult;
import in.co.madhur.dashclock.AppPreferences.Keys;
import in.co.madhur.dashclock.Consts.API_STATUS;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSenseScopes;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.GaData;

public class DashAdsense extends DashClockExtension
{
	AppPreferences appPreferences;
	String ProfileId, metricKey, periodKey;
	private GoogleAccountCredential credential;
	private AdSense adsense_service;
	// private static FileDataStoreFactory dataStoreFactory;
	List<String> scopes = new ArrayList<String>();

	@Override
	protected void onUpdateData(int arg0)
	{
		// Check if user has changed the account, in that case, retrieve the new
		// credential object

		if (credential == null
				|| credential.getSelectedAccountName() == null
				|| !credential.getSelectedAccountName().equals(appPreferences.getUserName()))
		{
			Log.d(App.TAG, "Account changed, retrieving new cred object");

			try
			{
				credential = GoogleAccountCredential.usingOAuth2(this, scopes);
				credential.setSelectedAccountName(appPreferences.getUserName());
				adsense_service = initializeAdsense(credential);
			}
			catch (Exception e)
			{

				Log.e(App.TAG, "Exception in onInitialize" + e.getMessage());
			}
		}

		ProfileId = appPreferences.getMetadata(Keys.PROFILE_ID);
		metricKey = appPreferences.getMetadata(Keys.METRIC_ID);
		periodKey = appPreferences.getMetadata(Keys.PERIOD_ID);

		if (TextUtils.isEmpty(ProfileId))
		{
			Log.d(App.TAG, "Account not configured yet");
			return;
		}

		if (Connection.isConnected(this))
		{
			if (App.LOCAL_LOGV)
				Log.v(App.TAG, "Firing update:" + String.valueOf(arg0));

			// new APIResultTask().execute(ProfileId, metricKey, periodKey);
		}
		else
			Log.d(App.TAG, "No network, postponing update");
	}

	@Override
	protected void onInitialize(boolean isReconnect)
	{
		super.onInitialize(isReconnect);
		// appPreferences = new AppPreferences(this);

		scopes.add(AdSenseScopes.ADSENSE_READONLY);

	}

	// private Analytics getAnalyticsService(GoogleAccountCredential credential)
	// {
	// return new Analytics.Builder(AndroidHttp.newCompatibleTransport(), new
	// GsonFactory(),
	// credential).setApplicationName(getString(R.string.app_name)).build();
	//
	// }

	private AdSense initializeAdsense(GoogleAccountCredential credential)
			throws Exception
	{
		// Authorization.
		// Credential credential = authorize();

		// Set up AdSense Management API client.
		AdSense adsense = new AdSense.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(getString(R.string.app_name)).build();

		return adsense;
	}

	private class APIResultTask extends AsyncTask<String, Integer, APIResult>
	{

		@Override
		protected APIResult doInBackground(String... params)
		{
			return null;
		}

		@Override
		protected void onPostExecute(APIResult resultAPI)
		{
			// Do not do anything if there is a failure, could be network
			// condition.
			if (resultAPI.getStatus() == API_STATUS.FAILURE)
				return;

			GaData results = resultAPI.getResult();

			String profileName = appPreferences.getMetadata(Keys.PROFILE_NAME);
			String selectedProperty = appPreferences.getMetadata(Keys.PROPERTY_NAME);
			String metricKey = appPreferences.getMetadata(Keys.METRIC_ID);
			int metricIdentifier = getResources().getIdentifier(metricKey, "string", DashAdsense.this.getPackageName());
			int periodIdentifier = getResources().getIdentifier(periodKey, "string", DashAdsense.this.getPackageName());
			String result;

			if (App.LOCAL_LOGV)
				Log.v(App.TAG, "Processing result for " + profileName);

			if (results != null && results.getRows() != null)
			{

				if (!results.getRows().isEmpty())
				{

					result = results.getRows().get(0).get(0);

					try
					{
						Double numResult = Double.parseDouble(result);

						result = fmt(numResult);
					}
					catch (NumberFormatException e)
					{

					}
				}
				else
				{
					result = "0";
					Log.d(App.TAG, "empty result");

				}
			}
			else
			{
				result = "-1";
				Log.d(App.TAG, "null result");
				publishUpdate(null);
				return;
			}

			try
			{
				publishUpdate(new ExtensionData().visible(true).status(result).icon(R.drawable.ic_dashclock).expandedTitle(String.format(getString(R.string.title_display_format), getString(metricIdentifier), getString(periodIdentifier), result)).expandedBody(String.format(getString(R.string.body_display_format), profileName, selectedProperty)));
			}
			catch (Exception e)
			{

				Log.e(App.TAG, "Exception while published:" + e.getMessage());
			}

		}

	}

	private static String fmt(double d)
	{
		if (d == (int) d)
			return String.format("%d", (int) d);
		else
		{
			return new DecimalFormat("#.##").format(d);
		}
	}

}
