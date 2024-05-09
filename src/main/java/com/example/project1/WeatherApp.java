/*
Version 1.3 - Modularity Enhancement
In this version, we've enhanced modularity by refactoring the code into smaller, more focused methods. 
Here's a summary of changes:
1. Introduced buildApiRequestUrl Method
2. Introduced parseJsonString Method
3. Introduced extractValue Method
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
        JSONArray locationData = getLocationData(locationName);
        if (locationData == null) {
            return null;
        }

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = buildApiRequestUrl(latitude, longitude); // ***

        try {
            String resultJsonString = String.valueOf(fetchApiResponse(urlString));
            if (resultJsonString == null) {
                return null;
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

    private static JSONArray getLocationData(String locationName) {
        // Implementation here remains unchanged...
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
        // Implementation remains unchanged...
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
        // Implementation remains unchanged...
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
