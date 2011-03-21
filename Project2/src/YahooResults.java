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
	
	
	/**
	 * Constructor with result data
	 * @param res A string of data in JSON format
	 */
	public YahooResults (String res) {
		buildArray(res);
	}
	
	/**
	 * Parses results data and compiles into an object array
	 * @param res A string of data in JSON format
	 */
	private void buildArray(String res) {
		
		Scanner scan = new Scanner(res);
		
		// Parse the no of results
		scan.findInLine("\"totalhits\":\"(\\w+)\"");
		totalhits = Integer.parseInt(scan.match().group(1));
		
		scan.findInLine("\"deephits\":\"(\\w+)\"");
		deephits = Integer.parseInt(scan.match().group(1));
		
		scan.findInLine("\"count\":\"(\\w+)\"");
		count = Integer.parseInt(scan.match().group(1));
		
		// Parse the results abstract, title and url
		String matchRes = "\"abstract\":\"(.*?)\",.*?\"title\":\"(.*?)\",\"url\":\"(.*?)\"";
		
		// Parse the results abstract
		String abstractRes = "\"abstract\":\"(.*?)\",.*?\"title\":\".*?\",\"url\":\".*?\"";
		
		// Store each result into array
		for (int i = 0; i < count; i++) {
			scan.findInLine(abstractRes); // Match one result

			// Store the three values into ResultNode
			String summary = scan.match().group(1);
			//String title = scan.match().group(2);
			//String url = scan.match().group(3);
			
			//ResultNode node = new ResultNode(i, summary, title, url);
			//_arr.add(node);
			_summ.add(summary);
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
	public float getCoverage() {
		return totalhits;
	}
	
	public float getSpecificity() {
		return 0;
	}
	
}
