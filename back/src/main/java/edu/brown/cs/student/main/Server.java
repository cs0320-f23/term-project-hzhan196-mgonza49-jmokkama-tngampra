package edu.brown.cs.student.main;

import static spark.Spark.after;

import edu.brown.cs.student.main.Handlers.*;
import edu.brown.cs.student.main.api.APICall;
import edu.brown.cs.student.main.api.BroadbandDatasource;
import spark.Spark;

//stencil taken from gearup code
/**
 * Top-level class for this project. Contains the main() method which starts Spark and runs the various handlers.
 * Uses dependency injections to facilitate mock testing.
 */
public class Server {
    static final int port = 3232;
    private final BroadbandDatasource apiCall;

    /**
     * Constructor to set up Spark endpoints for api calls.
     * @param toUse - Either mock or ACS datasource object that makes API calls
     * @param cacheSize - size of cache
     */
    public Server(BroadbandDatasource toUse, int cacheSize, String path) {
        apiCall = toUse;

        Spark.port(port);
        /*
            Setting CORS headers to allow cross-origin requests from the client; this is necessary for the client to
            be able to make requests to the server.

            By setting the Access-Control-Allow-Origin header to "*", we allow requests from any origin.
            This is not a good idea in real-world applications, since it opens up your server to cross-origin requests
            from any website. Instead, you should set this header to the origin of your client, or a list of origins
            that you trust.

            By setting the Access-Control-Allow-Methods header to "*", we allow requests with any HTTP method.
            Again, it's generally better to be more specific here and only allow the methods you need, but for
            this demo we'll allow all methods.

            We recommend you learn more about CORS with these resources:
                - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
                - https://portswigger.net/web-security/cors
         */
        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "*");
        });

        // Setting up the handler for the GET /load and /view endpoints
        LoadHandler loadHandler = new LoadHandler();
        BroadbandHandler broadbandHandler = new BroadbandHandler(apiCall, cacheSize);

        Spark.get("loadcsv", loadHandler);
        Spark.get("viewcsv", new ViewHandler(loadHandler));
        Spark.get("broadband", broadbandHandler);
        Spark.get("redline", new RedLineHandler(path));
        Spark.get("searchprograms", new DatabaseSearchHandler());

        Spark.init();
        Spark.awaitInitialization();

        // Notice this link alone leads to a 404... Why is that?
        System.out.println("Server started at http://localhost:" + port);
    }

    /**
     * Main method to create server
     */
    public static void main(String[] args) {
        //starts port server
        Server server = new Server(new APICall(), 10, "./back/data/geodata/fullDownload.json");
    }

    /**
     * Takes down all endpoints mapped, used at end of testing.
     */
    public void tearDown() {
        Spark.unmap("/loadcsv");
        Spark.unmap("/searchcsv");
        Spark.unmap("/viewcsv");
        Spark.unmap("/broadband");
        Spark.unmap("/redline");
        Spark.stop();
        Spark.awaitStop(); // don't proceed until the server is stopped
    }
}
