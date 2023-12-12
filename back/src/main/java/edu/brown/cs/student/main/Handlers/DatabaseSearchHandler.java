package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
// import edu.brown.cs.student.main.server.source.CensusSource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


public class BroadbandHandler implements Route {

  /**
   * Adapted from the testing code. Helper method to start a connection to a specific API endpoint.
   * Modified signature to take in a callRequest string to set the request method to use either GET
   * or POST.
   *
   * @param apiCall the call string for the API endpoint
   * @return the connection to the API endpoint
   * @throws IOException if there is an error connecting to the API endpoint
   */
  private static HttpURLConnection tryRequest(String apiCall, String callRequest) throws IOException {
    URL request = new URL("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*");
    HttpURLConnection connection = (HttpURLConnection) request.openConnection();
    connection.setRequestMethod(callRequest);
    connection.connect();

    if (connection.getResponseCode() != 200) {
      throw new RuntimeException("HttpResponseCode: " + connection.getResponseCode());
    }
    return connection;
  }

  private List<List<String>> searchDatabase(String keyword, String country) {

    // check nullness too
    keyword = keyword.toLowerCase();
    if (country != null) {
      country = country.toLowerCase();
    }

    return new ArrayList<String>;

    // String stateID = stateCodes.get(state);
    // String countyId = "";
    // if (county != null) {
    //   countyId = countyCodes.get(state).get(county);
    // }

    // // Variable to store the broadband data
    // List<List<String>> broadbandData = new ArrayList<>();

    // Moshi moshi = new Moshi.Builder().build();
    // Type mapCensus = Types.newParameterizedType(List.class, List.class, String.class);
    // JsonAdapter<List<List<String>>> adapter = moshi.adapter(mapCensus);
    // Map<String, Object> responses = new HashMap<>();

    // try {
    //   // https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:* from EdStem
    //   // Api key 95c215370b6aa167d5a8a7a987f913613c0edd2c
    //   URL url = null;
    //   if (county == null) {
    //     url =
    //         new URL(
    //             "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:"
    //                 + stateID);
    //   } else if (county != null) {
    //     url =
    //         new URL(
    //             "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
    //                 + countyId
    //                 + "&in=state:"
    //                 + stateID);
    //   }
    //   HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    //   connection.setRequestMethod("GET");
    //   connection.connect();

    //   if (connection.getResponseCode() != 200) {
    //     // Throw an error instead!
    //     return null;
    //   }

    //   try (Buffer buffer = new Buffer().readFrom(connection.getInputStream())) {
    //     List<List<String>> stateResponses = adapter.fromJson(buffer);
    //     if (stateResponses != null && !stateResponses.isEmpty()) {
    //       responses.put("status", "success");
    //       responses.put("data", stateResponses);
    //       broadbandData.addAll(stateResponses);
    //     }
    //   }
    //   connection.disconnect();
    // } catch (Exception e) {}
    // return broadbandData;
  }



  // Maps the state codes to their names by calling the 2010 census API
  /**
   * Adapted from the other handler classes. Expected Behavior: 1. Calls the methods to map the
   * state and county codes to their names 2. Makes a call to the 2021 census API to get the
   * broadband data for the state and county 3. Stores the broadband data in a list of lists 4.
   * Returns the broadband data
   *
   * @param request The request to handle
   * @param response The response object providing functionality for modifying the response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) {
    // Query parameters for the API call, county code is optional
    String keyword = request.queryParams("keyword");
    String country = request.queryParams("country");

    if ((keyword == null) && (country == null)) {
      response.status(400);
      // some sort of error
      return "No parameters provided";
    }


    // TODO: CHANGE NAME
    List<List<String>> searchData = this.searchDatabase(keyword, country);
    if (searchData == null) {
      return "Cannot connect to source";
    }

    Moshi moshi = new Moshi.Builder().build();
    Type mapCensus = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapCensus);
    Map<String, Object> responses = new HashMap<>();

    // // ADDING TIME TO LIST!
    // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    // LocalDateTime now = LocalDateTime.now();
    // String time = dtf.format(now);

    // List<String> time_lst = new ArrayList<>();
    // time_lst.add(time);

    responses.put("status", "success");
    // responses.put("time", time_lst);
    responses.put("result", searchData);
    response.status(200);
    return adapter.toJson(responses);
  }
}