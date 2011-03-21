import java.io.*;
import java.util.*;

public class Category {
	//private static final char pathDelimiter = '\\' ;  // Windows
	private static final char pathDelimiter = '/' ; // Unix
	
	private String name;
	private Category parent = null; // root has no parent
	private String probepath;
	private String probefile;
	private HashMap<String,Category> children = new HashMap<String,Category>(); // all sub-categories
	//private HashSet<String> probes = new HashSet<String>(); // note: root category has no probes
	private ArrayList<String> probes = new ArrayList<String>();
	
	// Calculated data
	// site -> value
	private HashMap<String,Integer> eCoverage = new HashMap<String,Integer> ();
	private HashMap<String,Float> eSpecificity = new HashMap<String,Float> ();
	
	public Category (String name) {
		this(name, ".");
	}

	public Category (String name, String path) {
		this.name = name;
		this.probepath = path;
		this.probefile = this.probepath + pathDelimiter + name.toLowerCase() + ".txt";
		buildHierarchy(this.probefile);
	}

	public void setParent(Category c) {
		parent = c;
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
					if (! children.containsKey(childName)) {
						child = new Category(childName, probepath);
						child.setParent(this); // @@@ Need this?
						children.put(childName, child);
					} else {
						child = children.get(childName);
					}
					child.addProbe(probe);
				}
			} finally {
				scanner.close();
			}
		} catch (FileNotFoundException e) {
			// Leaf node if no more category file
			// @@@ TODO: debug code, comment this out 
			System.out.println("Leaf-level category: " + name);
		}
	}

//	public String debugSring() {
//		StringBuilder sb = new StringBuilder();
//		sb.append(name + ":");
//		for (String k : children.keySet()) {
//			sb.append(" " + k);
//		}
//		sb.append("\n");
//		sb.append(name + " probes:\n");
//		for (String p : probes) {
//			sb.append (" " + p + "\n");
//		}
//
//		// get child's children...
//		for (String k : children.keySet()) {
//			sb.append(children.get(k));
//		}
//		return sb.toString();
//	}
	public String toString() {
		return name;
	}
}
