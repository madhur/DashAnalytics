package in.co.madhur.dashclock.dashadsense;

import in.co.madhur.dashclock.Consts.APIPeriod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
	public static AdsenseReportsGenerateResponse run(AdSense adsense, String period, String isLocal) throws Exception
	{

		// Prepare report.
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		String startDate = null, endDate = null;
		boolean isLocalTime=Boolean.parseBoolean(isLocal);
		
		if(period==APIPeriod.TODAY.toString())
		{
			// startDate = DATE_FORMATTER.format(today);
			startDate="today";
			endDate=startDate;
		}
		else if(period == APIPeriod.YESTERDAY.toString())
		{
			calendar.add(Calendar.DATE, -1);
			
			startDate=DATE_FORMATTER.format(calendar.getTime());
			endDate=startDate;
			
		}
		else if(period==APIPeriod.THISMONTH.toString())
		{
			startDate="startOfMonth";
			endDate="today";
			
		}
		else if(period==APIPeriod.LASTMONTH.toString())
		{
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DATE, 1);
			startDate=DATE_FORMATTER.format(calendar.getTime());
			
			calendar.add(Calendar.MONTH, 2);  
	        calendar.set(Calendar.DAY_OF_MONTH, 1);  
	        calendar.add(Calendar.DATE, -1);
	        
	        endDate=DATE_FORMATTER.format(calendar.getTime());
			
		}
		
//		calendar.setTime(today);
//		calendar.add(Calendar.DATE, -7);
//		Date oneWeekAgo = calendar.getTime();
//
//		String startDate = DATE_FORMATTER.format(oneWeekAgo);
//		String endDate = DATE_FORMATTER.format(today);
		Generate request = adsense.reports().generate(startDate, endDate);

		if(isLocalTime)
			request.setUseTimezoneReporting(true);
		else
			request.setUseTimezoneReporting(false);
		
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
