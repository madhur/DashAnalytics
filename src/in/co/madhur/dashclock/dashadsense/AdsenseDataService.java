package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.DataService;
import in.co.madhur.dashclock.API.AccountResult;
import in.co.madhur.dashclock.API.GNewProfile;
import in.co.madhur.dashclock.Consts.APIOperation;
import in.co.madhur.dashclock.Consts.API_STATUS;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.Account;
import com.google.api.services.adsense.model.Accounts;
import android.util.Log;

public class AdsenseDataService extends DataService
{
	public AdSense adsense_service;
	
	public void showAccountsAsync()
	{

		new APIManagementTask().execute(APIOperation.SELECT_ACCOUNT);
	}

	public class APIManagementTask extends DataService.APIManagementTask
	{

		@Override
		protected AccountResult doInBackground(APIOperation... params)
		{
			Accounts accounts = null;
			final int MAX_LIST_PAGE_SIZE = 50;
			AdSense adsense = adsense_service;
			ArrayList<GNewProfile> acProfiles = new ArrayList<GNewProfile>();

			if (adsense_service == null)
			{

				Log.e(App.TAG, "Adsense service object isn null");
				return new AccountResult(API_STATUS.FAILURE);
			}

			String pageToken = null;

			try
			{
				do
				{

					accounts = GetAllAccounts.run(adsense, MAX_LIST_PAGE_SIZE);

					if (accounts == null)
						return new AccountResult(API_STATUS.FAILURE);

					if (accounts.getItems() != null
							&& !accounts.getItems().isEmpty())
					{
						for (Account account : accounts.getItems())
						{
							if(App.LOCAL_LOGV)
							Log.d(App.TAG, "Account with ID \"%s\" and name \"%s\" was found.\n"
									+ account.getId() + account.getName());

							acProfiles.add(new GNewProfile(account.getId(), account.getName()));
						}
					}
					else
					{
					}

					pageToken = accounts.getNextPageToken();
				}
				while (pageToken != null);

			}
			catch (GoogleJsonResponseException e)
			{

				String message = e.getStatusMessage();
				Log.e(App.TAG, e.getMessage());

				try
				{
					Log.v(App.TAG, e.getMessage().substring(e.getMessage().indexOf("{")));
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

			catch (UserRecoverableAuthIOException e)
			{
				AdsenseDataService.this.extensionActivity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);

			}
			catch (Exception e)
			{
				Log.e(App.TAG, e.getMessage());
			}

			return new AccountResult(acProfiles, true);

		}

	}

}
