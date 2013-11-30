package in.co.madhur.dashclock;

import in.co.madhur.dashclock.API.AccountResult;
import in.co.madhur.dashclock.Consts.APIOperation;
import in.co.madhur.dashclock.Consts.API_STATUS;
import in.co.madhur.dashclock.dashadsense.AdsenseDataService.APIManagementTask;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public abstract class DataService extends Service
{
	
	protected static final int REQUEST_AUTHORIZATION = 2;
	protected IBinder binder = new LocalBinder();
	public BaseActivity extensionActivity;
	
	public void showAccountsAsync()
	{

		new APIManagementTask().execute(APIOperation.SELECT_ACCOUNT);
	}


	public void showToast(final String toast)
	{
		extensionActivity.runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();

			}
		});

	}
	
	public  class APIManagementTask extends
	AsyncTask<APIOperation, Integer, AccountResult>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			App.getEventBus().post(new AccountResult(API_STATUS.STARTING));

		}

		@Override
		protected void onPostExecute(AccountResult result)
		{
			super.onPostExecute(result);

			App.getEventBus().post(result);

		}
		

		@Override
		protected AccountResult doInBackground(APIOperation... params)
		{
			return null;
		}
		
		
		
	}

    @Override
    public IBinder onBind(Intent intent)
    {
            return binder;
    }

	public class LocalBinder extends Binder
	{
		DataService getService(BaseActivity extensionActivity)
		{
			DataService.this.extensionActivity = extensionActivity;
			return DataService.this;
		}

	}

}
