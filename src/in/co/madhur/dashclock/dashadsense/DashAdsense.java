package in.co.madhur.dashclock.dashadsense;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.AppPreferences;
import in.co.madhur.dashclock.Connection;
import in.co.madhur.dashclock.Consts.APIMetrics;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.APIResult;
import in.co.madhur.dashclock.AppPreferences.Keys;
import in.co.madhur.dashclock.Consts.API_STATUS;
import in.co.madhur.dashclock.dashanalytics.DashAnalytics;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSenseScopes;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse.Headers;

public class DashAdsense extends DashClockExtension
{
	AppPreferences appPreferences;
	String AccountId, metricKey, periodKey;
	private GoogleAccountCredential credential;
	private AdSense adsense_service;
	boolean isLocaltime, showCurrency;
	List<String> scopes = new ArrayList<String>();
	List<String> metrics=new ArrayList<String>();
	
	
	@Override
	protected void onUpdateData(int arg0)
	{
		// Check if user has changed the account, in that case, retrieve the new
		// credential object

		if (credential == null
				|| credential.getSelectedAccountName() == null
				|| !credential.getSelectedAccountName().equals(appPreferences.getUserName()))
		{
			Log.d(App.TAG_ADSENSE, "Account changed, retrieving new cred object");

			try
			{
				credential = GoogleAccountCredential.usingOAuth2(this, scopes);
				credential.setSelectedAccountName(appPreferences.getUserName());
				adsense_service = initializeAdsense(credential);
			}
			catch (Exception e)
			{

				Log.e(App.TAG_ADSENSE, "Exception in onInitialize"
						+ e.getMessage());
			}
		}

		AccountId = appPreferences.getMetadata(Keys.ACCOUNT_ID);
		periodKey = appPreferences.getMetadata(Keys.ADSENSE_PERIOD_ID);
		Log.d(App.TAG_ADSENSE, periodKey);
		isLocaltime = appPreferences.isLocalTime();
		showCurrency = appPreferences.isShowcurrency();
		
		
		metrics.clear();
	//	values.clear();
		
		metrics.add(APIMetrics.EARNINGS.toString());
//		values.put(APIMetrics.EARNINGS.toString(), null);
		
		if(appPreferences.getboolMetaData(Keys.SHOW_CLICKS))
		{
			metrics.add(APIMetrics.CLICKS.toString());
	//		values.put(APIMetrics.CLICKS.toString(), null);
		}
		if(appPreferences.getboolMetaData(Keys.SHOW_PAGE_VIEWS))
		{
			metrics.add(APIMetrics.PAGE_VIEWS.toString());
	//		values.put(APIMetrics.PAGE_VIEWS.toString(), null);
		}
		
		if(appPreferences.getboolMetaData(Keys.SHOW_PAGE_CTR))
		{
			metrics.add(APIMetrics.PAGE_VIEWS_CTR.toString());
	//		values.put(APIMetrics.PAGE_VIEWS_CTR.toString(), null);
		}
		if(appPreferences.getboolMetaData(Keys.SHOW_PAGE_RPM))
		{
			metrics.add(APIMetrics.PAGE_VIEWS_RPM.toString());
	//		values.put(APIMetrics.PAGE_VIEWS_RPM.toString(), null);	
		}
		if(appPreferences.getboolMetaData(Keys.SHOW_CPC))
		{
			metrics.add(APIMetrics.COST_PER_CLICK.toString());
	//		values.put(APIMetrics.COST_PER_CLICK.toString(), null);	
		}



		if (TextUtils.isEmpty(AccountId))
		{
			Log.d(App.TAG_ADSENSE, "Account not configured yet");
			return;
		}

		if (Connection.isConnected(this))
		{
			if (App.LOCAL_LOGV)
				Log.v(App.TAG_ADSENSE, "Firing update:" + String.valueOf(arg0));

			new APIResultTask().execute();
		}
		else
			Log.d(App.TAG_ADSENSE, "No network, postponing update");
	}

	@Override
	protected void onInitialize(boolean isReconnect)
	{
		super.onInitialize(isReconnect);
		appPreferences = new AdSensePreferences(this);
		scopes.add(AdSenseScopes.ADSENSE_READONLY);

	}

	private AdSense initializeAdsense(GoogleAccountCredential credential)
			throws Exception
	{
		AdSense adsense = new AdSense.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(getString(R.string.app_name)).build();

		return adsense;
	}

	private class APIResultTask extends AsyncTask<String, Integer, APIResult>
	{

		@Override
		protected APIResult doInBackground(String... params)
		{
			AdsenseReportsGenerateResponse result = null;
			try
			{

				result = GenerateReport.run(adsense_service, periodKey, isLocaltime , (ArrayList<String>) metrics);
			}
			catch (Exception e)
			{
				Log.e(App.TAG_ADSENSE, e.getMessage());
				return new APIResult(e.getMessage());
			}

			return new AdSenseAPIResult(result);

		}

		@Override
		protected void onPostExecute(APIResult resultAPI)
		{
			HashMap<String, String> values=new HashMap<String, String>();
//			StringBuilder sb = new StringBuilder();
			String currency = null;
			
			// Do not do anything if there is a failure, could be network
			// condition.
			if (resultAPI.getStatus() == API_STATUS.FAILURE)
				return;

			AdsenseReportsGenerateResponse response = ((AdSenseAPIResult) resultAPI).getResult();

			ArrayList<Headers> headers = (ArrayList<Headers>) response.getHeaders();
			
			if (response.getRows() != null && !response.getRows().isEmpty())
			{
				for (List<String> row : response.getRows())
				{
					if (App.LOCAL_LOGV)
						Log.v(App.TAG_ADSENSE, String.valueOf(row.size()));

					for(int i=0;i<headers.size();++i)
					{
						if(headers.get(i).getCurrency()!=null)
							currency=headers.get(i).getCurrency();
						
						values.put(headers.get(i).getName(), row.get(i));
					}
					
					// break after first iteration
					break;
//					response.getRows().get(0).get(location)
//					for (String column : row)
//					{
//						if (App.LOCAL_LOGV)
//							Log.v(App.TAG_ADSENSE, "adding value" + column);
//						sb.append(column);
//					}
				}

			}
			else
			{
				Log.d(App.TAG_ADSENSE, "No rows returned");
			}

			

//				for (Headers header : headers)
//				{
//					Log.d(App.TAG_ADSENSE, header.getName());
//					
//					
//					Log.d(App.TAG_ADSENSE, "Currency: " + currency);
//
//				}

//			if (TextUtils.isEmpty(sb.toString()))
//				return;

			int periodIdentifier = getResources().getIdentifier(periodKey, "string", DashAdsense.this.getPackageName());

			String expandedTitle, status;
			StringBuilder expandedBody=new StringBuilder();
			
			Set<String> heads=values.keySet();
			for(String header: heads)
			{
				if(header.equalsIgnoreCase(APIMetrics.EARNINGS.toString()))
					continue;
				
				int stringIdentifier = getResources().getIdentifier(header, "string", DashAdsense.this.getPackageName());
				String lineString=String.format(getString(R.string.adsense_attribute_display_format), getString(stringIdentifier), values.get(header));
				expandedBody.append(lineString);
				expandedBody.append("\n");
			}

			if (showCurrency && currency!=null)
			{
				status=String.format(getString(R.string.adsense_status_display_format_withcurrency),Currency.getInstance(currency).getSymbol(), values.get(APIMetrics.EARNINGS.toString()) );
				expandedTitle = String.format(getString(R.string.adsense_title_display_format_withcurrency), getString(periodIdentifier), Currency.getInstance(currency).getSymbol(), values.get(APIMetrics.EARNINGS.toString()));
			}
			else
			{
				status=values.get(APIMetrics.EARNINGS.toString());
				expandedTitle = String.format(getString(R.string.adsense_title_display_format), getString(periodIdentifier), values.get(APIMetrics.EARNINGS.toString()));
			}

			try
			{

				publishUpdate(new ExtensionData().visible(true).status(status).icon(R.drawable.ic_dashadsense).expandedTitle(expandedTitle).expandedBody(expandedBody.toString()));

			}
			catch (Exception e)
			{

				Log.e(App.TAG_ADSENSE, "Exception while published:"
						+ e.getMessage());
			}

		}

	}

}
