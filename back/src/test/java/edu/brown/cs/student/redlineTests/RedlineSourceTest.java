package edu.brown.cs.student.redlineTests;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Handlers.RedLineHandler;
import edu.brown.cs.student.main.RedlineObjects.Filterer;
import edu.brown.cs.student.main.api.APICall;
import edu.brown.cs.student.main.api.BroadbandDatasource;
import edu.brown.cs.student.main.Server;
import okio.Buffer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
 
public class RedlineSourceTest {

	private static Server server;

	@BeforeAll
	public static void setupOnce() {
		// Spark.port(0);
		// Logger.getLogger("").setLevel(Level.WARNING);

		// Spark.port(0);
		BroadbandDatasource actualSource = new APICall();
		server = new Server(actualSource, 10, "data/geodata/fullDownload.json");
		Logger.getLogger("").setLevel(Level.WARNING);
	}

	//stencil from sept 21 livecode
	/**
	 * setup endpoints before each test
	 */
	@BeforeEach
	public void setup() {
		// Re-initialize parser, state, etc. for every test method
		Spark.get("/redline", new RedLineHandler("data/geodata/fullDownload.json"));
		Spark.init();
		Spark.awaitInitialization();
	}

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

	@Test
	public void getallData() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		assertEquals("FeatureCollection", response.featCollection().type());

		assertEquals(response.featCollection().features().size(), 8878);
	}

	@Test
	public void getFiltered() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?minLat=40.1&maxLat=50.1&minLong=-80.1&maxLong=-70.1");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		assertEquals("FeatureCollection", response.featCollection().type());

		assertEquals(response.featCollection().features().size(), 2181);
	}

	@Test
	public void filterKeyword() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?keyword=New&York&City");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		assertEquals("FeatureCollection", response.featCollection().type());

		assertEquals(response.featCollection().features().size(), 1431);
	}

	@Test
	public void redlineOutOfBounds() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?minLat=50.05&maxLat=50.1&minLong=-80.1&maxLong=-80.05");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		assertEquals("FeatureCollection", response.featCollection().type());

		assertTrue(response.featCollection().features().isEmpty());

		loadConnection.disconnect();
	}

	@Test
	public void redlineMissingParams() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?minLat=80.2");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: One or more parameters of bounding box missing", response.result());

		loadConnection.disconnect();
	}

	@Test
	public void redlineKeywordsAndBox() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?keyword=foo&minLat=80.2");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: Can only search by area or bounding box, not both!", response.result());

		loadConnection.disconnect();
	}

	@Test
	public void redlineInputOfOfBounds() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?minLat=50.05&maxLat=50.1&minLong=-200.2&maxLong=-70.1");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: Latitude or longitude was out of bounds!", response.result());

		loadConnection.disconnect();
	}


	@AfterAll
	public static void setupEnd() {
		server.tearDown();
	}
}
