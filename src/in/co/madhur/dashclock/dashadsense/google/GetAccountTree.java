package in.co.madhur.dashclock.dashadsense.google;

import java.util.List;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.Account;

public class GetAccountTree {

	  /**
	   * Auxiliary method to recurse through the account tree, displaying it.
	   * @param parentAccount the account to be print a sub-tree for.
	   * @param level the depth at which the top account exists in the tree.
	   */
	  private static void displayTree(Account parentAccount, int level) {
	    for (int i = 0; i < level; i++) {
	      System.out.print("  ");
	    }
	    System.out.printf("Account with ID \"%s\" and name \"%s\" was found.\n", parentAccount.getId(),
	        parentAccount.getName());

	    List<Account> subAccounts = parentAccount.getSubAccounts();

	    if (subAccounts != null && !subAccounts.isEmpty()) {
	      for (Account subAccount : subAccounts) {
	        displayTree(subAccount, level + 1);
	      }
	    }
	  }

	  /**
	   * Runs this sample.
	   *
	   * @param adsense AdSense service object on which to run the requests.
	   * @param accountId the ID for the account to be used.
	   * @throws Exception
	   */
	  public static void run(AdSense adsense, String accountId) throws Exception {
	    System.out.println("=================================================================");
	    System.out.printf("Displaying AdSense account tree for %s\n", accountId);
	    System.out.println("=================================================================");

	    // Retrieve account.
	    Account account = adsense.accounts().get(accountId).setTree(true).execute();
	    displayTree(account, 0);

	    System.out.println();
	  }
	}