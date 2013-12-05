package in.co.madhur.dashclock.dashanalytics;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.Consts.APIPeriod;
import in.co.madhur.dashclock.API.APIResult;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.util.Log;

import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;

public class GenerateReport
{

	static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Runs this sample.
	 * 
	 * @param adsense
	 *            AdSense service object on which to run the requests.
	 * @param adClientId
	 *            the ad client ID on which to run the report.
	 * @throws Exception
	 */
	public static APIResult run(Analytics analytics, String ProfileId, String periodKey, ArrayList<String> metrics)
	{

		Calendar calendar = Calendar.getInstance();
		String startDate = null, endDate = null;

		if (periodKey.equalsIgnoreCase(APIPeriod.TODAY.toString()))
		{
			// startDate = DATE_FORMATTER.format(today);
			startDate = "today";
			endDate = startDate;
		}
		else if (periodKey.equalsIgnoreCase(APIPeriod.YESTERDAY.toString()))
		{
			calendar.add(Calendar.DATE, -1);

			startDate = DATE_FORMATTER.format(calendar.getTime());
			endDate = startDate;

		}
		else if (periodKey.equalsIgnoreCase(APIPeriod.LASTWEEK.toString()))
		{
			startDate = "startOfMonth";
			endDate = "today";

		}
		else if (periodKey.equalsIgnoreCase(APIPeriod.LAST30DAYS.toString()))
		{
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DATE, 1);
			startDate = DATE_FORMATTER.format(calendar.getTime());

			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DATE, -1);

			endDate = DATE_FORMATTER.format(calendar.getTime());

		}

		StringBuilder metricsBuilder = new StringBuilder();

		for (String metric : metrics)
		{
			metricsBuilder.append(metric);
			metricsBuilder.append(',');
		}

		if (App.LOCAL_LOGV)
		{
			Log.d(App.TAG_ADSENSE, "Start Date: " + startDate);
			Log.d(App.TAG_ADSENSE, "End Date: " + endDate);
		}

		try
		{
			Log.d(App.TAG, metricsBuilder.substring(0, metricsBuilder.length() - 1));
			Get apiQuery = analytics.data().ga().get("ga:" + ProfileId, startDate, endDate, metricsBuilder.substring(0, metricsBuilder.length() - 1).replace('_', ':'));
			Log.d(App.TAG, apiQuery.toString());

			return new AnalyticsAPIResult(apiQuery.execute());
		}
		catch (UnknownHostException e)
		{
			Log.e(App.TAG, "Exception unknownhost in doInBackground"
					+ e.getMessage());
			return new APIResult(e.getMessage());
		}
		catch (Exception e)
		{
			Log.e(App.TAG, "Exception in doInBackground" + e.getMessage());
			return new APIResult(e.getMessage());
		}

	}

	/**
	 * Escape special characters for a parameter being used in a filter.
	 * 
	 * @param parameter
	 *            the parameter to be escaped.
	 * @return the escaped parameter.
	 */
//	public static String escapeFilterParameter(String parameter)
//	{
//		return parameter.replace("\\", "\\\\").replace(",", "\\,");
//	}
}
