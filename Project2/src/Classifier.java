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
	private YahooBossSearcher yahoo;
	
	// Pre-built category hierarchy.
	public Classifier(YahooBossSearcher yahoo) { // Category root) {
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
		
		// For each sub-category of C, probe
		for (Category subcat: c.getChildren()) {

			// For each query probe, count matches
			int cov = 0;  // Coverage
			for (String probe : subcat.getProbes() ) {
				// Get result and count matches
				YahooResults results = yahoo.search(probe, d);
				if (results == null) continue;
				int matches = results.getCoverage();
				cov += matches;
				// Store doc samples
				c.addDocSample(probe, results.getURLs());
			}
			subcat.setECoverage(d,cov);
		}
		
		// Calculate ESpecificity vector
		for (Category subcat: c.getChildren()) {
			calculateESpecificity(d, subcat);
			System.out.println("Specificity for "+subcat+" is "+ subcat.getESpecificity(d));
			System.out.println("Coverage for "+subcat+" is "+ subcat.getECoverage(d));			
		}
		
		// For each sub-category of C, classify
		for (Category subcat: c.getChildren()) {
			if (subcat.getESpecificity(d) >= t_es && subcat.getECoverage(d) >= t_ec) {
				result.addAll(classify(subcat, d, t_ec, t_es, subcat.getESpecificity(d)));
			}
		}
		
		if (result.isEmpty())
			result.add(c);
		
		//System.out.println("DEBUG: "+c+" result:" + result);
		return result;
	}

	/*
	 * Calculate the ESpecificity(D, C)
	 * D = Database or site 
	 * C = Category 
	 */
	private void calculateESpecificity(String d, Category c) {
		Category parent = c.getParent();

		// sum of the ECoverage for all children of the parent
		int sumCoverage = 0;
		for (Category pc: parent.getChildren()) {
			sumCoverage += pc.getECoverage(d);
		}

		float eSpecificity = ( parent.getESpecificity(d) * c.getECoverage(d) ) / sumCoverage;
		c.setESpecificity(d,eSpecificity);
		
		//System.out.println("DEBUG: ESpecificity(" + c + ") = " + eSpecificity);
		//return eSpecificity;
	}
}
