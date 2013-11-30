package in.co.madhur.dashclock.dashadsense.google;

import in.co.madhur.dashclock.App;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSense.Reports.Generate;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;

public class GenerateReport1
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
	public static String run(AdSense adsense, String adClientId) throws Exception
	{

		StringBuilder sb=new StringBuilder();
		
		// Prepare report.
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.DATE, -7);
		Date oneWeekAgo = calendar.getTime();

		String startDate = DATE_FORMATTER.format(oneWeekAgo);
		String endDate = DATE_FORMATTER.format(today);
		Generate request = adsense.reports().generate(startDate, endDate);

		// Specify the desired ad client using a filter.
		// request.setFilter(Arrays.asList("AD_CLIENT_ID==" +
		// escapeFilterParameter(adClientId)));

		request.setMetric(Arrays.asList("EARNINGS", "CLICKS"));
		// request.setDimension(Arrays.asList("DATE"));

		// Sort by ascending date.
		// request.setSort(Arrays.asList("+DATE"));

		// Run report.
		AdsenseReportsGenerateResponse response = request.execute();

		if (response.getRows() != null && !response.getRows().isEmpty())
		{
			for (List<String> row : response.getRows())
			{
				Log.v(App.TAG, String.valueOf(row.size()));
				for (String column : row)
				{
					Log.v(App.TAG, "adding value" + column);
					sb.append(column);
				}
			}

		}
		else
		{
			Log.d(App.TAG,"No rows returned");
		}
		
		return sb.toString();

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
