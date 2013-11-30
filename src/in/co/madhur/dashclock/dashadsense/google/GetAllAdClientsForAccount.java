package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.AdClient;
import com.google.api.services.adsense.model.AdClients;

public class GetAllAdClientsForAccount {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @param accountId the ID for the account to be used.
	   * @param maxPageSize the maximum page size to retrieve.
	   * @throws Exception
	   */
	  public static void run(AdSense adsense, String accountId, int maxPageSize) throws Exception {
	    System.out.println("=================================================================");
	    System.out.printf("Listing all ad clients for account %s\n", accountId);
	    System.out.println("=================================================================");

	    // Retrieve ad client list in pages and display data as we receive it.
	    String pageToken = null;
	    AdClients adClients = null;
	    do {
	      adClients = adsense.accounts().adclients().list(accountId)
	          .setMaxResults(maxPageSize)
	          .setPageToken(pageToken)
	          .execute();

	      if (adClients.getItems() != null && !adClients.getItems().isEmpty()) {
	        for (AdClient adClient : adClients.getItems()) {
	          System.out.printf("Ad client for product \"%s\" with ID \"%s\" was found.\n",
	              adClient.getProductCode(), adClient.getId());
	          System.out.printf("\tSupports reporting: %s\n",
	              adClient.getSupportsReporting() ? "Yes" : "No");
	        }
	      } else {
	        System.out.println("No ad clients found.");
	      }

	      pageToken = adClients.getNextPageToken();
	    } while (pageToken != null);

	    System.out.println();
	  }
	}