package edu.brown.cs.student.handlerTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.LoadHandler;
import edu.brown.cs.student.main.Handlers.SearchHandler;
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

public class SearchHandlerTests {

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
    Spark.get("/searchcsv", new SearchHandler(loadHandler));
    Spark.init();
    Spark.awaitInitialization();
  }

  //from sept 21 livecode
  /** The method that unmaps the routes for each endpoint and stops the server gracefully. */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.unmap("/searchcsv");
    Spark.stop();
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  //stencil from sept 21 livecode
  /**
   * makes endpoint connection - for these tests endpoint will be search
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

    HttpURLConnection clientConnection = tryRequest("searchcsv?columnIndex=1&keyword=hi&headerName=hi");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("Cannot invoke \"edu.brown.cs.student.main.CSVClasses.Parse.parseAndSearchCSV(int, String, String)\" because \"this.loadHandler.parser\" is null", resp.get("exception"));
    assertEquals("error_bad_json", resp.get("result"));

    clientConnection.disconnect();
  }

  /**
   * searching by column index
   */
  @Test
  public void validCSVbyColumnIndexTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=y");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?columnIndex=1&keyword=hi&headerName=");

    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    List<List<String>> parsedRows = new ArrayList<>();
//    List<String> sublist1 = new ArrayList<>();
//    sublist1.add("hi what's up");
//    sublist1.add("hello");
//    sublist1.add("go");
//    sublist1.add("blue");
//    parsedRows.add(sublist1);

    List<String> sublist2 = new ArrayList<>();
    sublist2.add("hi");
    sublist2.add("hi");
    sublist2.add("hi");
    sublist2.add("hi");
    parsedRows.add(sublist2);

    assertEquals("success", resp.get("result"));
    assertEquals(parsedRows, resp.get("data"));

    clientConnection.disconnect();
  }


  /**
   * too many parameters given, need 3
   */
  @Test
  public void tooManyParametersTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=y");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?columnIndex=1&keyword=hi&headerName=hi&gdfd=fdd");

    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals("error_bad_request", resp.get("result"));
    assertEquals("Too many or too little requests are passed in", resp.get("exception"));


    clientConnection.disconnect();
  }

  /**
   * column index does not exist in CSV
   */
  @Test
  public void invalidColumnIndexTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=n");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?columnIndex=100&keyword=hi&headerName=hi");

    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals("error_bad_json", resp.get("result"));


    clientConnection.disconnect();
  }

  /**
   * header does not exist in csv passed in
   */
  @Test
  public void invalidHeaderTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=y");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?columnIndex=-1&keyword=hi&headerName=jjjjjj");

    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals("error_bad_json", resp.get("result"));


    clientConnection.disconnect();
  }


  /**
   * no keyword specified
   */
  @Test
  public void noKeywordTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=y");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?columnIndex=&keyword=&headerName=");

    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals("error_bad_request", resp.get("result"));
    assertEquals("Need keyword input to search in csv", resp.get("exception"));


    clientConnection.disconnect();
  }


  /**
   * searching by header
   */
  @Test
  public void validCSVbyHeaderTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV1.csv&hasHeader=y");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?columnIndex=-1&keyword=hi&headerName=hi");

    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    List<List<String>> parsedRows = new ArrayList<>();
    List<String> sublist1 = new ArrayList<>();
    sublist1.add("hi what's up");
    sublist1.add("hello");
    sublist1.add("go");
    sublist1.add("blue");
    parsedRows.add(sublist1);

    List<String> sublist2 = new ArrayList<>();
    sublist2.add("hi");
    sublist2.add("hi");
    sublist2.add("hi");
    sublist2.add("hi");
    parsedRows.add(sublist2);

    assertEquals("success", resp.get("result"));
    assertEquals(parsedRows, resp.get("data"));

    clientConnection.disconnect();
  }


  /**
   * searching in csv that has no headers, no column index specified to narrow search
   */
  @Test
  public void validCSVWithoutHeadersTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV2.csv&hasHeader=n");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?columnIndex=-1&keyword=10&headerName=");

    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    List<List<String>> parsedRows = new ArrayList<>();
    List<String> sublist1 = new ArrayList<>();
    sublist1.add("10");
    sublist1.add("3");

    parsedRows.add(sublist1);

    assertEquals("success", resp.get("result"));
    assertEquals(parsedRows, resp.get("data"));

    clientConnection.disconnect();
  }


  /**
   * csv has no headers, column index is specified and keyword exists but not in column index specified
   */
  @Test
  public void noResultsTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV2.csv&hasHeader=n");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?columnIndex=1&keyword=10&headerName=");

    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    List<List<String>> parsedRows = new ArrayList<>();

    assertEquals("success", resp.get("result"));
    assertEquals(parsedRows, resp.get("data"));

    clientConnection.disconnect();
  }


  /**
   * keyword does not exist in csv
   */
  @Test
  public void notInCSVTest() throws IOException {

    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=sampleCSVs/sampleCSV2.csv&hasHeader=n");
    assertEquals(200, clientConnection.getResponseCode());
    HttpURLConnection clientConnection2 = tryRequest("searchcsv?columnIndex=1&keyword=michigannn&headerName=");

    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    List<List<String>> parsedRows = new ArrayList<>();

    assertEquals("success", resp.get("result"));
    assertEquals(parsedRows, resp.get("data"));

    clientConnection.disconnect();
  }
}
