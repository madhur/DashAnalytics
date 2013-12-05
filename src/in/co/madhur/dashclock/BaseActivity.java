package in.co.madhur.dashclock;

import in.co.madhur.dashclock.API.AccountResult;
import in.co.madhur.dashclock.API.GNewProfile;
import in.co.madhur.dashclock.DataService.LocalBinder;
import in.co.madhur.dashclock.dashadsense.AdsenseDataService;
import in.co.madhur.dashclock.dashanalytics.AnalyticsDataService;
import java.io.IOException;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.squareup.otto.Subscribe;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.OnNavigationListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public abstract class BaseActivity extends Activity
{
	protected AppPreferences appPreferences;
	protected ListView listView;
	protected static final int REQUEST_ACCOUNT_PICKER = 1;
	protected GoogleAccountCredential credential;
	protected DataService mService;
	protected boolean mBound = false;
	protected static final int REQUEST_AUTHORIZATION = 2;
	protected ArrayList<GNewProfile> acProfiles;

	protected ServiceConnection mConnection = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService(BaseActivity.this);
			setServiceObject();
			mBound = true;

			InitAccount();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			mBound = false;
		}
	};

	protected abstract void setServiceObject();

	protected void InitAccount()
	{

		int selectedIndex = getActionBar().getSelectedNavigationIndex();
		
		String selectedAccount = getAccountsList().get(selectedIndex);
		
		try
		{
			acProfiles = (ArrayList<GNewProfile>) appPreferences.getConfigData(selectedAccount);

		}
		catch (JsonParseException e)
		{
			Log.e(App.TAG, e.getMessage());
		}
		catch (JsonMappingException e)
		{
			Log.e(App.TAG, e.getMessage());
		}
		catch (IOException e)
		{
			Log.e(App.TAG, e.getMessage());
		}

		if (acProfiles == null)
		{
			if (App.LOCAL_LOGV)
				Log.v(App.TAG_BASE, "Initing accounts from net for "
						+ selectedAccount);
			
			if (Connection.isConnected(this))
			{
				mService.showAccountsAsync();
			}
			else
			{
				UpdateUI(new AccountResult( getString(R.string.network_not_connected)));
			}
		}
		else
		{
			if (App.LOCAL_LOGV)
				Log.v(App.TAG_BASE, "Initing accounts from cache for "
						+ selectedAccount);

			UpdateUI(new AccountResult(acProfiles, false));
			UpdateSelectionPreferences();
		}

	}

	protected abstract void UpdateUI(AccountResult accountResult);
	
	protected abstract void UpdateSelectionPreferences();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main2);

		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (status != ConnectionResult.SUCCESS)
		{
			Log.e(App.TAG_BASE, String.valueOf(status));
			Toast.makeText(this, getString(R.string.gps_missing), Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listview);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				MyBaseAdapter myAdapter = (MyBaseAdapter) listView.getAdapter();
				GNewProfile newProfile = (GNewProfile) myAdapter.getItem(position);

				if (newProfile != null)
				{
					PersistPreferences(newProfile, credential.getSelectedAccountName());
				}

			}
		});

//		App.getEventBus().register(UpdateUI);

	}

	protected abstract void PersistPreferences(GNewProfile newProfile, String selectedAccountName);

	protected void startAddGoogleAccountIntent()
	{
		Intent addAccountIntent = new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		addAccountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[] { "com.google" });
		startActivity(addAccountIntent);
	}

	protected ArrayList<String> getAccountsList()
	{
		ArrayList<String> accountList = new ArrayList<String>();

		AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		Account[] list = manager.getAccounts();

		for (Account account : list)
		{
			if (account.type.equalsIgnoreCase("com.google"))
			{
				accountList.add(account.name);
			}
		}

		return accountList;
	}

	protected void setNavigationList(String accountName)
	{
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setDisplayShowTitleEnabled(false);

		ArrayList<String> navItems = getAccountsList();
		navItems.add("Add Account");

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActionBar().getThemedContext(), R.layout.spinner_item, navItems);
		adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
		
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
						Log.v(App.TAG_BASE, "Fetching accounts for:"
								+ getAccountsList().get(itemPosition));

					credential.setSelectedAccountName(getAccountsList().get(itemPosition));

					setService();

					if (mBound)
						unbindService(mConnection);

					getAccounts();
				}

				return true;
			}
		});

		if (accountName != null)
		{
			int index = navItems.indexOf(accountName);
			if (index != -1)
				getActionBar().setSelectedNavigationItem(index);
			else
				Log.e(App.TAG_BASE, "acount not found");
		}

	}

	protected abstract Object getService(GoogleAccountCredential credential2);

	protected abstract MyBaseAdapter getListAdater(ArrayList<GNewProfile> acProfiles, Context baseActivity);

	protected abstract void setService();

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		switch (requestCode)
		{
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == RESULT_OK && data != null
						&& data.getExtras() != null)
				{
					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null)
					{
						setNavigationList(accountName);

						credential.setSelectedAccountName(accountName);
						// analytics_service = getService(credential);

						setService();

						getAccounts();
					}
				}
				else if (resultCode == RESULT_CANCELED)
				{

					setNavigationList(null);
				}

				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK)
				{
					getAccounts();
				}
				else
				{
					startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
				}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
			    finish();
			    break;
			    
			case R.id.action_refresh:
				if (!mBound || mService == null)
				{
					Toast.makeText(this, getString(R.string.gps_missing), Toast.LENGTH_LONG).show();

					return true;

				}

				if (Connection.isConnected(this))
				{
					mService.showAccountsAsync();
				}
				else
					Toast.makeText(this, getString(R.string.network_not_connected), Toast.LENGTH_SHORT).show();

				break;

			case R.id.action_settings:
				startActivity(getPreferenceIntent());
				break;

			default:
				return super.onOptionsItemSelected(item);

		}

		return true;
	}
	
	protected abstract Intent getPreferenceIntent();

	@Override
	protected void onStart()
	{
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, (android.view.Menu) menu);
		return super.onCreateOptionsMenu(menu);
	}

//	@Subscribe
//	public void Notify(Intent reason)
//	{
//
//		startActivityForResult(reason, REQUEST_AUTHORIZATION);
//	}

	protected void getAccounts()
	{
		Intent i = new Intent();
		
		if (this instanceof in.co.madhur.dashclock.dashanalytics.MainActivity)
			i.setClass(this, AnalyticsDataService.class);
		else if (this instanceof in.co.madhur.dashclock.dashadsense.MainActivity)
			i.setClass(this, AdsenseDataService.class);
		
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (mBound)
		{
			unbindService(mConnection);
			mBound = false;
		}
	}

}
