package edu.brown.cs.student.broadbandTests;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.api.APIResponse;
import edu.brown.cs.student.main.api.BroadbandDatasource;
import edu.brown.cs.student.main.Handlers.BroadbandHandler;
import edu.brown.cs.student.main.api.MockedAPICall;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class BroadbandHandlerTest {
	private final Type listOfListString = Types.newParameterizedType(List.class,
			Types.newParameterizedType(List.class, String.class));
	private JsonAdapter<List<List<String>>> listListStringadapter;
	private JsonAdapter<APIResponse> apiResponseAdapter;
	private BroadbandHandler broadbandHandler;

	@BeforeAll
	public static void setupOnce() {
		// Pick an arbitrary free port
		Spark.port(0);
		// Eliminate logger spam in console for test suite
		Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
	}

	@BeforeEach
	public void setup() {
		// Initialize mock state, county lists and mock APIResponse object
		List<List<String>> stateList = new ArrayList<>();
		String[] stateHeaders = {"NAME", "state"};
		List<String> stateHeadersList = new ArrayList<>(Arrays.asList(stateHeaders));
		stateList.add(stateHeadersList);

		String[] stateRow = {"California", "06"};
		List<String> stateRowList = new ArrayList<>(Arrays.asList(stateRow));
		stateList.add(stateRowList);

		List<List<String>> countyList = new ArrayList<>();
		String[] countyHeaders = {"NAME", "state", "county"};
		List<String> countyHeadersList = new ArrayList<>(Arrays.asList(countyHeaders));
		countyList.add(countyHeadersList);

		String[] countyRow = {"Orange County, California", "06", "059"};
		List<String> countyRowList = new ArrayList<>(Arrays.asList(countyRow));
		countyList.add(countyRowList);

		APIResponse response = new APIResponse("06", "059", "18:39:31", "93.0");

		// Use *MOCKED* data when in this test environment.
		// Notice that the WeatherHandler code doesn't need to care whether it has
		// "real" data or "fake" data. Good separation of concerns enables better testing.
		BroadbandDatasource mockedSource = new MockedAPICall(stateList, countyList, response);

		this.broadbandHandler = new BroadbandHandler(mockedSource, 10);

		Spark.get("/broadband", this.broadbandHandler);
		Spark.awaitInitialization(); // don't continue until the server is listening


		// New Moshi adapter for responses (and requests, too; see a few lines below)
		//   For more on this, see the Server gearup.
		Moshi moshi = new Moshi.Builder().build();
		this.listListStringadapter = moshi.adapter(this.listOfListString);

		this.apiResponseAdapter = moshi.adapter(APIResponse.class);
	}

	@AfterEach
	public void tearDown() {
		// Gracefully stop Spark listening on both endpoints
		Spark.unmap("/broadband");
		Spark.stop();
		Spark.awaitStop(); // don't proceed until the server is stopped
	}

	/**
	 * Helper function that is used to establish a HttpConnection to the local api.
	 */
	private HttpURLConnection tryRequest(String apiCall) throws IOException {
		// Configure the connection (but don't actually send a request yet)
		URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
		HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
		// The request body contains a Json object
		clientConnection.setRequestProperty("Content-Type", "application/json");
		// We're expecting a Json object in the response body
		clientConnection.setRequestProperty("Accept", "application/json");

		clientConnection.connect();
		return clientConnection;
	}

	/**
	 * testBasicSuccess: Test case where a valid state and a county that is actually in the state
	 * is passed into the query parameters of the api call.
	 */
	@Test
	public void testBasicSuccess() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"broadband?state=California&county=Orange%20County,%20California");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		BroadbandHandler.BroadbandSuccessResponse response =
				moshi.adapter(BroadbandHandler.BroadbandSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		APIResponse realResponse = new APIResponse("06", "059", "18:39:31", "93.0");

		assertEquals("06", response.data().stateCode);
		assertEquals("059", response.data().countyCode);
		assertEquals("18:39:31", response.data().timestamp);
		assertEquals("93.0", response.data().percentage);

		assertEquals(
				"California",
				response.inputParams().state());
		assertEquals(
				"Orange County, California",
				response.inputParams().county());

		loadConnection.disconnect();
	}

	/**
	 * testCountyNotInState: Test case where a valid state is passed in but the county
	 * passed in is invalid.
	 */
	@Test
	public void testCountyNotInState() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"broadband?state=California&county=Fake%20County,%20California");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		BroadbandHandler.BroadbandFailureResponse response =
				moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());
		assertEquals(
				"Input county: Fake County, California does not exist in California",
				response.exception());
		assertEquals(
				"California",
				response.inputParams().state());
		assertEquals(
				"Fake County, California",
				response.inputParams().county());

		loadConnection.disconnect();
	}

	/**
	 * Test case where a state that is not in the list of states
	 * is passed into the query parameter of state.
	 */
	@Test
	public void testStateDoesNotExist() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"broadband?state=Providence&county=Orange%20County,%20Providence");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		BroadbandHandler.BroadbandFailureResponse response =
				moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());
		assertEquals(
				"Input state: Providence does not exist",
				response.exception());
		assertEquals(
				"Providence",
				response.inputParams().state());
		assertEquals(
				"Orange County, Providence",
				response.inputParams().county());

		loadConnection.disconnect();
	}

	/**
	 *Test input missing state param but has county param
	 */
	@Test
	public void testStateMissingInQuery() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"broadband?county=Orange%20County,%20Providence");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		BroadbandHandler.BroadbandFailureResponse response =
				moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());
		assertEquals(
				"Missing state or county in query",
				response.exception());
		assertNull(response.inputParams().state());
		assertEquals(
				"Orange County, Providence",
				response.inputParams().county());

		loadConnection.disconnect();
	}

	/**
	 * Test input missing county but has state param
	 */
	@Test
	public void testCountyMissingInQuery() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"broadband?state=Providence");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		BroadbandHandler.BroadbandFailureResponse response =
				moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());
		assertEquals(
				"Missing state or county in query",
				response.exception());
		assertNull(response.inputParams().county());
		assertEquals(
				"Providence",
				response.inputParams().state());

		loadConnection.disconnect();
	}

	/**
	 * Test both input params missing
	 */
	@Test
	public void testBothMissingInQuery() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"broadband?");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		BroadbandHandler.BroadbandFailureResponse response =
				moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());
		assertEquals(
				"Missing state or county in query",
				response.exception());
		assertNull(response.inputParams().county());
		assertNull(response.inputParams().state());

		loadConnection.disconnect();
	}

	/**
	 *  Makes a list of states and compares it to the Hashmap
	 * in broadbandHandler that should have been saved after the saveStateCodes function
	 * is called in the constructor of broadbandHandler.
	 */
	@Test
	public void testSaveStateCode() throws IOException {
		List<List<String>> stateList = new ArrayList<>();
		String[] stateRow = {"California", "06"};
		List<String> stateRowList = new ArrayList<>(Arrays.asList(stateRow));
		stateList.add(stateRowList);

		int i = 0;
		for (Map.Entry<String, String> entry : this.broadbandHandler.getStateCodes().entrySet() ) {
			String key = entry.getKey();
			String value = entry.getValue();

			List<String> currRow = stateList.get(i);
			assertEquals(key, currRow.get(0));
			assertEquals(value, currRow.get(1));
			i ++;
		}
	}
}
