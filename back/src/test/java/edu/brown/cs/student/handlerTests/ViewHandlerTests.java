package edu.brown.cs.student.handlerTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.LoadHandler;
import edu.brown.cs.student.main.Handlers.ViewHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class ViewHandlerTests {

  //stencil from sept 21 livecode
  /** Method to set up port and logger before all testing occurs */
  @BeforeAll
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }


  //stencil from sept 21 livecode
  /**
   * setup endpoints before each test
   */
  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method
    LoadHandler loadHandler = new LoadHandler();
    //load handler must be called before view handler so a file can be loaded
    Spark.get("/loadcsv", loadHandler);
    Spark.get("/viewcsv", new ViewHandler(loadHandler));
    Spark.init();
    Spark.awaitInitialization();
  }

  //from sept 21 livecode
  /** The method that unmaps the routes for each endpoint and stops the server gracefully. */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.unmap("/viewcsv");
    Spark.stop();
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  //stencil from sept 21 livecode
  /**
   * makes endpoint connection - for these tests endpoint will be view
   * @param parameters - query parameters and load csv endpoint
   * @return the connection for the given URL, just after connecting
   * @throws IOException when connection fails
   */
  private HttpURLConnection tryRequest(String parameters) throws IOException {
    // making request URL with port and endpoint
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + parameters);
    HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("Accept", "application/json");

    connection.connect();
    return connection;
  }

  /**
   * did not load file in load handler, should error
   */
  @Test
  public void noCallToLoadTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("viewcsv");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request: loadcsv endpoint must be called first", resp.get("result"));
    clientConnection.disconnect();
  }

  /**
   * gave a parameter when viewHandler does not take in any
   */
  @Test
  public void invalidInputTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("viewcsv?hey=hi");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request: no parameters can be passed in", resp.get("result"));
    clientConnection.disconnect();
  }


  /** gets the contents of a small csv with no header */
  @Test
  public void viewValidCsvTest() throws IOException {
    //file path from content root, not including data folder since that is restricted in load handler
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV2.csv&hasHeader=n");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("viewcsv");
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    List<List<String>> parsedRows = new ArrayList<>();
    List<String> sublist1 = new ArrayList<>();
    sublist1.add("10");
    sublist1.add("3");
    parsedRows.add(sublist1);

    assertEquals("success", response.get("result"));
    assertEquals(parsedRows, response.get("data"));
    clientConnection.disconnect();
  }


  /** gets the contents of a small csv with a header */
  @Test
  public void viewValidCsvWithHeaderTest() throws IOException {
    //file path from content root, not including data folder since that is restricted in load handler
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV3.csv&hasHeader=y");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("viewcsv");
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    List<List<String>> parsedRows = new ArrayList<>();
    List<String> sublist1 = new ArrayList<>();
    sublist1.add("1");
    sublist1.add("2");
    parsedRows.add(sublist1);

    assertEquals("success", response.get("result"));
    assertEquals(parsedRows, response.get("data"));
    clientConnection.disconnect();
  }

  /** gets the contents of a csv that is empty */
  @Test
  public void emptyCSVTest() throws IOException {
    //file path from content root, not including data folder since that is restricted in load handler
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/emptyCSV.csv&hasHeader=n");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("viewcsv");
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    List<List<String>> parsedRows = new ArrayList<>();

    assertEquals("success", response.get("result"));
    assertEquals(parsedRows, response.get("data"));
    clientConnection.disconnect();
  }
}
