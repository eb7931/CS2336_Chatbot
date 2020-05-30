import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WeatherApi{
		private
		CurrentWeather currentWeather = null;
		Forecast forecast = null;
		public String searchTerm;		
		WeatherApi() throws Exception {this("junk search term to trigger 404");}
		WeatherApi(String location) throws Exception{
			setLocation(location);
		}
		public String humidity(String location) {
			try {
				setLocation(location);
				String humid = currentWeather.getHumidity();
				return "Humidity in " + location + " is currently " + humid + "%";
			}
			catch(Exception e){
				return "Couldn't get humidity for " + location + ".";
			}
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
			}
			in.close();
			con.disconnect();
			return content.toString();
		}
		public String weather() {
			return currentWeather.toString();
		}
		public String weather(int i) {
			return forecast.toString(i);
		}
		public void setLocation(String location) throws Exception{
			searchTerm = location;
			updateWeather();
			valid();
			
		}
		private void valid() throws Exception {
			if(currentWeather.cod == 404) {
				throw new Exception();
			}
			if(forecast.cod == "404") {
				throw new Exception();
			}
		}
		private void updateWeather() {
			currentWeather = CurrentWeather.getCurrentWeather(searchTerm);
			forecast = Forecast.getForecast(searchTerm);
		}

//-------Here starts the models and containers for the current weather in the weather api
		
		//class which is used to contain data gathered in call
		//to current weather api
		public static class CurrentWeather extends City{
			int cod;
			Sys sys;
			Clouds clouds;
			Wind wind;
			public Main main;
			Weather weather[];
			double toFahrenheit(double k) {
				return (k-273.15)*9/5 + 32;
			}
			public String getHumidity() {
				if(cod != 404)
				return new DecimalFormat("#.##").format(main.humidity);
				return "couldn't get humidity data";
			}
			public String toString() {
				if(cod == 404) {
					return "No valid weather data to return";
				}
				return "The temperature in " + getCity()
						+ " is " + getTemp() + " with a high of " + getTempMax() + " and low of " + getTempMin() + ".";
			}
			String getCity() {
				if(cod == 404) {
					return "no city available";
				}
				return name;
			}
			String getTempMin() {
				return getTempMin(0);
			}
			String getTempMin(int i) {
				try {
					if(cod == 404) {
						return "No valid min temp";
					}
					double temp = toFahrenheit(main.temp_min);
					return new DecimalFormat("#.##").format(temp);
				}
				catch(NullPointerException e) {
					return "Null pointer detected";
				}
			}
			String getTempMax() {
				return getTempMax(0);
			}
			String getTempMax(int i) {
				try {
					if(cod == 404) {
						return "No valid max temp";
					}
					double temp = toFahrenheit(main.temp_max);
					return new DecimalFormat("#.##").format(temp);
				}
				catch(NullPointerException e) {
					return "Null pointer detected";
				}
			}
			String getTemp() {
				return getTemp(0);
			}
			String getTemp(int i) {
				try {
					if(cod == 404) {
						return "No valid temp";
					}
					double temp = toFahrenheit(main.temp);
					return new DecimalFormat("#.##").format(temp);
				}
				catch(NullPointerException e) {
					return "Null pointer detected";
				}
			}
			
			//I decided to use a factory method so as to prevent unintended instantiation where necessary
			//information or methods of marking the returned object as invalid
			static CurrentWeather getCurrentWeather(String location) {
				try {
				String url =  "http://api.openweathermap.org/data/2.5/weather?q=";
				url += location;
				url += ",us&APPID=4fef0cfc5fe03402e5dc30320faf243d";
				Gson gson = new GsonBuilder().create();
				String reader = getJSON(url);
				CurrentWeather weather = gson.fromJson(reader, CurrentWeather.class);
				return weather;
				}
				catch(IOException e) {
					System.out.println("could not initialize CurrentWeather");
				}
				return new CurrentWeather(404);
			}
			private CurrentWeather() {}
			private CurrentWeather(int code) {cod = code;}
		}
		
		//a model for sys in the current weather forecast of the weather api
		public static class Sys{
			double type;
			double id;
			double message;
			String country;
			double sunrise;
			double sunset;
		}

//-------Here starts the models and containers for the 3 day forecast in the weather api		
		
		//this is the container being used to capture any potentially relevant or useful
		//data from the 3 day forecast in the weather api
		public static class Forecast{
			// important to note that the api for openweathermap
			//returns the weather for the next many 3 hour periods and begins
			//with the period ending in a multiple of 3 hours and contains
			//the current time, show in the UTC +0000 hour time zone
			
			public
			String cod;
			double message;
			int cnt;
			List list[];
			City city;
			

			String getCity() {
				if(cod == "404") {
					return "no city available";
				}
				return city.name;
			}
			double toFahrenheit(double k) {
				return (k-273.15)*9/5 + 32;
			}
			public String toString() {
				return toString(0);
			}
			public String toString(int i) {
				int j = 0;
				int hours = (Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
				i += 3-hours%3;
				j = i/3 - 1;
				int startTime = ((hours + i)-(hours + 1)%3)%24;
				int endTime = (startTime + 3)%24;
				if(cod == "404") {
					return "No valid weather data to return";
				}
				return "The temperature in " + getCity() + " from " + startTime + " to " + endTime
						+ " is " + getTemp(j) + " with a high of " + getTempMax(j) + " and low of " + getTempMin(j) + ".";
			}

			String getTempMin() {
				return getTempMin(0);
			}
			String getTempMin(int i) {
				if(cod == "404") {
					return "No valid min temp";
				}
				double temp = toFahrenheit(list[i].main.temp_min);
				return new DecimalFormat("#.##").format(temp);
			}
			String getTempMax() {
				return getTempMax(0);
			}
			String getTempMax(int i) {
				if(cod == "404") {
					return "No valid max temp";
				}
				double temp = toFahrenheit(list[i].main.temp_max);
				return new DecimalFormat("#.##").format(temp);
			}
			String getTemp() {
				return getTemp(0);
			}
			String getTemp(int i) {
				if(cod == "404") {
					return "No valid temp";
				}
				double temp = toFahrenheit(list[i].main.temp);
				return new DecimalFormat("#.##").format(temp);
			}
			static Forecast getForecast(String location) {
				try {
					String url =  "http://api.openweathermap.org/data/2.5/forecast?q=";
					url += location;
					url += ",us&APPID=4fef0cfc5fe03402e5dc30320faf243d";
					Gson gson = new GsonBuilder().create();
					String reader = getJSON(url);
					Forecast weather = gson.fromJson(reader, Forecast.class);
					return weather;
				}
				catch(IOException e) {
					System.out.println("could not initialize Forecast");
				}

			return new Forecast("404");
			}
			private Forecast(String code) {cod = code;}
			private Forecast() {}
		}	
		

		//a model for rain under list in the 3 day forecast of the weather api
		public static class Rain{
			public double rain;
		}
		
		//a model for list in the 3 day forecast of the weather api
		public static class List{
			Main main;
			Weather weather[];
			Clouds clouds;
			Wind wind;
			Rain rain;
			String dt_txt;
		}

		//a model for weather under list in the 3 day forecast of the weather api
		public static class Weather{
			public
			double id;
			String main;
			String description;
			String icon;
		}
		
		//a model for main under list in the 3 day forecast of the weather api
		public static class Main{
		
			public double temp = 0;
			public double temp_min = 0;
			public double temp_max = 0;
			public double pressure;
			public double sea_level;
			public double grnd_level;
			public double humidity;
			public double temp_kf;
		}
		
		//a model for cloud under list in the 3 day forecast of the weather api
		public static class Clouds{
			public double all;
		}
		
		//a model for wind under list in the 3 day forecast of the weather api
		public static class Wind{
			public
			double speed;
			double deg;
		}
		
		//a model for city found in root of current weather and under list of the 3 day forecast
		public static class City{
			double id;
			String name;
			Coord coord;
			String country;
		}
		
		//a model for coord found in city in current weather and list of the 3 day forecast
		public static class Coord{
			double lat;
			double lon;
		}
	}
/*some deprecated code that I had added that I thought may be useful
just putting it down here in case I want it later


		private CurrentWeather getCurrentWeather(){
			return currentWeather;
		}
		private Forecast getForecast() {
			return forecast;
		}
		private CurrentWeather getCurrentWeather(String location) throws Exception{
			setLocation(location);
			return currentWeather;
		}
		private Forecast getForecast(String location) throws Exception{
			setLocation(location);
			return forecast;
		}

		public static class InvalidDataException extends Exception{
			
		}





*/