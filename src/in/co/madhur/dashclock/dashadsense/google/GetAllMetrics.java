package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.Metadata;
import com.google.api.services.adsense.model.ReportingMetadataEntry;

public class GetAllMetrics {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @throws Exception
	   */
	  public static void run(AdSense adsense) throws Exception {
	    System.out.println("=================================================================");
	    System.out.println("Listing all metrics for default account");
	    System.out.println("=================================================================");

	    // Retrieve and display metrics.
	    Metadata metrics = adsense.metadata().metrics().list().execute();

	    if (metrics.getItems() != null && !metrics.getItems().isEmpty()) {
	      for (ReportingMetadataEntry metric : metrics.getItems()) {
	        boolean firstProduct = true;
	        StringBuilder products = new StringBuilder();
	        for (String product : metric.getSupportedProducts()) {
	          if (!firstProduct) {
	            products.append(", ");
	          }
	          products.append(product);
	          firstProduct = false;
	        }
	        System.out.printf("Metric id \"%s\" for product(s): [%s] was found.\n",
	            metric.getId(), products.toString());
	      }
	    } else {
	      System.out.println("No metrics found.");
	    }

	    System.out.println();
	  }
	}