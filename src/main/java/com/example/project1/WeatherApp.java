/*
Version 1.4 - User Interface Integration
In this version, we've integrated a simple user interface by adding a new method displayWeatherData to the WeatherApp class. 
This method takes the retrieved weather data as input and displays it in a user-friendly format. 
*/
package com.example.project1;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        // Implementation remains unchanged...
        JSONArray locationData = getLocationData(locationName);
        if (locationData == null) {
            return null; // Error handling: Unable to retrieve location data
        }

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = buildApiRequestUrl(latitude, longitude); // ***

        try {
            String resultJsonString = String.valueOf(fetchApiResponse(urlString));
            if (resultJsonString == null) {
                return null; // *** Error handling: API connection failed
            }

            JSONObject resultJsonObj = parseJsonString(resultJsonString);
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            double temperature = extractValue(hourly, "temperature_2m", index);
            long humidity = (long) extractValue(hourly, "relativehumidity_2m", index);
            double windspeed = extractValue(hourly, "windspeed_10m", index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // New method to display weather data
    public static void displayWeatherData(JSONObject weatherData) {
        if (weatherData == null) {
            System.out.println("Unable to retrieve weather data.");
            return;
        }

        double temperature = (double) weatherData.get("temperature");
        long humidity = (long) weatherData.get("humidity");
        double windspeed = (double) weatherData.get("windspeed");

        System.out.println("Weather Information:");
        System.out.println("Temperature: " + temperature + " C");
        System.out.println("Humidity: " + humidity + " %");
        System.out.println("Windspeed: " + windspeed + " m/s");
    }

    // Other methods remain unchanged...

    private static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String buildApiRequestUrl(double latitude, double longitude) {
        return "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject parseJsonString(String jsonString) {
        try {
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }
        return 0;
    }

    private static String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'"));
    }

    private static double extractValue(JSONObject hourly, String key, int index) {
        JSONArray data = (JSONArray) hourly.get(key);
        return (double) data.get(index);
    }
}
