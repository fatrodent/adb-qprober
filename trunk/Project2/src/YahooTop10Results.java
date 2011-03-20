/****
 *  Nicole Lee (ncl2108), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	This class parses the data from Yahoo Search and stores results in an array.
 *	Also collects relevance judgment of the results.
 ****/

import java.util.ArrayList;
import java.util.Scanner;

public class YahooTop10Results {
	
	// Array of results
	private ArrayList<ResultNode> _arr = new ArrayList<ResultNode>(); 
	private int count = 0;
	
	/**
	 * Constructor with result data
	 * @param res A string of data in JSON format
	 */
	public YahooTop10Results (String res) {
		buildArray(res);
	}
	
	/**
	 * Parses results data and compiles into an object array
	 * @param res A string of data in JSON format
	 */
	private void buildArray(String res) {
		
		Scanner scan = new Scanner(res);
		
		// Parse the no of results
		scan.findInLine("\"count\":\"(\\w+)\"");
		count = Integer.parseInt(scan.match().group(1));
		
		// Parse the results abstract, title and url
		String matchRes = "\"abstract\":\"(.*?)\",.*?\"title\":\"(.*?)\",\"url\":\"(.*?)\"";
		
		// Store each result into array
		for (int i = 0; i < count; i++) {
			scan.findInLine(matchRes); // Match one result

			// Store the three values into ResultNode
			String summary = scan.match().group(1);
			String title = scan.match().group(2);
			String url = scan.match().group(3);
			
			ResultNode node = new ResultNode(i, summary, title, url);
			_arr.add(node);
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
	
	/**
	 * Returns result nodes marked relevant by user
	 * @return relevant result nodes
	 */
	public ArrayList<ResultNode> getRelevantResultNodes() {
		ArrayList<ResultNode> relevantResults = new ArrayList<ResultNode> (_arr.size());
		for (ResultNode r: _arr) {
			//System.out.print("node " + r.getDocId());			
			if (r.isRelevant()) {
				//System.out.print(" is relevant");
				relevantResults.add(r);
			}
			//System.out.println("");
		}
		return relevantResults;
	}
	
	/**
	 * Prints the results and prompts for relevance judgment
	 */
	public void getUserFeedback() {

		System.out.println("Total no of results : "+count);
		System.out.println("Yahoo! Search Results:");
		System.out.println("======================");
		
		Scanner in = new Scanner(System.in);
		
		// Iterate over the results
		int i = 0;
		for (ResultNode node : _arr) {
			System.out.println("Result "+ ++i);
			System.out.println(node);
			
			getRelevance(in, node); // Ask user if the result is relevant
		}
	}
	
	/**
	 * Prompts user if result is relevant
	 * @param in Scanner for getting user input
	 * @param node The result node in question
	 */
	public void getRelevance(Scanner in, ResultNode node) {
		System.out.println("Relevant (Y/N)?");
		String rel = in.next("[YyNn]"); // @@@ Need more elegant exception handling here
		
		// Set the node's relevance
		node.setRelevance(rel);
	}
	
	/**
	 * Calculates the precision score of results in array
	 * @return a precision score
	 */
	public float getPrecision() {
		float prec = 0;
		
		// Add up the number of relevant nodes
		for (ResultNode node : _arr) {
			if (node.isRelevant())
				prec++;
		}
		
		// Divide by no. of results to get score
		return prec / _arr.size();
	}
	
}
