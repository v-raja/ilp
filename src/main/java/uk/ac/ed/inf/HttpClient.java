package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This class consists of static methods to make HTTP requests and handle their responses. Currently, only GET requests
 * are supported.
 * @author Vivek Raja s1864074
 */
public class HttpClient {

    /**
     * The instantiation of an HTTP client used to make HTTP requests.
     */
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

    /**
     * Makes an HTTP GET request to the specified `requestUrl`. If any exception is encountered while making the
     * request, the method prints the error to screen and exits with status 1.
     * @param requestUrl The url to make an HTTP GET request to.
     * @return the body of the response of a successful request.
     */
    public static String getRequest(String requestUrl) {
        // The instantiation of the HttpRequest object which contains details of the request we'd like to make
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .build();

        try {
            // The response from the server
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            switch (response.statusCode()) {
                case 200:
                    // Successful request so return the body.
                    return response.body();
                case 404:
                    System.err.println("Path not found on server. Code: 404. Request URL: " + requestUrl + ".");
                    System.exit(1); // Exit the application
                default:
                    System.err.println("Error encountered in GET request. Code: " + response.statusCode() + ". Request URL: " + requestUrl + ".");
                    System.exit(1); // Exit the application
            }
        } catch (java.net.ConnectException e) {
            System.err.println("Fatal error: Unable to connect to " +
                    requestUrl + ". Error message: " + e.getMessage() + ".");
            System.exit(1); // Exit the application
        } catch (IOException e) {
            System.err.println("Fatal error: Unable to connect to " +
                    requestUrl + " due to IO Exception. Error message: " + e.getMessage() + ".");
            System.exit(1); // Exit the application
        } catch (InterruptedException e) {
            System.err.println("Request interrupted by user. Error message: " + e.getMessage() + ".");
            System.exit(1); // Exit the application
        }

        // We won't ever get here since we always `System.exit(1)` for any error.
        return null;
    }
}
