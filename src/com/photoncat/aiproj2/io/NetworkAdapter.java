package com.photoncat.aiproj2.io;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The adapter to the p2p gaming server api.
 * http://www.notexponential.com/aip2pgaming/api/index.php
 *
 * The document of the API can be found at:
 * https://docs.google.com/presentation/d/1apI8JBF4FK8nm1xFmj0ud4O51VAJnjkiZxmbIeBqADE/edit#slide=id.g336cc86b3f_0_5
 *
 * Our team ID is 1102 and I'm hard wiring the ID into the code.
 *
 * The API requires an auth whose required information will be loaded from a file therefore won't be check in.
 */
public class NetworkAdapter {
    private final static String SERVER_URL = "http://www.notexponential.com/aip2pgaming/api/index.php";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.exit(0xDEADBEEF);
        }
        File file = new File(args[0]);
        String username = "";
        String password = "";
        String userId = "";
        String apiKey = "";
        try (Scanner scanner = new Scanner(file)){
            username = scanner.nextLine();
            password = scanner.nextLine();
            userId = scanner.nextLine();
            apiKey = scanner.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0x3f3f3f3f);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            System.err.println("Please check your input file format.\n Format: username, password, userId, api key, each in a separate line, without any leading or following spaces.");
            System.exit(987654321);
        }
        String usernameColonPassword = username + ":" + password;
        String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());

        BufferedReader httpResponseReader = null;
        try {
            // Connect to the web server endpoint
            URL serverUrl = new URL(SERVER_URL + "?type=team&teamId=1102");
            HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

            // Set HTTP method as GET
            urlConnection.setRequestMethod("GET");

            // Include the HTTP Basic Authentication payload
            urlConnection.addRequestProperty("Authorization", basicAuthPayload);

            // Include other payloads
            urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.addRequestProperty("userid", userId);
            urlConnection.addRequestProperty("x-api-key", apiKey);

            // Read response from web server, which will trigger HTTP Basic Authentication request to be sent.
            httpResponseReader =
                    new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String lineRead;
            while((lineRead = httpResponseReader.readLine()) != null) {
                System.out.println(lineRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpResponseReader != null) {
                try {
                    httpResponseReader.close();
                } catch (IOException e) {
                    // Close quietly
                }
            }
        }
    }
}
