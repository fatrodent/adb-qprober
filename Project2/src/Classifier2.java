import java.util.*;

/**
 *  Implements classifying algorithm based on Luis Gravano's QProber
 *  
 *  COMS E6111 - Project 2  03/25/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */
public class Classifier2 {
	private YahooBossSearcher yahoo;

	// Calculate Especificity vector
	// Maps Category Name to ESpecificity value
	private HashMap<String, Float> eSpecificity = new HashMap<String, Float>();
	

	// Pre-built category hierarchy.
	public Classifier2(YahooBossSearcher yahoo) { // Category root) {
		this.yahoo = yahoo;
		//this.root = root;
	}

	/**
	 * Classifies a database
	 * @param c category
	 * @param d database (host site)
	 * @param t_ec coverage threshold
	 * @param t_es specificity threshold
	 * @param esp specificity of parent
	 * @return Categories of given database
	 */
	public ArrayList<Category> classify(Category c, String d, int t_ec, float t_es, float esp) {
		ArrayList<Category> result = new ArrayList<Category>();	
		
		// If leaf node, return it
		if (c.isLeaf()) {
			result.add(c);
			return result;
		}
		
		// Get Ecoverage for D: maps category -> coverage
		HashMap<String, Integer> ecoverage = new HashMap<String, Integer>();
		
		// For each sub-category of C, probe
		for (Category cat: c.getChildren()) {

			// For each query probe, count matches
			int cov = 0;  // Coverage
			for (String probe : cat.getProbes() ) {
				// Get result and count matches
				YahooResults results = yahoo.search(probe, d);
				int matches = results.getCoverage();
				cov += matches;
			}
			cat.setECoverage(d,cov);
		}
		
		// DEBUG
		for (Category cat: c.getChildren()) {
			int cov = cat.getECoverage(d);
			System.out.println("ECoverage("+d+","+cat+")="+cov);
		}
		
		// Calculate ESpecificity vector
		//HashMap<String, Float> eSpecificity = new HashMap<String, Float>();
		for (Category cat: c.getChildren()) {
			float s = calculateESpecificity(d, cat);
			//eSpecificity.put(cat.getName(), s);
			System.out.println("ESpecificity("+d+","+cat+")="+s); // debug
		}
		
		// For each subcategory of C, classify
		for (Category cat: c.getChildren()) {
			if (cat.getESpecificity(d) >= t_es && cat.getECoverage(d) >= t_ec) {
				result.addAll(classify(cat, d, t_ec, t_es, cat.getESpecificity(d)));
			}
		}
		
		if (result.isEmpty())
			result.add(c);
		
		return result;
	}

	/*
	 * Calculate the ESpecificity(D, C)
	 * D = Database or site 
	 * C = Category 
	 */
	private float calculateESpecificity(String d, Category c) {
		Category parent = c.getParent();

		// sum of the ECoverage for all children of the parent
		int sumCoverage = 0;
		for (Category pc: parent.getChildren()) {
			sumCoverage += pc.getECoverage(d);
		}

		float eSpecificity = ( parent.getESpecificity(d) * c.getECoverage(d) ) / sumCoverage;
		c.setESpecificity(d,eSpecificity);
		
		//System.out.println("DEBUG: ESpecificity(" + c + ") = " + eSpecificity);
		return eSpecificity;
	}
}
