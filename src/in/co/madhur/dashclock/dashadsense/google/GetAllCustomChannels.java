package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.CustomChannel;
import com.google.api.services.adsense.model.CustomChannels;

public class GetAllCustomChannels {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @param adClientId the ID for the ad client to be used.
	   * @param maxPageSize the maximum page size to retrieve.
	   * @return the last page of custom channels.
	   * @throws Exception
	   */
	  public static CustomChannels run(AdSense adsense, String adClientId, int maxPageSize)
	      throws Exception {
	    System.out.println("=================================================================");
	    System.out.printf("Listing all custom channels for ad client %s\n", adClientId);
	    System.out.println("=================================================================");

	    // Retrieve custom channel list in pages and display the data as we receive it.
	    String pageToken = null;
	    CustomChannels customChannels = null;
	    do {
	      customChannels = adsense.customchannels().list(adClientId)
	          .setMaxResults(maxPageSize)
	          .setPageToken(pageToken)
	          .execute();

	      if (customChannels.getItems() != null && !customChannels.getItems().isEmpty()) {
	        for (CustomChannel channel : customChannels.getItems()) {
	          System.out.printf("Custom channel with code \"%s\" and name \"%s\" was found.\n",
	              channel.getCode(), channel.getName());
	        }
	      } else {
	        System.out.println("No custom channels found.");
	      }

	      pageToken = customChannels.getNextPageToken();
	    } while (pageToken != null);

	    System.out.println();
	    return customChannels;
	  }
	}