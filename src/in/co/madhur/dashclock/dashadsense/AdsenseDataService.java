package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.DataService;
import in.co.madhur.dashclock.API.AccountResult;
import in.co.madhur.dashclock.API.GNewProfile;
import in.co.madhur.dashclock.Consts.APIOperation;
import in.co.madhur.dashclock.Consts.API_STATUS;
import in.co.madhur.dashclock.dashadsense.google.GenerateReport1;
import in.co.madhur.dashclock.dashadsense.google.GetAllAccounts;
import java.util.ArrayList;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.Account;
import com.google.api.services.adsense.model.Accounts;
import android.util.Log;

public class AdsenseDataService extends DataService
{
	public AdSense adsense_service;

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

				Log.e(App.TAG, "Analytics service object isn null");
				return null;
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

				GenerateReport1.run(adsense, null);

			}
			catch (UserRecoverableAuthIOException e)
			{
				AdsenseDataService.this.extensionActivity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return new AccountResult(acProfiles, true);

		}

	}

}
