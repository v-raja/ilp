package uk.ac.ed.inf;

/**
 * This class is a wrapper of the HttpClient class to make it easier to make requests to the webserver.
 * Ideally, this class should be used through the instance of this class available in the public `instance` field.
 * If the class is used through the instance, the server URL should be set using `setServer`.
 * @author Vivek Raja s1864074
 */
public class WebServerClient {

    /**
     * The URL of the webserver.
     */
    private String serverURL;

    /**
     * An instance of the class. Requests can be made using `WebServerClient.get()`.
     * If you are not using the default host and port, please specify with `setServer()`.
     */
    public static WebServerClient instance = new WebServerClient("localhost", 80);


    /**
     * Instantiates a WebServerClient object which allows you to make calls to the webserver.
     * @param host The machine that hosts the webserver
     * @param port The port the webserver on the host is running on.
     */
    public WebServerClient(String host, int port) {
        setServer(host, port);
    }

    /**
     * Sets the host and port of the webserver to be called when requests are made.
     * @param host The machine that hosts the webserver
     * @param port The port the webserver on the host is running on.
     */
    public void setServer(String host, int port) {
        this.serverURL = String.format("http://%s:%d", host, port);
    }

    /**
     * Makes a HTTP GET request to the webserver at the specified path and returns the body of the response.
     * @param requestPath The path on the server to make a HTTP GET request to.
     */
    public String get(String requestPath) {
        if (this.serverURL == null) {
            System.err.println("You have used the instance without setting the server. Please call `setServer` before trying " +
                    "to make calls to the webserver");
            return null;
        }
        return HttpClient.getRequest(this.serverURL + requestPath);
    }
}
