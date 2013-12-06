package in.co.madhur.dashclock;

import com.crittercism.app.Crittercism;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.app.Application;

public class App extends Application
{
	private static Bus busAnalytics, busAdSense;
	public static String TAG="DashAnalytics";
	public static String TAG_ADSENSE="DashAdSense";
	public static String TAG_BASE="DashAnalyticsBase";
	
	public static final boolean DEBUG = true;
	public static final boolean LOCAL_LOGV = DEBUG;

	@Override
	public void onCreate()
	{
		super.onCreate();

		busAnalytics = new Bus(ThreadEnforcer.ANY);
		busAdSense = new Bus(ThreadEnforcer.ANY);
		
		Crittercism.initialize(getApplicationContext(), "527b160b8b2e3376d3000003");
	}

	public static Bus getAnalyticsEventBus()
	{
		return busAnalytics;
	}
	
	public static Bus getAdSenseEventBus()
	{
		return busAdSense;
	}

}
