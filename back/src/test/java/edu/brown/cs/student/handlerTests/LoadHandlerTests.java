package edu.brown.cs.student.handlerTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.LoadHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class LoadHandlerTests {


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
    Spark.get("/loadcsv", new LoadHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  //from sept 21 livecode
  /** The method that unmaps the routes for each endpoint and stops the server gracefully. */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.stop();
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  //stencil from sept 21 livecode
  /**
   * makes endpoint connection - for these tests endpoint will be load
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


  /** checks if a small csv is loaded successfully */
  @Test
  public void loadValidCsvTest() throws IOException {
    //file path from content root, not including data folder since that is restricted in load handler
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=y");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", response.get("result"));
    assertEquals("data/sampleCSVs/sampleCSV1.csv", response.get("filepath"));
    assertEquals("y", response.get("hasHeader"));
    clientConnection.disconnect();
  }


  /** checks if Rhode Island data from spec is loaded and parsed as stated in spec, this csv contains headers */
  @Test
  public void loadRICSVTest() throws IOException {
    //file path from content root, not including data folder since that is restricted in load handler
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=census/riACSData.csv&hasHeader=y");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", response.get("result"));
    assertEquals("data/census/riACSData.csv", response.get("filepath"));
    assertEquals("y", response.get("hasHeader"));
    clientConnection.disconnect();
  }

  /** tests CSV without headers */
  @Test
  public void fileWithOutHeadersTest() throws IOException {
    //file path from content root, not including data folder since that is restricted in load handler
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=n");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", response.get("result"));
    assertEquals("data/sampleCSVs/sampleCSV1.csv", response.get("filepath"));
    assertEquals("n", response.get("hasHeader"));
    clientConnection.disconnect();
  }


  /** too many parameters passed in */
  @Test
  public void tooManyParametersTest() throws IOException {
    //file path from content root, not including data folder since that is restricted in load handler
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=n&hey=hi");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_json: too many parameters passed in", response.get("result"));
    clientConnection.disconnect();
  }


  /** no filepath passed */
  @Test
  public void noFilepathTest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?&hasHeader=n");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));


    assertEquals("error_bad_json: Filepath must be passed in", response.get("result"));

    clientConnection.disconnect();
  }

  /** no header passed */
  @Test
  public void noHeadersTest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));


    assertEquals("error_bad_json: hasHeader must be passed in", response.get("result"));

    clientConnection.disconnect();
  }

  /** filepath is empty */
  @Test
  public void emptyFilepathTest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=&hasHeader=n");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));


    assertEquals("error_datasource: filepath is not valid or unable to be opened", response.get("result"));

    clientConnection.disconnect();
  }

  /** filepath does not exist in folder */
  @Test
  public void invalidFileTest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV100.csv&hasHeader=n");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));


    assertEquals("error_datasource: filepath is not valid or unable to be opened", response.get("result"));

    clientConnection.disconnect();
  }


  /** filepath does not exist in folder that we are restricting to but is contained somewhere else */
  @Test
  public void fileNotContainedTest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/misplacedCSV.csv&hasHeader=n");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));


    assertEquals("error_datasource: filepath is not valid or unable to be opened", response.get("result"));

    clientConnection.disconnect();
  }

  /** loads two csvs files and checks that the last one is not in the loader */
  @Test
  public void loadTwoValidCsvTest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=n");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", response.get("result"));
    HttpURLConnection clientConnection2 = tryRequest("loadcsv?filepath=census/dol_ri_earnings_disparity.csv&hasHeader=y");
    assertEquals(200, clientConnection2.getResponseCode());

    Map resp2 = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals("success", resp2.get("result"));
    assertEquals("data/census/dol_ri_earnings_disparity.csv", resp2.get("filepath"));
    assertNotEquals("data/sampleCSVs/sampleCSV1.csv", resp2.get("filepath"));

    clientConnection.disconnect();
  }
}
