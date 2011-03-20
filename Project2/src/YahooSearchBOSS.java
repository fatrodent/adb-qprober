/****
 *  Nicole Lee (ncl2108), Laima Tazmin (lt2233)
 *	E6111 - Project 1
 *	02/24/11
 *	This class requests data from Yahoo Search BOSS API given a search string.
 ****/

import java.net.*;
import java.io.*;

public class YahooSearchBOSS {
	private String _appid = null;
	private String _urlbase = "http://boss.yahooapis.com/ysearch/web/v1/";

	/**
	 * Constructor
	 * @param appid
	 */
	public YahooSearchBOSS (String appid) {
		_appid = appid;
	}
	
	/**
	 * Returns the top 10 results of the query
	 * @param term
	 * @return
	 * @throws MetaSearcherException
	 */
	public YahooTop10Results search (String term) throws MetaSearcherException {
		String result = search(term,"json");
		if (result == null) {
			throw new MetaSearcherException("Your search string '" + term + "' yielded no result.");
		}

		YahooTop10Results results = new YahooTop10Results(result);
		return results;
	}
	
	/**
	 * Searches a query with Yahoo's API
	 * @param term
	 * @param format
	 * @return
	 */
	public String search (String term, String format) {
		String result = null;
		try {
			String termenc = URLEncoder.encode(term,"UTF-8");
			String url = _urlbase + termenc + "?appid=" + _appid + "&format="+format;
			System.out.println("URL: " + url); // @@@ debug
			result = urlGet(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Returns the content of a document given its url
	 * @param urlstr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
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
