/**
 *  Parse JSON results from search
 *  
 *  COMS E6111 - Project 2  03/25/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.util.ArrayList;
import java.util.Scanner;

public class YahooResults {
	
	// Results
	private ArrayList<ResultNode> _arr = new ArrayList<ResultNode>(); 
	// Summaries
	private ArrayList<String> _summ = new ArrayList<String>();
	// Count
	private int count = 0;
	private int totalhits = 0;
	private int deephits = 0;
	
	private int top_k = 4; // hard-coded
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
	 * Parses results data and compiles into an object array
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
		
		// Parse the results abstract
		//String abstractRes = "\"abstract\":\"(.*?)\",.*?\"title\":\".*?\",\"url\":\".*?\"";
		
		// Store top-k results into array
		for (int i = 0; i < top_k; i++) {
			scan.findInLine(matchRes); // Match one result

			// Store the three values into ResultNode
			String summary = scan.match().group(1);
			String title = scan.match().group(2);
			String url = scan.match().group(3);
			
			ResultNode node = new ResultNode(i, summary, title, url);
			_arr.add(node);
			//_summ.add(summary);
		}
		
		scan.close();
	}
	
	/**
	 * Returns result nodes
	 * @return
	 */
	public ArrayList<ResultNode> getResultNodes() {
		return _arr;
	}
	
	/**
	 * Returns result's web pages
	 * @return
	 */
	public ArrayList<String> getDocs() {
		ArrayList<String> res = new ArrayList<String>();
		
		for (ResultNode r : _arr) {
			try {
				res.add(r.getWebPage());
			} catch (Exception e) {
				res.add(r.getTitle()+" "+r.getSummary());
			}
		}
		
		return res;
	}
	
	public int getResultCount () {
		return _arr.size();
	}
	
	public ArrayList<String> getResultSummaries() {
		return _summ;
	}
	
	/**
	 * Returns coverage
	 * @return
	 */
	public int getCoverage() {
		return totalhits;
	}
	
	public float getSpecificity() {
		return 0;
	}
	
	public String getRawResult() { // return the JSON string
		return rawResult;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("count=" + count + ", ");
		sb.append("totalhits=" + totalhits + ", ");
		sb.append("deephits=" + deephits);
		return sb.toString();
	}
}
