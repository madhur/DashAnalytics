package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.UrlChannel;
import com.google.api.services.adsense.model.UrlChannels;

public class GetAllUrlChannels {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @param adClientId the ID for the ad client to be used.
	   * @param maxPageSize the maximum page size to retrieve.
	   * @throws Exception
	   */
	  public static void run(AdSense adsense, String adClientId, int maxPageSize) throws Exception {
	    System.out.println("=================================================================");
	    System.out.printf("Listing all URL channels for ad client %s\n", adClientId);
	    System.out.println("=================================================================");

	    // Retrieve URL channel list in pages and display the data as we receive it.
	    String pageToken = null;
	    do {
	      UrlChannels urlChannels = adsense.urlchannels().list(adClientId)
	          .setMaxResults(maxPageSize)
	          .setPageToken(pageToken)
	          .execute();

	      if (urlChannels.getItems() != null && !urlChannels.getItems().isEmpty()) {
	        for (UrlChannel channel : urlChannels.getItems()) {
	          System.out.printf("URL channel with URL pattern \"%s\" was found.\n",
	              channel.getUrlPattern());
	        }
	      } else {
	        System.out.println("No URL channels found.");
	      }

	      pageToken = urlChannels.getNextPageToken();
	    } while (pageToken != null);

	    System.out.println();
	  }
	}