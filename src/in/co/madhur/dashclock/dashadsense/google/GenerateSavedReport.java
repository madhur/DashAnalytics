package in.co.madhur.dashclock.dashadsense.google;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSense.Reports.Generate;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;

public class GenerateSavedReport {

	  static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @param savedReportId the saved report ID on which to run the report.
	   * @throws Exception
	   */
	  public static void run(AdSense adsense, String savedReportId) throws Exception {
	    System.out.println("=================================================================");
	    System.out.printf("Running saved report %s\n", savedReportId);
	    System.out.println("=================================================================");

	    // Prepare report.
	    Date today = new Date();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(today);
	    calendar.add(Calendar.DATE, -7);
	    com.google.api.services.adsense.AdSense.Reports.Saved.Generate request = adsense.reports().saved().generate(savedReportId);

	    // Run saved report.
	    AdsenseReportsGenerateResponse response = request.execute();

	    if (response.getRows() != null && !response.getRows().isEmpty()) {
	      // Display headers.
	      for (AdsenseReportsGenerateResponse.Headers header : response.getHeaders()) {
	        System.out.printf("%25s", header.getName());
	      }
	      System.out.println();

	      // Display results.
	      for (List<String> row : response.getRows()) {
	        for (String column : row) {
	          System.out.printf("%25s", column);
	        }
	        System.out.println();
	      }

	      System.out.println();
	    } else {
	      System.out.println("No rows returned.");
	    }

	    System.out.println();
	  }

	  /**
	   * Escape special characters for a parameter being used in a filter.new.
	   *
	   * @param parameter the parameter to be escaped.
	   * @return the escaped parameter.
	   */
	  public static String escapeFilterParameter(String parameter) {
	    return parameter.replace("\\", "\\\\").replace(",", "\\,");
	  }
	}
