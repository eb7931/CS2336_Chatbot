import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.pircbot.*;

public class MyBot extends PircBot {
	private static final String url = "irc.freenode.net";
	private static final String channel = "#pircbot7931";
	public String getChannel() {return channel;}
	public String getUrl() {return url;}
	public boolean waitingForLocation = false;
	public boolean waitingForHumid = false;
	public WeatherApi weather;
	public TrumpQuotesApi quote = new TrumpQuotesApi();
	public MyBot(){
		this("MyBot_7931");
	}
	public MyBot(String name) {
		this.setName(name);
		this.setVerbose(true);
		try {
			this.connect(url);
		}
		catch(Exception e) {
			connectWithNewName(name);
		}
		joinChannel(channel);
		sendMessage(channel, "Good day all! " + getName() + " the bot has joined!");
	}
	public void onMessage(String c, String sender, String login, String hostname, String message){
		if(!message.contains("MyBot")) {
			sendMessage(channel, "I saw that message, my friend!");
			if(isRelatedToWeather(message)) {
				weatherResponse(message);
			}
			else if(message.contains("humid")||waitingForHumid) {
				humidityResponse(message);
			}
			else if(isRelatedToTrump(message)) {
				sendMessage(channel, quote.toString());
			}
		}
		
	}
	public boolean isRelatedToTrump(String message) {
		if(message.contains("trump")) {
			return true;
		}
		if(message.contains("donald")) {
			return true;
		}
		if(message.contains("tronald")) {
			return true;
		}
		if(message.contains("president")) {
			return true;
		}
		return false;
	}
	public boolean isRelatedToWeather(String message) {
		if(message.contains("weather"))
			return true;
		if(message.contains("forecast"))
			return true;
		if(message.contains("temperature"))
			return true;
		if(waitingForLocation) {
			return true;
		}
		return false;
	}
	public void humidityResponse(String message) {
		if(message.contains("humid")) {
			if(containsZip(message)) {
				try {
					weather = new WeatherApi(zip(message));
					sendMessage(channel, "Here's the humidity in " + zip(message));
					sendMessage(channel, weather.humidity(zip(message)));
					waitingForHumid = false;
				}
				catch(Exception e) {
					sendMessage(channel, "Couldn't get the humidity at " + zip(message) + ", try another");
					waitingForHumid = true;
				}
			}
			else {
				sendMessage(channel, "Where do you need the humidity?");
				waitingForHumid = true;
			}
			return;
		}
		
		if(waitingForHumid) {
			try {
				if(containsZip(message)) {
					weather = new WeatherApi(zip(message));
					sendMessage(channel, "Here's the humidity at " + zip(message));
					sendMessage(channel, weather.humidity(message));
					waitingForHumid = false;
				}
				else {

					weather = new WeatherApi(message);
					sendMessage(channel, "Here's the humidity at " + message);
					sendMessage(channel, weather.humidity(message));
					waitingForHumid = false;
					
				}
			}
			catch(Exception e) {
				sendMessage(channel, "Couldn't get the humidity at " + message + ", try another");
				waitingForHumid = true;
			}
		}
		
	}
	public void weatherResponse(String message) {
		if(message.contains("weather")) {
			if(containsZip(message)) {
				try {
					weather = new WeatherApi(zip(message));
					sendMessage(channel, "Here's the weather in " + zip(message));
					sendMessage(channel, weather.weather());
					waitingForLocation = false;
				}
				catch(Exception e) {
					sendMessage(channel, "Couldn't get the weather at " + zip(message) + ", try another");
					waitingForLocation = true;
				}
			}
			else {
				sendMessage(channel, "Where do you need the weather?");
				waitingForLocation = true;
			}
			return;
		}
		
		if(waitingForLocation) {
			try {
				if(containsZip(message)) {
					weather = new WeatherApi(zip(message));
					sendMessage(channel, "Here's the weather at " + zip(message));
					sendMessage(channel, weather.weather());
					waitingForLocation = false;
				}
				else {

					sendMessage(channel, "aww, didn't recognize zip");
					weather = new WeatherApi(message);
					sendMessage(channel, "Here's the weather at " + message);
					sendMessage(channel, weather.weather());
					waitingForLocation = false;
					
				}
			}
			catch(Exception e) {
				sendMessage(channel, "Couldn't get the weather at " + message + ", try another");
				waitingForLocation = true;
			}
		}
	}
	public boolean containsZip(String message) {
		Pattern p = Pattern.compile("([0-9]{5})");   // the pattern to search for
	    Matcher m = p.matcher(message);
	    if (m.find())
	    {
	      return true;
	    }
		return false;
	}
	public String zip(String message) {
		Pattern p = Pattern.compile("([0-9]{5})");   // the pattern to search for
	    Matcher m = p.matcher(message);
	    String zip = "00000";
	    // if we find a match, get the group 
	    if (m.find())
	    {
	      // we're only looking for one group, so get it
	      zip = m.group(1);
	      // print the group out for verification
	    }
	    System.out.println(zip+"_");
	    return zip;
	}
	public void connectWithNewName(String name) {
		try {
			this.setName(name + "-");
			this.connect(url);
		}
		catch(Exception e) {
			connectWithNewName(getName());
		}
	}
}