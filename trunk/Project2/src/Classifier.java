import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Implements classifying algorithm based on Luis Gravano's QProber
 *  
 *  COMS E6111 - Project 2  03/25/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */
public class Classifier {
	
	public static final int ROOT = 0;
	public static final int COMPUTERS = 1;
	public static final int HARDWARE = 2;
	public static final int PROGRAMMING = 3;
	public static final int HEALTH = 4;
	public static final int FITNESS = 5;
	public static final int DISEASES = 6;
	public static final int SPORTS = 7;
	public static final int BASKETBALL = 8;
	public static final int SOCCER = 9;
	
	YahooBossSearcher yahoo;
	
	public Classifier(YahooBossSearcher yahoo) {
		this.yahoo = yahoo;
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
	public ArrayList<Integer> classify(int C, String D, int t_ec, float t_es, float esp) {
		ArrayList<Integer> result = new ArrayList<Integer>();	
		
		// If leaf node, return it
		if (isLeaf(C)) {
			result.add(C);
			return result;
		}
		
		// Get Ecoverage for D
		HashMap<Integer, Integer> ecoverage = new HashMap<Integer, Integer>();
		
		// For each subcategory of C
		for (int cat : getSubcat(C)) {
			
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
			
			// Calculate Especificity
			float espec = getEspecificity();
			
			if (espec >= t_es && cov >= t_ec) {
				result.addAll(classify(cat, D, t_ec, t_es, espec));
			}
		}
		
		if (result.isEmpty())
			result.add(C);
		
		return result;
	}
	
	private boolean isLeaf(int category) {
		
		return true;
		
	}

	private int[] getSubcat(int category) {
		
		return null;
		
	}
	
	private ArrayList<String> getQueryProbes(int category) {
		
		return null;
		
	}
	
	private float getEspecificity() {
		
		return 0;
		
	}
	
}
