package edu.brown.cs.student.broadbandTests;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.*;
import edu.brown.cs.student.main.Handlers.LoadHandler;
import edu.brown.cs.student.main.api.APICall;
import edu.brown.cs.student.main.Handlers.BroadbandHandler;
import edu.brown.cs.student.main.api.APIResponse;
import edu.brown.cs.student.main.api.BroadbandDatasource;
import okio.Buffer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.*;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BroadbandSourceTest {
	private BroadbandHandler broadbandHandler;
	private static Server server;

	@BeforeAll
	public static void setupOnce() {
		BroadbandDatasource actualSource = new APICall();

		server = new Server(actualSource, 10, "data/geodata/fullDownload.json");

		Logger.getLogger("").setLevel(Level.WARNING);
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

		assertEquals("06", response.data().stateCode);
		assertEquals("059", response.data().countyCode);
		assertEquals("93.0", response.data().percentage);

		loadConnection.disconnect();
	}

	/**
	 * Test case where a valid state and a county that is actually in the state
	 * is passed into the query parameters of the api call. However, the state has a population
	 * less than 65,000, so it is not in the dataset, and should return failure.
	 */
	@Test
	public void testCountyPopulationTooSmall() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"broadband?state=California&county=Colusa%20County,%20California");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		BroadbandHandler.BroadbandFailureResponse response =
				moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());
		assertEquals(
				"java.io.IOException: County has not been included in 2021 broadband dataset, " +
						"population too low",
				response.exception());

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
	 * Test input missing state param but has county param
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

	@Test
	public void apiTest() throws IOException{
		BroadbandDatasource actualSource = new APICall();

		APIResponse res = actualSource.getData(Pair.of("06", "059"));

		assertEquals("06", res.stateCode);
		assertEquals("059", res.countyCode);
		assertEquals("93.0", res.percentage);
	}

	@AfterAll
	public static void setupEnd() {
		server.tearDown();
	}
}
