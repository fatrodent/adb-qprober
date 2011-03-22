/**
 *  QProber Implementation
 *  COMS E6111 - Project 2  03/25/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.util.*;

public class QProber {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Check if there are 2 or 3 arguments provided
		if (args.length < 3 || args.length > 4)
			usage("Invalid Arguments");

		// Set search string
		String host = args[0];

		// Convert t_es and t_ec to real numbers
		float t_es = 0;
	    try {
			t_es = Float.parseFloat(args[1]);
	    } catch (NumberFormatException e) {
	        System.err.println("Invalid value for t_es: " + e.getMessage());
	        System.exit(1);
	    }
	    if (t_es < 0 || t_es > 1) {
	    	System.err.println("Invalid value for t_es. Specificity threshold must be between 0 and 1.");
	    }

		int t_ec = 0;
	    try {
	    	t_ec = Integer.parseInt(args[2]);
	    } catch (NumberFormatException e) {
	        System.err.println("Invalid value for t_ec: " + e.getMessage());
	        System.exit(1);
	    }
	    if (t_ec < 1) {
	    	System.err.println("Invalid value for t_ec. Coverage threshold must be >= 1.");
	    }
		
		// Set appid (optional)
		String appid = (args.length >= 4) ? args[3] : 
			"ypykm2bV34HB8360S0knusfiUrQYS5A3ZvDlsTIHh13Vw8BPYSUHNloyoJ2bSg--";

		
		System.out.println("DEBUG: host = " + host);
		System.out.println("DEBUG: t_es = " + t_es);
		System.out.println("DEBUG: t_ec = " + t_ec);
		System.out.println("DEBUG: appid = " + appid);
		
		// Get the probes from files, and build the classification hierarchy
		Category root = new Category("Root", "/Users/Nicole/workspace/Project2/src/categories");		
		
		YahooBossSearcher yahoo = new YahooBossSearcher(appid);
		Classifier2 c = new Classifier2(yahoo);
		ArrayList<Category> clist = c.classify(root, host, t_ec, t_es, 1);

		for (Category cat: clist) {
			System.out.println(cat);
		}
	}

	/**
	 * Returns an error message and usage information
	 * @param errMsg A string with the desired error message
	 */
    public static void usage(String errMsg) {
    	if (errMsg != null)
    		System.err.println(errMsg);
    	usage();
    }

    /**
     * Returns information about the expected usage
     */
    public static void usage() {
    	// This is like $0 in perl/shell
    	StackTraceElement[] stack = Thread.currentThread ().getStackTrace ();
        StackTraceElement main = stack[stack.length - 1];
        String mainClass = main.getClassName ();
        
        System.err.println("Usage: " + mainClass + " <host> <t_es> <t_ec> [<yahoo appId>]");
        System.exit(1);
    }
}
