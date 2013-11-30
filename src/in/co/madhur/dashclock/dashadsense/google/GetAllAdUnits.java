package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.AdUnit;
import com.google.api.services.adsense.model.AdUnits;

public class GetAllAdUnits {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @param adClientId the ID for the ad client to be used.
	   * @param maxPageSize the maximum page size to retrieve.
	   * @return the last page of ad units.
	   * @throws Exception
	   */
	  public static AdUnits run(AdSense adsense, String adClientId, int maxPageSize) throws Exception {
	    System.out.println("=================================================================");
	    System.out.printf("Listing all ad units for ad client %s\n", adClientId);
	    System.out.println("=================================================================");

	    // Retrieve ad unit list in pages and display data as we receive it.
	    String pageToken = null;
	    AdUnits adUnits = null;
	    do {
	      adUnits = adsense.adunits().list(adClientId)
	          .setMaxResults(maxPageSize)
	          .setPageToken(pageToken)
	          .execute();

	      if (adUnits.getItems() != null && !adUnits.getItems().isEmpty()) {
	        for (AdUnit unit : adUnits.getItems()) {
	          System.out.printf("Ad unit with code \"%s\", name \"%s\" and status \"%s\" was found.\n",
	              unit.getCode(), unit.getName(), unit.getStatus());
	        }
	      } else {
	        System.out.println("No ad units found.");
	      }

	      pageToken = adUnits.getNextPageToken();
	    } while (pageToken != null);

	    System.out.println();

	    // Return the last page of ad units, so that the main sample has something to run.
	    return adUnits;
	  }
	}