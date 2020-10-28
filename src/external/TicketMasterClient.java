package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TicketMasterClient {
	private static final String HOST = "https://app.ticketmaster.com";
	private static final String PATH = "/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "event";
	private static final int DEFAULT_RADIUS = 50;
	private static final String API_KEY = "ZzqWsJUssj8kKN8gm54PBjix2LtWnFnB";

	public JSONArray search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		
		try {
			// encode keyword content
			keyword = URLEncoder.encode(keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// format resource path query param
		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, DEFAULT_RADIUS);
		String url = HOST + PATH + "?" + query;
		StringBuilder responseBody = new StringBuilder();
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			
			// check out response code, then response
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				return new JSONArray();
			}
			// // reader.read(char) -- too slow
			// on my perspective, the response stream input
			// InputStreamReader reader = new InputStreamReader(connection.getInputStream());
			BufferedReader reader = new BufferedReader (new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				responseBody.append(line);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		try {
			JSONObject obj = new JSONObject(responseBody.toString());
			if (!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return embedded.getJSONArray("events");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JSONArray();
	}
	/**
	 * Main entry to test TicketMasterClient.
	 */
	public static void main(String[] args) {
		TicketMasterClient client = new TicketMasterClient();
		JSONArray events = client.search(34.101155,-118.343727, null);
		try {
		    for (int i = 0; i < events.length(); ++i) {
		       JSONObject event = events.getJSONObject(i);
		       System.out.println(event.toString(2));
		    }
		} catch (Exception e) {
	                  e.printStackTrace();
		}	
	}

}

