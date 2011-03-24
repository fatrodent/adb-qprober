/**
 *  Parse JSON results from search and extracts samples
 *  
 *  COMS E6111 - Project 2  03/25/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.util.*;

public class YahooResults {
	
	// URLs
	private ArrayList<String> _url = new ArrayList<String>();
	// Count
	private int count = 0;
	private int totalhits = 0;
	private int deephits = 0;
	
	private int top_k = 4; // top 4 results
	private String rawResult = null;
	
	
	/**
	 * Constructor with result data
	 * @param res A string of data in JSON format
	 */
	public YahooResults (String res) {
		rawResult = res; // save for debugging
		buildArray(res);
	}

	/**
	 * Parses results data and stores urls
	 * @param res A string of data in JSON format
	 */
	private void buildArray(String res) {
		//System.out.println("DEBUG: result="+res);  //@@@ DEBUG
		Scanner scan = new Scanner(res);
		
		// Parse the no of results
		scan.findInLine("\"totalhits\":\"(\\w+)\"");
		totalhits = Integer.parseInt(scan.match().group(1));

		scan.findInLine("\"deephits\":\"(\\w+)\"");
		deephits = Integer.parseInt(scan.match().group(1));
		
		scan.findInLine("\"count\":\"(\\w+)\"");
		count = Integer.parseInt(scan.match().group(1));

		//System.out.println("DEBUG: totalhits="+totalhits+", deephits="+deephits+", count="+ count); //@@@ DEBUG

		// Parse the results abstract, title and url
		String matchRes = "\"abstract\":\"(.*?)\",.*?\"title\":\"(.*?)\",\"url\":\"(.*?)\"";
		
		// Store up to top-k results into array
		for (int i = 0; i < Math.min(top_k, totalhits); i++) {
			scan.findInLine(matchRes); // Match one result

			// Store the url for sampling
			String url = scan.match().group(3);
			_url.add(trim(url));
		}
		
		scan.close();
	}
	
	/**
	 * Returns result's URLs
	 * @return
	 */
	public ArrayList<String> getURLs() {
		return _url;
	}
	
	/**
	 * Returns coverage
	 * @return
	 */
	public int getCoverage() {
		return totalhits;
	}
	
	public String getRawResult() { // return the JSON string
		return rawResult;
	}

	/**
	 * Removes HTML code and replaces escaped characters
	 * @param str A string from Yahoo
	 * @return A clean string
	 */
	public static String trim(String str) {
		String newStr = null;

		newStr = str.replaceAll("\\<.*?\\>", "");
		newStr = newStr.replaceAll("\\\\/", "/");
		newStr = newStr.replaceAll("\\\\\"", "\"");

		return newStr;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("count=" + count + ", ");
		sb.append("totalhits=" + totalhits + ", ");
		sb.append("deephits=" + deephits);
		return sb.toString();
	}
}
