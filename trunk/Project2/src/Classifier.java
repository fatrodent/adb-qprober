import java.io.*;
import java.util.*;

/**
 *  Implements classifying algorithm based on Luis Gravano's QProber
 *  
 *  COMS E6111 - Project 2  03/25/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */
public class Classifier {
	
	YahooBossSearcher yahoo;
	HashMap<String, ArrayList<String>> probes; // Maps category to list of query probes
	
	public Classifier(YahooBossSearcher yahoo) {
		this.yahoo = yahoo;
		buildHierarchy();
	}
	
	/**
	 * Build hierarchy
	 */
	private void buildHierarchy() {
		ArrayList<String> probe = new ArrayList<String>();
		
		try {
			
			// Read file
			String filename = "root.txt";
			File file = new File(filename);
			Scanner scan = new Scanner(file);
			
			while (scan.hasNextLine()) {
				scan.findInLine("(.*?) (.*?)\n");
				String category = scan.match().group(1);
				probes.put(category, null);
				String p = scan.match().group(2);
				probe.add(p);
				System.out.println("DEBUG: Reading "+category+" with: "+probe);
			}
						
		} catch (FileNotFoundException e) {
			System.err.println("Could not open file");
		}
		
		
	}
	
	/**
	 * Classifies a database
	 * @param C category
	 * @param D database (host site)
	 * @param t_ec coverage threshold
	 * @param t_es specificity threshold
	 * @param esp specificity of parent
	 * @return Categories of given database
	 */
	public ArrayList<String> classify(String C, String D, int t_ec, float t_es, float esp) {
		ArrayList<String> result = new ArrayList<String>();	
		
		// If leaf node, return it
		if (isLeaf(C)) {
			result.add(C);
			return result;
		}
		
		// Get Ecoverage for D: maps category -> coverage
		HashMap<String, Integer> ecoverage = new HashMap<String, Integer>();
		
		// For each subcategory of C, probe
		for (String cat : getSubcat(C)) {
			
			// Coverage
			int cov = 0;
			
			// For each query probe, count matches
			for (String probe : getQueryProbes(cat)) {
				
				// Get result and count matches
				YahooResults results = yahoo.search(probe, D);
				int matches = results.getCoverage();
				cov += matches;
			}
			
			ecoverage.put(cat, cov);
		}
		
		// Calculate Especificity vector
		HashMap<String, Float> especificity = getEspecificity(ecoverage, esp);
		
		// For each subcategory of C, classify
		for (String cat : getSubcat(C)) {
			
			// If above thresholds, combine the result
			if (especificity.get(cat) >= t_es && ecoverage.get(cat) >= t_ec) {
				result.addAll(classify(cat, D, t_ec, t_es, especificity.get(cat)));
			}
		}
		
		if (result.isEmpty())
			result.add(C);
		
		return result;
	}
	
	/**
	 * If category is a leaf node
	 * @param category
	 * @return
	 */
	private boolean isLeaf(String category) {
		if (getSubcat(category).isEmpty())
			return true;
		
		return false;	
	}

	/**
	 * Return subcategories
	 * @param category
	 * @return
	 */
	private ArrayList<String> getSubcat(String category) {
		ArrayList<String> arr = new ArrayList<String>();
		
		if (category.equals("Root")) {
			arr.add("Computers");
			arr.add("Health");
			arr.add("Sports");
		} else if (category.equals("Computers")) {
			arr.add("Hardware");
			arr.add("Programming");
		} else if (category.equals("Health")) {
			arr.add("Fitness");
			arr.add("Diseases");
		} else if (category.equals("Sports")) {
			arr.add("Basketball");
			arr.add("Soccer");
		} 
		
		return arr;
	}
	
	/**
	 * Return query probes
	 * @param category
	 * @return
	 */
	private ArrayList<String> getQueryProbes(String category) {
		return probes.get(category);
	}
	
	private HashMap<String, Float> getEspecificity(HashMap<String, Integer> ecoverage, Float esp) {
		
		return null;
		
	}
	
//	public static void main(String args[]) {
//		
//		YahooBossSearcher yahoo = new YahooBossSearcher("ypykm2bV34HB8360S0knusfiUrQYS5A3ZvDlsTIHh13Vw8BPYSUHNloyoJ2bSg--");
//
//		Classifier c = new Classifier(yahoo);
//		
//		float e = Float.parseFloat("0.6");
//		
//		for (String s : c.classify("Root","diabetes.org",100,e,e)) 
//			System.out.println(s);
//		
//	}
	
}
