package com.amagh.bakemate.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Helper class for connecting to the network and retrieving the response
 */

public class NetworkUtils {

    /**
     * Connects to the site and retrieves the document for the link.
     *
     * @param urlString String from of the URL to connect and download
     * @return String containing the contents of the document
     * @throws IOException If the URL is Malformed or there is an error connecting to the web page
     */
    public static String getHttpResponse(String urlString) throws IOException {
        // Create a URL Object from the input urlString
        URL url = new URL(urlString);

        // Initialized outside of try-block to close in finally
        HttpURLConnection urlConnection = null;

        try {
            // Open a connection to the URL
            urlConnection = (HttpURLConnection) url.openConnection();

            // Init Scanner to parse the document
            Scanner scanner = new Scanner(urlConnection.getInputStream());
            scanner.useDelimiter("\\A");

            // Init the reponse to return
            String response = null;

            // Check if Scanner has a valid next()
            if (scanner.hasNext()) {
                // Set the return response
                response = scanner.next();
            }

            // Close the Scanner and InputStream
            scanner.close();

            return response;
        } finally {
            // Close the connection if it is valid
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


}
