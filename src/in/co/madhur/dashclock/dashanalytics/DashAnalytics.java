package in.co.madhur.dashclock.dashanalytics;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.GaData;

public class DashAnalytics extends DashClockExtension
{
	AppPreferences appPreferences;
	String ProfileId, metricKey, periodKey;
	private GoogleAccountCredential credential;
	private Analytics analytics_service;

	List<String> scopes = new ArrayList<String>();

	@Override
	protected void onUpdateData(int arg0)
	{
		// Check if user has changed the account, in that case, retrieve the new
		// credential object

		if (credential.getSelectedAccountName() == null
				|| !credential.getSelectedAccountName().equals(appPreferences.getUserName()))
		{
			Log.d(App.TAG, "Account changed, retrieving new cred object");

			InitAuth();
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

			new APIResultTask().execute();
		}
		else
			Log.d(App.TAG, "No network, postponing update");
	}

	@Override
	protected void onInitialize(boolean isReconnect)
	{
		super.onInitialize(isReconnect);
		appPreferences = new AnalyticsPreferences(this);

		scopes.add(AnalyticsScopes.ANALYTICS_READONLY);
		
		InitAuth();

	}
	
	private void InitAuth()
	{
		try
		{
			credential = GoogleAccountCredential.usingOAuth2(this, scopes);
			credential.setSelectedAccountName(appPreferences.getUserName());
			analytics_service = getAnalyticsService(credential);
		}
		catch (Exception e)
		{

			Log.e(App.TAG, "Exception in onInitialize" + e.getMessage());
		}
		
	}

	private Analytics getAnalyticsService(GoogleAccountCredential credential)
	{
		return new Analytics.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(getString(R.string.app_name)).build();

	}

	private class APIResultTask extends AsyncTask<String, Integer, APIResult>
	{

		@Override
		protected APIResult doInBackground(String... params)
		{
			try
			{
				Get apiQuery = analytics_service.data().ga().get("ga:"
						+ ProfileId, periodKey, periodKey, "ga:" + metricKey);
				Log.d(App.TAG, apiQuery.toString());

				return new AnalyticsAPIResult(apiQuery.execute());
			}
			catch (UnknownHostException e)
			{
				Log.e(App.TAG, "Exception unknownhost in doInBackground"
						+ e.getMessage());
				return new APIResult(e.getMessage());
			}
			catch (Exception e)
			{
				Log.e(App.TAG, "Exception in doInBackground" + e.getMessage());
				return new APIResult(e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(APIResult resultAPI)
		{
			// Do not do anything if there is a failure, could be network
			// condition.
			if (resultAPI.getStatus() == API_STATUS.FAILURE)
				return;

			GaData results = ((AnalyticsAPIResult) resultAPI).getResult();

			String profileName = appPreferences.getMetadata(Keys.PROFILE_NAME);
			String selectedProperty = appPreferences.getMetadata(Keys.PROPERTY_NAME);
			String metricKey = appPreferences.getMetadata(Keys.METRIC_ID);
			int metricIdentifier = getResources().getIdentifier(metricKey, "string", DashAnalytics.this.getPackageName());
			int periodIdentifier = getResources().getIdentifier(periodKey, "string", DashAnalytics.this.getPackageName());
			String result;

			if (App.LOCAL_LOGV)
				Log.v(App.TAG, "Processing result for " + profileName);

			if (results != null)
			{
				if (results.getRows() != null)
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
							Log.e(App.TAG, e.getMessage());
						}
					}
					else
					{
						result = "0";
						Log.d(App.TAG, "empty result");

						// TODO: Check if its ok to publish zero metric
						
					}
				}
				else
				{
					result = "0";
					Log.d(App.TAG, "null rows");

					// TODO: Check if its ok to publish zero metric

				}
			}
			else
			{
				result = "-1";
				Log.d(App.TAG, "null result");
				// TODO: Check if its ok to publish zero metric
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
