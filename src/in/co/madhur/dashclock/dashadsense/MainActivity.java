package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.BaseActivity;
import in.co.madhur.dashclock.MyBaseAdapter;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.AccountResult;
import in.co.madhur.dashclock.API.GNewProfile;
import in.co.madhur.dashclock.API.GProfile;
import in.co.madhur.dashclock.AppPreferences.Keys;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSenseScopes;
import com.squareup.otto.Subscribe;

import android.app.ActionBar.OnNavigationListener;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity

{
	private AdSense adsense_service;
	OnNavigationListener listNavigator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		appPreferences=new AdSensePreferences(this);

		List<String> scopes = new ArrayList<String>();
		scopes.add(AdSenseScopes.ADSENSE_READONLY);

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
		
		App.getAdSenseEventBus().register(this);
		
	}

	
	protected void PersistPreferences(GNewProfile newProfile, String accountEmail)
	{
		if (newProfile != null)
		{
			appPreferences.setMetadataMultiple(newProfile.getAccountId(), newProfile.getAccountName(), accountEmail);
		}

	}

	@Override
	protected void UpdateSelectionPreferences()
	{
		
		String accountId = appPreferences.getMetadata(Keys.ACCOUNT_ID);

		if (!TextUtils.isEmpty(accountId))
		{
			int position = GProfile.getItemPositionByAccountId(acProfiles, accountId);
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


	@Override
	protected MyBaseAdapter getListAdater(ArrayList<GNewProfile> acProfiles, Context baseActivity)
	{
		return new MyAdapter(acProfiles, baseActivity);
	}


	@Override
	protected Intent getPreferenceIntent()
	{
		Intent i=new Intent();
		i.setClass(this, DashAdSensePreferenceActivity.class);
		return i;
	}
	
	@Subscribe
	public void UpdateUI(AccountResult result)
	{
		ProgressBar progressbar = (ProgressBar) findViewById(R.id.pbHeaderProgress);
		LinearLayout spinnerLayout = (LinearLayout) findViewById(R.id.spinnerslayout);
		TextView statusMessage = (TextView) findViewById(R.id.statusMessage);

		switch (result.getStatus())
		{
			case STARTING:
				statusMessage.setVisibility(View.GONE);
				progressbar.setVisibility(View.VISIBLE);
				spinnerLayout.setVisibility(View.GONE);

				break;

			case FAILURE:
				statusMessage.setVisibility(View.VISIBLE);
				progressbar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				statusMessage.setText(result.getErrorMessage());

				break;

			case SUCCESS:

				statusMessage.setVisibility(View.GONE);
				progressbar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.VISIBLE);

				if (result.getItems() != null)
				{
					this.acProfiles = result.getItems();

					MyBaseAdapter myAdapter = getListAdater(acProfiles, this);
					listView.setAdapter(myAdapter);

					UpdateSelectionPreferences();

					if (result.isPersist() && acProfiles.size() > 0)
					{
						if (App.LOCAL_LOGV)
							Log.v(App.TAG_BASE, "saving configdata");

						try
						{
							appPreferences.saveConfigData(acProfiles, credential.getSelectedAccountName());
						}
						catch (JsonProcessingException e)
						{
							Log.e(App.TAG, e.getMessage());
						}
					}

				}

				break;
		}

	}

}
