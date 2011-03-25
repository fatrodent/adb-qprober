import java.io.*;
import java.util.*;

public class Category {
	private static final char pathDelimiter = '/' ;
	
	private String name;      // e.g. Programming
	private String fullname;  // e.g. Root/Computers/Programming

	private Category parent;  // root has no parent, and value is null
	private String probepath;
	private String probefile;
	private HashMap<String,Category> children = new HashMap<String,Category>(); // all sub-categories
	private ArrayList<String> probes = new ArrayList<String>();

	// maps query string to top 4 doc samples
	private HashMap<String,ArrayList<String>> docSampleHash = new HashMap<String,ArrayList<String>> ();
	
	// Calculated data - eCoverage and eSpecificity 
	private HashMap<String,Integer> eCoverage = new HashMap<String,Integer> (); // host => eCoverage
	private HashMap<String,Float> eSpecificity = new HashMap<String,Float> ();  // host => eSpecificity
	
	public Category (String name) {
		this(name, ".");
	}

	public Category (String name, String path) {
		this(null, name, path); // root has no parent
	}

	public Category (Category parent, String name, String path) {
		this.parent = parent;
		this.name = name;
		this.fullname = (parent == null) ? name : parent.getFullName() + pathDelimiter + name;
		this.probepath = path;
		this.probefile = this.probepath + pathDelimiter + name.toLowerCase() + ".txt";
		buildHierarchy(this.probefile);
	}
	
	public Category getParent() {
		return parent;
	}
	
	public Collection<Category> getChildren() {
		return children.values();
	}
	public String getName () { // return category name
		return name;
	}

	public String getFullName() {
		return fullname;
	}
	
	public void addProbe(String p) {
		probes.add(p);
	}
	public ArrayList<String> getProbes () {
		return probes;
	}
	
	public void setECoverage (String site, int c) {
		eCoverage.put(site, c);
	}
	public int getECoverage (String site) {
		if (eCoverage.containsKey(site)) {
			return eCoverage.get(site);
		} else {
			return 0;  // @@@ TBD
		}
	}
	

	public void setESpecificity (String site, float s) {
		eSpecificity.put(site, s);
	}
	public float getESpecificity (String site) {
		if (eSpecificity.containsKey(site)) {
			return eSpecificity.get(site);
		} else {
			return 1; // @@@ TBD
		}
	}

	public boolean isLeaf () {
		return (children.size() == 0); // no children == leaf node
	}
	
	public boolean isRoot() {
		return (parent == null); // root node has no parent
	}
		
	/**
	 * Add url samples from the query probes
	 * 
	 * IMPORTANT NOTE:
	 * this list of urls are not unique within the category. 
	 * uniqueness verification is postponed to run time... 
	 *	
	 * @param docs
	 */
	public void addDocSample(String query, ArrayList<String> urls) {
		docSampleHash.put(query, urls);
	}

	/**
	 * read text file for sub-categories and probes
	 */
	public void buildHierarchy(String file) {
		try {
			Scanner scanner = new Scanner(new FileReader(file)); 
			try {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine().trim() ;
					String[] arr = line.split("\\s+", 2);
					if (arr.length < 2) continue; // skip lines that don't split cleanly 

					String childName = arr[0]; // child category name
					String probe     = arr[1]; // child's probe

					Category child;
					if (! children.containsKey(childName)) { // first time we see this sub-category
						child = new Category(this, childName, probepath);
						children.put(childName, child);
						//System.out.println("Adding category " + child.getFullName());
					} else {
						child = children.get(childName);
					}
					child.addProbe(probe);
				}
			} finally {
				scanner.close();
			}
		} catch (FileNotFoundException e) {
			// Leaf node if no more category file - ignore this exception
			//System.out.println("Leaf-level category: " + name);
		}
	}
	
	/**
	 * Extracts list of words from each URL, combines with document frequency count
	 * Writes content summary to file
	 */
	public void buildContentSummary(String host) {
		HashMap<String, Integer> contentSumm = new HashMap<String, Integer> ();

		// @@@ TODO: Document sample of a node = doc sample of current category + doc sample of child category
		Set<String> probeSet = docSampleHash.keySet();
		HashSet<String> seenUrl = new HashSet<String> (); // to check duplicate url
		int count=0; // progress counter
		for (String probe: probeSet) {
			System.out.println(++count + "/" + probeSet.size());
			//System.out.println(++count + "/" + probeSet.size() + " " + probe); // debug
			ArrayList<String> urls = docSampleHash.get(probe);
			for (String url : urls) {
				if (seenUrl.contains(url)) { // skip duplicate url
					continue;
				}
				seenUrl.add(url);
		
				try {
					// Retrieve page with Lynx
					System.out.println("\nGetting page: "+url +"\n");
					Set<String> words = getWordsLynx.runLynx(url);

					// Merge new list with existing list
					merge(contentSumm, words);

				} catch (Exception e) {
					// Couldn't get URL	
					System.err.println("WARNING: Could not retrieve "+url+" for extraction.");
				}

			}
		}


		// Format HashMap to string
		ArrayList<String> sb = new ArrayList<String>();
		for (Map.Entry<String, Integer> m : contentSumm.entrySet()) {
			sb.add(m.getKey() + "#" + m.getValue());
		}
		
		Collections.sort(sb);
		
		// Create file
		writeContentSummary(sb, host);
		
		// DEBUG: Print to console
		//System.out.println(sb);
	}
	
	/**
	 * Adds a new list of words to existing list and counts document frequency
	 * @param summ
	 * @param words
	 */
	public void merge(HashMap<String, Integer> summ, Set<String> words) {
		
		for (String word : words) {
			if (summ.containsKey(word)) {
				// Increase doc frequency
				int freq = summ.get(word);
				summ.remove(word);
				summ.put(word, ++freq);
			} else {
				// First time seeing word, doc frequency = 1
				summ.put(word, 1);
			}
		}
	}
	
	/**
	 * Write content summary to file
	 * @param s
	 * @param host
	 */
	public void writeContentSummary(ArrayList<String> sb, String host) {
		// File name: Category-Host.txt
		String filename = name + "-" + host + ".txt";
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			for (String s : sb) out.append(s + "\n");
			out.close();
		} catch (Exception e) {
			System.err.println("Could not write "+filename);
		}
	}

	public String toString() {
		return "category:" + name;
	}
}
