package in.co.madhur.dashclock.dashadsense;

import android.content.Context;
import in.co.madhur.dashclock.AppPreferences;
import in.co.madhur.dashclock.Consts;

public class AdSensePreferences extends AppPreferences
{
	
	public AdSensePreferences(Context context)
	{
		this.context = context;
		this.sharedPreferences = context.getSharedPreferences(Consts.ADSENSE_PREFERENCE_NAME, Context.MODE_PRIVATE);
	}


}
