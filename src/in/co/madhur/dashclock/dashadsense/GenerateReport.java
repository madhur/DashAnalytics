package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.App;
import in.co.madhur.dashclock.API.APIResult;
import in.co.madhur.dashclock.Consts.APIPeriod;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.util.Log;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSense.Reports.Generate;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;

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
	 * @throws IOException
	 * @throws Exception
	 */
	public static APIResult run(AdSense adsense, String period, boolean isLocalTime, ArrayList<String> metrics) throws IOException
		
	{

		// Prepare report.
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		String startDate = null, endDate = null;

		if (period.equalsIgnoreCase(APIPeriod.TODAY.toString()))
		{
			// startDate = DATE_FORMATTER.format(today);
			startDate = "today";
			endDate = startDate;
		}
		else if (period.equalsIgnoreCase(APIPeriod.YESTERDAY.toString()))
		{
			calendar.add(Calendar.DATE, -1);

			startDate = DATE_FORMATTER.format(calendar.getTime());
			endDate = startDate;

		}
		else if (period.equalsIgnoreCase(APIPeriod.THISMONTH.toString()))
		{
			startDate = "startOfMonth";
			endDate = "today";

		}
		else if (period.equalsIgnoreCase(APIPeriod.LASTMONTH.toString()))
		{
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DATE, 1);
			startDate = DATE_FORMATTER.format(calendar.getTime());

			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DATE, -1);

			endDate = DATE_FORMATTER.format(calendar.getTime());

		}

		if (App.LOCAL_LOGV)
		{
			Log.d(App.TAG_ADSENSE, "Start Date: " + startDate);
			Log.d(App.TAG_ADSENSE, "End Date: " + endDate);
		}

		Generate request = adsense.reports().generate(startDate, endDate);

		if (isLocalTime)
			request.setUseTimezoneReporting(true);
		else
			request.setUseTimezoneReporting(false);

		request.setMetric(metrics);

		// Run report.
		try
		{
			AdsenseReportsGenerateResponse response = request.execute();
			return new AdSenseAPIResult(response);
		}
		catch (UnknownHostException e)
		{
			Log.e(App.TAG_ADSENSE, "Exception unknownhost in doInBackground"
					+ e.getMessage());
			return new APIResult(e.getMessage());
		}
		catch (Exception e)
		{
			Log.e(App.TAG_ADSENSE, "Exception in doInBackground"
					+ e.getMessage());
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
	public static String escapeFilterParameter(String parameter)
	{
		return parameter.replace("\\", "\\\\").replace(",", "\\,");
	}
}
