package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.SavedAdStyle;
import com.google.api.services.adsense.model.SavedAdStyles;

public class GetAllSavedAdStyles {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @param maxPageSize the maximum page size to retrieve.
	   * @return the last page of ad styles.
	   * @throws Exception
	   */
	  public static SavedAdStyles run(AdSense adsense, int maxPageSize) throws Exception {
	    System.out.println("=================================================================");
	    System.out.printf("Listing all saved ad styles for default account\n");
	    System.out.println("=================================================================");

	    // Retrieve saved ad style list and display the data as we receive it.
	    String pageToken = null;
	    SavedAdStyles savedAdStyles = null;
	    do {
	      savedAdStyles = adsense.savedadstyles()
	          .list()
	          .setMaxResults(maxPageSize)
	          .setPageToken(pageToken)
	          .execute();

	      if (savedAdStyles.getItems() != null && !savedAdStyles.getItems().isEmpty()) {
	        for (SavedAdStyle savedAdStyle : savedAdStyles.getItems()) {
	          System.out.printf("Saved ad style with name \"%s\" was found.\n", savedAdStyle.getName());
	        }
	      } else {
	        System.out.println("No saved ad styles found.");
	      }

	      pageToken = savedAdStyles.getNextPageToken();
	    } while (pageToken != null);

	    System.out.println();
	    return savedAdStyles;
	  }
	}