package in.co.madhur.dashclock.dashadsense;

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
	public static AdsenseReportsGenerateResponse run(AdSense adsense, String period) throws Exception
	{

		// Prepare report.
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.DATE, -7);
		Date oneWeekAgo = calendar.getTime();

		String startDate = DATE_FORMATTER.format(oneWeekAgo);
		String endDate = DATE_FORMATTER.format(today);
		Generate request = adsense.reports().generate(startDate, endDate);


		request.setMetric(Arrays.asList("EARNINGS"));

		// Run report.
		AdsenseReportsGenerateResponse response = request.execute();
		
		return response;

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
