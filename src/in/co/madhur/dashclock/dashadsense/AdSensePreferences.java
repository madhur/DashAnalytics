package in.co.madhur.dashclock.dashadsense;

import android.content.Context;
import android.preference.PreferenceManager;
import in.co.madhur.dashclock.AppPreferences;

public class AdSensePreferences extends AppPreferences
{
	
	public AdSensePreferences(Context context)
	{
		this.context = context;
		this.sharedPreferences = context.getSharedPreferences("AdSense", Context.MODE_PRIVATE);
	}


}
