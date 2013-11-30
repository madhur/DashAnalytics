package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.Account;
import com.google.api.services.adsense.model.Accounts;

public class GetAllAccounts {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @param maxPageSize the maximum page size to retrieve.
	   * @return the last page of retrieved accounts.
	   * @throws Exception
	   */
	  public static Accounts run(AdSense adsense, int maxPageSize) throws Exception {
	    System.out.println("=================================================================");
	    System.out.println("Listing all AdSense accounts");
	    System.out.println("=================================================================");

	    // Retrieve account list in pages and display data as we receive it.
	    String pageToken = null;
	    Accounts accounts = null;
	    do {
	      accounts = adsense.accounts().list()
	          .setMaxResults(maxPageSize)
	          .setPageToken(pageToken)
	          .execute();

	      if (accounts.getItems() != null && !accounts.getItems().isEmpty()) {
	        for (Account account : accounts.getItems()) {
	          System.out.printf("Account with ID \"%s\" and name \"%s\" was found.\n",
	              account.getId(), account.getName());
	        }
	      } else {
	        System.out.println("No accounts found.");
	      }

	      pageToken = accounts.getNextPageToken();
	    } while (pageToken != null);

	    System.out.println();
	    return accounts;
	  }
	}