package in.co.madhur.dashclock.dashanalytics;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.AppPreferences;
import in.co.madhur.dashclock.DisplayAttribute;
import in.co.madhur.dashclock.AppPreferences.ANALYTICS_KEYS;
import in.co.madhur.dashclock.Connection;
import in.co.madhur.dashclock.Consts.ANALYTICS_METRICS;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.APIResult;
import in.co.madhur.dashclock.AppPreferences.Keys;
import in.co.madhur.dashclock.Consts.ADSENSE_METRICS;
import in.co.madhur.dashclock.Consts.API_STATUS;
import in.co.madhur.dashclock.Consts.ATTRIBUTE_TYPE;
import in.co.madhur.dashclock.dashadsense.DashAdsense;
import android.os.AsyncTask;
import android.os.Debug;
import android.text.TextUtils;
import android.text.format.Time;
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
import com.google.api.services.analytics.model.GaData.ColumnHeaders;

public class DashAnalytics extends DashClockExtension
{
	AppPreferences appPreferences;
	String ProfileId, metricKey, periodKey;
	private GoogleAccountCredential credential;
	private Analytics analytics_service;

	List<String> scopes = new ArrayList<String>();

	List<String> metrics = new ArrayList<String>();
	
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
		
		metrics.clear();
		metrics.add(metricKey);
		for(ANALYTICS_KEYS key : ANALYTICS_KEYS.values())
		{
			if(appPreferences.getAnalyticProperty(key))
			{
				metrics.add(key.getMetric());
				
			}
			
		}

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
			return GenerateReport.run(analytics_service, ProfileId, periodKey, (ArrayList<String>) metrics);
		}

		@Override
		protected void onPostExecute(APIResult resultAPI)
		{
			HashMap<String, DisplayAttribute> values = new HashMap<String, DisplayAttribute>();
			// Do not do anything if there is a failure, could be network
			// condition.
			if (resultAPI.getStatus() == API_STATUS.FAILURE)
				return;

			GaData results = ((AnalyticsAPIResult) resultAPI).getResult();

			String profileName = appPreferences.getMetadata(Keys.PROFILE_NAME);
			String selectedProperty = appPreferences.getMetadata(Keys.PROPERTY_NAME);
//			String metricKey = appPreferences.getMetadata(Keys.METRIC_ID);
			Log.d(App.TAG, "metricL "+metricKey);
			int metricIdentifier = getResources().getIdentifier(metricKey, "string", DashAnalytics.this.getPackageName());
			int periodIdentifier = getResources().getIdentifier(periodKey, "string", DashAnalytics.this.getPackageName());
			boolean showProfile=appPreferences.getboolMetaData(Keys.SHOW_PROFILE);
			boolean showLastUpdate=appPreferences.getboolMetaData(Keys.SHOW_ANALYTICS_LASTUPDATE);
			
			String result;

			if (App.LOCAL_LOGV)
				Log.v(App.TAG, "Processing result for " + profileName);
			
			List<ColumnHeaders> columnHeaders=results.getColumnHeaders();

			if (results != null)
			{
				if (results.getRows() != null)
				{
					if (!results.getRows().isEmpty())
					{
						
						for (List<String> row : results.getRows())
						{

							for (int i = 0; i < columnHeaders.size(); ++i)
							{
									values.put(columnHeaders.get(i).getName().replace(':', '_'), new DisplayAttribute(row.get(i), columnHeaders.get(i).getDataType()));
							}

							// break after first iteration
							break;

						}
						

//						result = results.getRows().get(0).get(0);
//
//						try
//						{
//							Double numResult = Double.parseDouble(result);
//
//							result = fmt(numResult);
//						}
//						catch (NumberFormatException e)
//						{
//							Log.e(App.TAG, e.getMessage());
//						}
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
			
			StringBuilder expandedBody = new StringBuilder();
			String status=values.get(metricKey).toString();
			Log.d(App.TAG, String.valueOf(metricIdentifier));
			
			String expandedTitle = "";
			
			if(metricIdentifier!=0)
				expandedTitle=String.format(getString(R.string.title_display_format), getString(metricIdentifier), getString(periodIdentifier), status);
			else
				Log.d(App.TAG, "could not match resources:" + metricKey);
			
			Set<String> heads = values.keySet();
			for (String header : heads)
			{
				String lineString = null;

				if (header.equalsIgnoreCase(ANALYTICS_METRICS.getByMetric(metricKey).toString()))
					continue;
				
				

				int stringIdentifier = getResources().getIdentifier(header, "string", DashAnalytics.this.getPackageName());

				if(stringIdentifier!=0)
				{
				lineString = String.format(getString(R.string.adsense_attribute_display_format), getString(stringIdentifier), values.get(header));
				}

				expandedBody.append(lineString);
				expandedBody.append("\n");
			}
			
			if(showProfile)
			{
				expandedBody.append(String.format(getString(R.string.body_display_format), profileName, selectedProperty));
				expandedBody.append("\n");
			}
			if(showLastUpdate)
			{
				Time t=new Time();
				t.setToNow();
				expandedBody.append(String.format(getString(R.string.lastupdate_display_format),t.hour, t.minute));
				expandedBody.append("\n");
				
			}

			try
			{
				publishUpdate(new ExtensionData().visible(true).status(status).icon(R.drawable.ic_dashclock).expandedTitle(expandedTitle).expandedBody(expandedBody.toString()));
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
