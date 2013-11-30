package in.co.madhur.dashclock.dashanalytics;

import android.content.Context;
import android.preference.PreferenceManager;
import in.co.madhur.dashclock.AppPreferences;

public class AnalyticsPreferences extends AppPreferences
{

	public AnalyticsPreferences(Context context)
	{
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
	}

}
