/**
 *  Modified code provided by Prof. Gravano
 *  
 *  COMS E6111 - Project 2  03/25/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class getWordsLynx {
	private static String[] lynxPaths = 
	{
		"/usr/bin/lynx",   // on cs system
		"/lynx/lynx.bat"   // on my window's host
	};

	public static Set<String> runLynx(String url) {
		// find the path of lynx on this system from the known locations
		String lynxPath = null;
		for (String lpath: lynxPaths) {
			if ((new File(lpath)).exists()) {
				lynxPath = lpath;
				break;
			}
		}
		if (lynxPath == null) {
			System.err.println("WARNING: lynx not in known paths, will leave it to system path");
			lynxPath = "lynx"; // use system's path to find it
		}
		return runLynx(lynxPath, url);
	}

    public static Set<String> runLynx(String path, String url) {
        int buffersize = 40000;
        StringBuffer buffer = new StringBuffer(buffersize);

        try {
        	
            String cmdline[] = {path, "-connect_timeout=20 --dump ", url};
            Process p = Runtime.getRuntime().exec(cmdline);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            char[] cbuf = new char[1];

            while (stdInput.read(cbuf, 0, 1) != -1 || stdError.read(cbuf, 0, 1) != -1) {
                buffer.append(cbuf);
            }
            p.waitFor();
            stdInput.close();
            stdError.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        

        // Remove the References at the end of the dump
        int end = buffer.indexOf("\nReferences\n");

        if (end == -1) {
            end = buffer.length();
        }
        // Remove everything inside [   ] and do not write more than two consecutive spaces
        boolean recording = true;
        boolean wrotespace = false;
        StringBuffer output = new StringBuffer(end);

        for (int i = 0; i < end; i++) {
            if (recording) {
                if (buffer.charAt(i) == '[') {
                    recording = false;
                    if (!wrotespace) {
                        output.append(' ');
                        wrotespace = true;
                    }
                    continue;
                } else {
                    if (Character.isLetter(buffer.charAt(i)) && buffer.charAt(i)<128) {
                        output.append(Character.toLowerCase(buffer.charAt(i)));
                        wrotespace = false;
                    } else {
                        if (!wrotespace) {
                            output.append(' ');
                            wrotespace = true;
                        }
                    }
                }
            } else {
                if (buffer.charAt(i) == ']') {
                    recording = true;
                    continue;
                }
            }
        }
        Set<String> document = new TreeSet<String>();
        StringTokenizer st = new StringTokenizer(output.toString());

        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            //System.out.println(tok);
            document.add(tok);
        }
        return document;
    }

//    public static void main(String args[]) {
//    	String url = "http://connect.diabetes.org/app/render/go.aspx?utm_source=Homepage&utm_medium=FeatureSlider7&utm_content=OnlineCommunity&utm_campaign=COMM&xsl=tp_community.xslt&cons_id=&ts=1287711773&signature=5d1f7973c5a5319053ceabbb0d403c5a";
//
//    	//Set<String> ss = runLynx(args[0]);
//    	Set<String> ss = runLynx(url);
//        for (String s: ss) {
//        	System.out.println(s);
//        }
//    }
}



