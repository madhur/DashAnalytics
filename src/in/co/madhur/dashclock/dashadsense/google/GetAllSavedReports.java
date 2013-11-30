package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.SavedReport;
import com.google.api.services.adsense.model.SavedReports;

public class GetAllSavedReports {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @param maxPageSize the maximum page size to retrieve.
	   * @return the last page of saved reports.
	   * @throws Exception
	   */
	  public static SavedReports run(AdSense adsense, int maxPageSize) throws Exception {
	    System.out.println("=================================================================");
	    System.out.printf("Listing all saved reports for default account\n");
	    System.out.println("=================================================================");

	    // Retrieve saved report list in pages and display the data as we receive it.
	    String pageToken = null;
	    SavedReports savedReports = null;
	    do {
	      savedReports = adsense.reports()
	          .saved()
	          .list()
	          .setMaxResults(maxPageSize)
	          .setPageToken(pageToken)
	          .execute();

	      if (savedReports.getItems() != null && !savedReports.getItems().isEmpty()) {
	        for (SavedReport savedReport : savedReports.getItems()) {
	          System.out.printf("Saved report with id \"%s\" and name \"%s\" was found.\n",
	              savedReport.getId(), savedReport.getName());
	        }
	      } else {
	        System.out.println("No saved reports found.");
	      }

	      pageToken = savedReports.getNextPageToken();
	    } while (pageToken != null);

	    System.out.println();
	    return savedReports;
	  }
	}