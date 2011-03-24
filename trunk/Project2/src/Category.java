import java.io.*;
import java.util.*;

public class Category {
	//private static final char pathDelimiter = '\\' ;  // Windows
	private static final char pathDelimiter = '/' ; // Unix
	
	private String name;      // e.g. Programming
	private String fullname;  // e.g. Root/Computers/Programming

	private Category parent = null; // root has no parent
	private String probepath;
	private String probefile;
	private HashMap<String,Category> children = new HashMap<String,Category>(); // all sub-categories
	private ArrayList<String> probes = new ArrayList<String>();
	private ArrayList<String> docSample = new ArrayList<String>(); // list of urls for sampling
	
	// Calculated data
	// site -> value
	private HashMap<String,Integer> eCoverage = new HashMap<String,Integer> ();
	private HashMap<String,Float> eSpecificity = new HashMap<String,Float> ();
	
	public Category (String name) {
		this(name, ".");
	}

	public Category (String name, String path) {
		this.name = name;
		this.fullname = name; // Root
		this.probepath = path;
		this.probefile = this.probepath + pathDelimiter + name.toLowerCase() + ".txt";
		buildHierarchy(this.probefile);
	}

	public Category (Category parent, String name, String path) {
		this.parent = parent;
		this.name = name;
		this.fullname = parent.getFullName() + pathDelimiter + name;
		this.probepath = path;
		this.probefile = this.probepath + pathDelimiter + name.toLowerCase() + ".txt";
		buildHierarchy(this.probefile);
	}
	
//	public void setParent(Category c) {
//		parent = c;
//		fullname = parent.getFullName() + "/" + name;
//	}

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
	 * Add unique url samples from the query probes for this category
	 * @param docs
	 */
	public void addDocSample(ArrayList<String> urls) {
		for (String url : urls) {
			if (!docSample.contains(url))
				docSample.add(url);
		}
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
		
		for (String url : docSample) {
			try {
				// Retrieve page with Lynx
				System.out.println("\nGetting page: "+url +"\n");
				Set<String> words = getWordsLynx.runLynx("lynx",url);
				
				// @@@ TODO - Store in cache and use a progress counter
				
				// Merge new list with existing list
				merge(contentSumm, words);
				
			} catch (Exception e) {
				// Couldn't get URL	
				System.err.println("WARNING: Could not retrieve "+url+" for extraction.");
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
