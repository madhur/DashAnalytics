package in.co.madhur.dashclock.dashadsense.google;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.Metadata;
import com.google.api.services.adsense.model.ReportingMetadataEntry;

public class GetAllDimensions {

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @throws Exception
	   */
	  public static void run(AdSense adsense) throws Exception {
	    System.out.println("=================================================================");
	    System.out.println("Listing all dimensions for default account");
	    System.out.println("=================================================================");

	    // Retrieve and display dimensions.
	    Metadata dimensions = adsense.metadata().dimensions().list().execute();

	    if (dimensions.getItems() != null && !dimensions.getItems().isEmpty()) {
	      for (ReportingMetadataEntry dimension : dimensions.getItems()) {
	        boolean firstProduct = true;
	        StringBuilder products = new StringBuilder();
	        for (String product : dimension.getSupportedProducts()) {
	          if (!firstProduct) {
	            products.append(", ");
	          }
	          products.append(product);
	          firstProduct = false;
	        }
	        System.out.printf("Dimension id \"%s\" for product(s): [%s] was found.\n",
	            dimension.getId(), products.toString());
	      }
	    } else {
	      System.out.println("No dimensions found.");
	    }

	    System.out.println();
	  }
	}