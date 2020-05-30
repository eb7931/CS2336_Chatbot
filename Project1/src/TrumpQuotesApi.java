import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TrumpQuotesApi{
	public TrumpQuote quote;
	public String toString() {
		if(isValid()) {
			return quote.toString();
		}
		return getTrumpQuote().toString();
	}
		public static String getJSON(String address)throws IOException{
		URL url = new URL(address);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
		    content.append(inputLine);
			//System.out.println(inputLine);
		}
		in.close();
		con.disconnect();
		return content.toString();
	}
	public boolean isValid() {
		if(quote == null) {
			return false;
		}
		else if(quote.code == 404) {
			return false;
		}
		else {
			return true;
		}
	}
	public class TrumpQuote{
		String value;
		String appeared_at;
		int code = 0;
		public TrumpQuote(){}
		public TrumpQuote(int cod) {
			code = 404;
		}
		public String toString() {
			return "Trump once said \"" + value + 
					"\" on " + getDate();
		}
		public String getDate() {
			if(isValid()) {
				String temp = "";
				for(int i = 0; i <10; i++) {
					temp += appeared_at.charAt(i);
				}
				return temp;
			}
			return "No quote, no date. Sorry bub.";
		}
		public boolean isValid() {
			if(code == 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	public void setCode(int cod) {
		quote.code = cod;
	}
	TrumpQuote getTrumpQuote() {
		try {
			String url =  "https://api.tronalddump.io/random/quote";
			Gson gson = new GsonBuilder().create();
			String reader = getJSON(url);
			TrumpQuote trumpQuote = gson.fromJson(reader, TrumpQuote.class);
			trumpQuote.code = 0;
			return trumpQuote;
		}
		catch(IOException e) {
			System.out.println("couldn't get a quote");			
			return new TrumpQuote(404);
		}
	}
	TrumpQuotesApi(){
		quote = getTrumpQuote();
	}
}