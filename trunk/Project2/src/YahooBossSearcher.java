/**
 *  Send Web Query using Yahoo BOSS Search API
 *  
 *  COMS E6111 - Project 2  03/25/2011
 *  
 *  @author Nicole Lee (ncl2108)
 *  @author Laima Tazmin (lt2233)
 */

import java.net.*;
import java.io.*;

public class YahooBossSearcher {
	private String _appid = null;
	private String _urlbase = "http://boss.yahooapis.com/ysearch/web/v1/";

	public YahooBossSearcher (String appid) {
		_appid = appid;
	}

//	public YahooTop10Results search (String term, String site) {
//		YahooTop10Results result = null;
//		try {
//			String termenc = URLEncoder.encode(term,"UTF-8");
//			String url = _urlbase + termenc + "?appid=" + _appid + "&sites=" + site + "&format=json";
//			System.out.println("URL: " + url); // @@@ debug
//			String page = urlGet(url);
//			result = new YahooTop10Results(page);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//	}

	public String search (String term) {
		return search(term, null);
	}
	
	public String search (String term, String site) {
		String page = null;
		try {
			String termenc = URLEncoder.encode("\"" + term + "\"","UTF-8");			
			//String termenc = URLEncoder.encode(term,"UTF-8");
			String url = _urlbase + termenc + "?appid=" + _appid + "&format=json" ;
			if (site != null) { // site-specific search
				url += "&sites=" + site;
			}
			System.out.println("URL: " + url); // @@@ debug
			page = urlGet(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;		
	}
	
	public static String urlGet (String urlstr) throws MalformedURLException, IOException {
		URL url = new URL(urlstr);
		URLConnection urlconn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
        String inputLine;
        StringBuilder sb = new StringBuilder(1000);
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine + "\n");
        }
        in.close();
        return sb.toString();
	}
	
}
