/**
 *  QProber Implementation
 *  COMS E6111 - Project 2  03/25/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.io.File;
import java.util.*;

public class QProber {

	public static void main(String[] args) {
		
		// Check if there are 4 or 5 arguments provided
		if (args.length < 4 || args.length > 5)
			usage("Invalid Arguments");

		int i = 0; // process each argument, starting from 0th
		
		// Working Directory - where category and cache directories are
		// The category files should be copied in from a wrapper script
		// workdir/category  - contains category files
		// workdir/cache     - writeable directory to cache pages
		String workdir = args[i++];
		String catdir  = workdir + "/categories";
		//String cachedir = workdir + "/cache";

		// Die if category directory does not exist 
		if (! (new File(catdir)).exists()) {		
			System.err.println("Directory " + catdir + " does not exist");
			System.exit(1);
		}
		// Create cachedir if it does not exist
//		File cdir= new File(cachedir);
//		if (! cdir.exists() && ! cdir.mkdir()) {		
//			System.err.println("Unable to mkdir " + cachedir);
//			System.exit(1);
//		}
		
		// Set search string
		String host = args[i++];

		// Convert t_es and t_ec to real numbers
		float t_es = 0;
	    try {
			t_es = Float.parseFloat(args[i++]);
	    } catch (NumberFormatException e) {
	        System.err.println("Invalid value for t_es: " + e.getMessage());
	        System.exit(1);
	    }
	    if (t_es < 0 || t_es > 1) {
	    	System.err.println("Invalid value for t_es. Specificity threshold must be between 0 and 1.");
	    }

		int t_ec = 0;
	    try {
	    	t_ec = Integer.parseInt(args[i++]);
	    } catch (NumberFormatException e) {
	        System.err.println("Invalid value for t_ec: " + e.getMessage());
	        System.exit(1);
	    }
	    if (t_ec < 1) {
	    	System.err.println("Invalid value for t_ec. Coverage threshold must be >= 1.");
	    }
		
		// Set appid (optional)
		String appid = (args.length > i) ? args[i++] : 
			"ypykm2bV34HB8360S0knusfiUrQYS5A3ZvDlsTIHh13Vw8BPYSUHNloyoJ2bSg--";

//		System.out.println("DEBUG: workdir = " + workdir);
//		System.out.println("DEBUG: host = " + host);
//		System.out.println("DEBUG: t_es = " + t_es);
//		System.out.println("DEBUG: t_ec = " + t_ec);
//		System.out.println("DEBUG: appid = " + appid);

		// Get the probes from files, and build the classification hierarchy
		Category root = new Category("Root", catdir);		

		// QProber
		YahooBossSearcher yahoo = new YahooBossSearcher(appid);
		Classifier c = new Classifier(yahoo);
		
		System.out.println("\n\nClassifying...");
		ArrayList<Category> clist = c.classify(root, host, t_ec, t_es, 1);

		System.out.println("\n\nClassification:");
		for (Category cat: clist) {
			System.out.println(cat.getFullName());
		}

		// Content Summary
		System.out.println("\n\nExtracting topic content summaries...");
		for (Category cat : clist) {
			Category cp = cat;
			while (cp != null) {
				if (!cp.isLeaf()) {
					System.out.println("Creating Content Summary for "+cp);
					cp.buildContentSummary(host);
				}
				cp = cp.getParent();
			}
		}
	}

	/**
	 * Returns an error message and usage information
	 * 
	 * @param errMsg  an error string
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
        
        System.err.println("Usage: " + mainClass + " <workdir> <host> <t_es> <t_ec> [<yahoo appId>]");
        System.exit(1);
    }
}
