package edu.brown.cs.student.main.api;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import okio.Buffer;
import java.time.LocalTime;
import org.apache.commons.lang3.tuple.Pair;


/**
 * Class that makes API calls to the ACS API
 */
public class APICall implements BroadbandDatasource {
  /**
   * connect - Helper function that takes in a URL object and creates a HttpURLConnection
   * @param requestURL - the URL to make the API request to
   * @throws IOException - throws exception when Http Connection is unsuccessful.
   * @return a successful HttpUrlConnection to the URL in case of success.
   */
  public HttpURLConnection connect(URL requestURL) throws IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if(! (urlConnection instanceof HttpURLConnection))
      throw new IOException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if(clientConnection.getResponseCode() != 200){
      System.out.println(clientConnection.getResponseCode());

      // 204 code happens when county is in 2010 data set but not in 2021 data set
      if(clientConnection.getResponseCode() == 204){
        throw new IOException("County has not been included in 2021 broadband dataset, population too low");
      }

      throw new IOException("unexpected: API connection not success status "+clientConnection.getResponseMessage());}
    return clientConnection;
  }

  /**
   * getData - Function that gets the broadband data
   * @param query_in - A pair containing the state and county Strings.
   * @throws IOException - throws exception when Http Connection is unsuccessful.
   * @return if successful, an APIResponse object containing the data from the API call.
   */
  public APIResponse getData(Pair<String,String> query_in) throws IOException {
    String countyCode = query_in.getRight();
    String stateCode = query_in.getLeft();

    URL requestURL = new URL("https", "api.census.gov",
        "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E"
            + "&for=county:" + countyCode + "&in=state:" + stateCode);
    HttpURLConnection clientConnection = connect(requestURL);
    Moshi moshi = new Moshi.Builder().build();
    Type listOfListString = Types.newParameterizedType(List.class,
        Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listOfListString);
    // NOTE: important! pattern for handling the input stream
    List<List<String>> body = adapter.fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();

    try {
      //defensive programming - checks whether there is nothing returned from API
      if (body == null || body.isEmpty()) {
        throw new IOException("County and state pair is not found in API");
      }

      //if only header is returned
      if (body.size() >= 2) {
        //list of header and data is returned
        List<String> result = body.get(1);
        String percentageResult = result.get(1);
        String currentTime = LocalTime.now().toString();

        return new APIResponse(stateCode, countyCode, currentTime, percentageResult);
      } else {
        throw new IOException("County and state pair is not found in API");
      }
    } catch (IOException | NullPointerException e) {
      throw new IOException("County and state pair is not found in API");
    }
  }

  /**
   * getStateCodeList - Function that gets the state codes through
   *                      API Call and saves them.
   * @throws IOException - throws exception when Http Connection is unsuccessful.
   * @return if successful, a list of string lists containing the data.
   */
  public List<List<String>> getStateCodeList() throws IOException {
    URL requestURL = new URL("https", "api.census.gov",
            "/data/2010/dec/sf1?get=NAME&for=state:*");
    HttpURLConnection clientConnection = connect(requestURL);
    Moshi moshi = new Moshi.Builder().build();

    Type listOfListString = Types.newParameterizedType(List.class,
            Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listOfListString);

    // NOTE: important! pattern for handling the input stream
    List<List<String>> body = adapter.fromJson(
            new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    return body;
  }

  /**
   * getStateCodeList - Function that gets the county codes through API calls
   * @param state - The state to get county code for
   * @param stateCodes_in - A hashmap mapping state to state codes
   * @throws IOException - throws exception when Http Connection is unsuccessful.
   * @return if successful, a list of string lists containing the data.
   */
  public List<List<String>> getCountyCodeList(String state, HashMap<String, String> stateCodes_in) throws IOException {
    String stateCode = stateCodes_in.get(state);
    if (stateCode == null) {
      throw new IOException("Input state:" + state + " does not exist");
    }

    URL requestURL = new URL("https", "api.census.gov",
            "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);
    HttpURLConnection clientConnection = connect(requestURL);
    Moshi moshi = new Moshi.Builder().build();

    Type listOfListString = Types.newParameterizedType(List.class,
            Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listOfListString);

    // NOTE: important! pattern for handling the input stream
    List<List<String>> body = adapter.fromJson(
            new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();

    return body;
  }
}
