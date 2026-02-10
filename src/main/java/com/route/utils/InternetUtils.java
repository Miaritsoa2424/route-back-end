package com.route.utils;

import java.net.HttpURLConnection;
import java.net.URL;

public class InternetUtils {

    public static boolean hasInternetAccess() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000); // 3 secondes
            connection.setReadTimeout(3000);
            connection.setRequestMethod("HEAD");
            connection.connect();

            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode < 400);

        } catch (Exception e) {
            return false;
        }
    }
}
