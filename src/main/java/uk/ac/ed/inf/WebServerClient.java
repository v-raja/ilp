package uk.ac.ed.inf;

public class WebServerClient {
    private String serverURL;
    public static WebServerClient instance = new WebServerClient();

    public void setServer(String host, int port) {
        this.serverURL = String.format("http://%s:%d", host, port);
    }

    public String get(String requestPath) {
        if (this.serverURL == null) {
            System.out.println("You have used the instance without setting the server. Please call `setServer` before trying " +
                    "to make calls to the webserver");
            return null;
        }
        return HttpClient.getRequest(this.serverURL + requestPath);
    }
}
