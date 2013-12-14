package in.co.madhur.dashclock.dashanalytics;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.DataService;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.AccountResult;
import in.co.madhur.dashclock.API.GAccount;
import in.co.madhur.dashclock.API.GNewProfile;
import in.co.madhur.dashclock.API.GProfile;
import in.co.madhur.dashclock.API.GProperty;
import in.co.madhur.dashclock.Consts.APIOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.crittercism.app.Crittercism;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Profiles;
import com.squareup.otto.Bus;
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.ListMultimap;

import android.util.Log;

public class AnalyticsDataService extends DataService
{
	public Analytics analytics_service;
	
	public void showAccountsAsync()
	{
		new APIManagementTask().execute(APIOperation.SELECT_ACCOUNT);
	}
	
	private class APIManagementTask extends
			DataService.APIManagementTask
	{

		@Override
		protected AccountResult doInBackground(APIOperation... params)
		{
			Accounts accounts;
			int account_num;
			Profiles profiles;
			com.google.api.services.analytics.model.Webproperties webproperties;
			String WebpropertyId;
			String Id, accountName, propertyName, profileName;

			List<GAccount> gAccounts = new ArrayList<GAccount>();

			boolean isApp;
			ArrayList<GNewProfile> acProfiles = new ArrayList<GNewProfile>();

			try
			{
				if (analytics_service == null)
				{

					Log.e(App.TAG, "Analytics service object is null");
					return null;
				}

				int size = analytics_service.management().accounts().list().size();
				if (size < 1)
				{
					getString(R.string.no_analytics_account);
				}

				try
				{
					accounts = analytics_service.management().accounts().list().execute();
				}
				catch (GoogleJsonResponseException e)
				{

					String message = e.getStatusMessage();
					Log.e(App.TAG, e.getMessage());

					try
					{
						JSONObject json = new JSONObject(new JSONTokener(e.getMessage().substring(e.getMessage().indexOf("{"))));
						JSONObject error = json.getJSONArray("errors").getJSONObject(0);
						message = error.getString("message");
					}
					catch (Exception ee)
					{

						Log.e(App.TAG, ee.getMessage());
					}

					return new AccountResult(message);
				}

				try
				{
					account_num = accounts.getItems().size();
				}
				catch (NullPointerException e)
				{
					// This exception occured for a user.
					//Log.e(App.TAG, e.getMessage());
					Crittercism.logHandledException(e);
					return new AccountResult("Could not retrieve accounts. Make sure you have Google Analytics account");

				}


				for (int i = 0; i < account_num; i++)
				{

					Id = accounts.getItems().get(i).getId();
					accountName = accounts.getItems().get(i).getName();

					if (App.LOCAL_LOGV)
					{
						Log.d(App.TAG, "account_id: " + Id);
						Log.d(App.TAG, "account_name: "
								+ accounts.getItems().get(i).getName());
					}

					gAccounts.add(new GAccount(Id, accounts.getItems().get(i).getName()));

					webproperties = analytics_service.management().webproperties().list(Id).execute();

					for (int j = 0; j < webproperties.getItems().size(); ++j)
					{

						WebpropertyId = webproperties.getItems().get(j).getId();
						propertyName = webproperties.getItems().get(j).getName();

						if (App.LOCAL_LOGV)
						{
							Log.d(App.TAG, "property_id: " + WebpropertyId);
							Log.d(App.TAG, "property_name: "
									+ webproperties.getItems().get(j).getName());
						}

						String kind = webproperties.getItems().get(j).getWebsiteUrl();
						if (kind == null)
							isApp = true;
						else
							isApp = false;

						GProperty gProperty = new GProperty(WebpropertyId, webproperties.getItems().get(j).getName());
						gAccounts.get(i).getProperties().add(gProperty);

						profiles = analytics_service.management().profiles().list(Id, WebpropertyId).execute();
						if (profiles != null && profiles.getItems() != null)
						{

							for (int k = 0; k < profiles.getItems().size(); ++k)
							{
								String Profile_Id = profiles.getItems().get(k).getId();
								profileName = profiles.getItems().get(k).getName();

								if (App.LOCAL_LOGV)
								{
									Log.d(App.TAG, "profile_id: " + Profile_Id);
									Log.d(App.TAG, "profile_name: "
											+ profiles.getItems().get(k).getName());
								}

								GProfile gProfile = new GProfile(Profile_Id, profiles.getItems().get(k).getName());

								gAccounts.get(i).getProperties().get(j).getProfiles().add(gProfile);

								acProfiles.add(new GNewProfile(Id, accountName, WebpropertyId, propertyName, Profile_Id, profileName, isApp));

							}
						}

					}

				}

			}
			catch (UserRecoverableAuthIOException e)
			{
				AnalyticsDataService.this.extensionActivity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
			}
			catch (IOException e)
			{
				Log.e(App.TAG, e.getMessage());
			}

			return new AccountResult(acProfiles, true);

		}

	}

	@Override
	public Bus getEventBus()
	{
		return App.getAnalyticsEventBus();
	}

	
}
