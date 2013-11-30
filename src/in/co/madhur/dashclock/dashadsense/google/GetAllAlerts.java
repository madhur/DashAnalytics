package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.Alert;
import com.google.api.services.adsense.model.Alerts;

public class GetAllAlerts {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @throws Exception
	   */
	  public static void run(AdSense adsense) throws Exception {
	    System.out.println("=================================================================");
	    System.out.println("Listing all alerts for default account");
	    System.out.println("=================================================================");

	    // Retrieve and display alerts.
	    Alerts alerts = adsense.alerts().list().execute();

	    if (alerts.getItems() != null && !alerts.getItems().isEmpty()) {
	      for (Alert alert : alerts.getItems()) {
	        System.out.printf("Alert id \"%s\" with severity \"%s\" and type \"%s\" was found.\n",
	            alert.getId(), alert.getSeverity(), alert.getType());
	      }
	    } else {
	      System.out.println("No alerts found.");
	    }

	    System.out.println();
	  }
	}