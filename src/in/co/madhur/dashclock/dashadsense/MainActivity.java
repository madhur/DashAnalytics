package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.BaseActivity;
import in.co.madhur.dashclock.MyBaseAdapter;
import in.co.madhur.dashclock.R;
import in.co.madhur.dashclock.API.GNewProfile;
import in.co.madhur.dashclock.API.GProfile;
import in.co.madhur.dashclock.AppPreferences.Keys;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSenseScopes;
import android.app.ActionBar.OnNavigationListener;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

}
