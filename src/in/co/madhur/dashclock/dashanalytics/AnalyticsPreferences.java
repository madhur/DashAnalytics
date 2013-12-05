package in.co.madhur.dashclock.dashanalytics;

import android.content.Context;
import android.preference.PreferenceManager;
import in.co.madhur.dashclock.AppPreferences;
import in.co.madhur.dashclock.Consts;

public class AnalyticsPreferences extends AppPreferences
{

	public AnalyticsPreferences(Context context)
	{
		this.context = context;
//		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.sharedPreferences = context.getSharedPreferences(Consts.ANALYTICS_PREFERENCE_NAME, Context.MODE_PRIVATE);
		
	}
	

}
