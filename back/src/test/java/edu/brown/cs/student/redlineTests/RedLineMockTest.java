package edu.brown.cs.student.redlineTests;

import edu.brown.cs.student.main.Handlers.BroadbandHandler;
import edu.brown.cs.student.main.Handlers.RedLineHandler;
import edu.brown.cs.student.main.api.APIResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import spark.Spark;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.LoadHandler;
import edu.brown.cs.student.main.Handlers.ViewHandler;
import edu.brown.cs.student.main.RedlineObjects.*;

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

import static org.junit.jupiter.api.Assertions.*;

public class RedLineMockTest {
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
		Spark.get("/redline", new RedLineHandler("data/geodata/mockDownload.json"));
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
	public void redlineBasic() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		assertEquals("FeatureCollection", response.featCollection().type());

		List<Filterer.Feature> featList = response.featCollection().features();
		Filterer.Feature firstFeature = featList.get(0);
		assertEquals("Feature", firstFeature.type());

		Filterer.Geometry geometry = firstFeature.geometry();
		assertEquals("MultiPolygon", geometry.type());
		List<List<Double>> actualCoords = new ArrayList<>();
		List<Double> dimension1 = new ArrayList<>();
		dimension1.add(-73.5);
		dimension1.add(41.1);
		actualCoords.add(dimension1);

		List<List<Double>> resCoords = new ArrayList<>();

		List<List<List<List<Double>>>> coords = geometry.coordinates();
		for (List<List<List<Double>>> c1 : coords) {
			for (List<List<Double>> c2 : c1) {
				resCoords.addAll(c2);
			}
		}

		for (int i = 0; i < resCoords.size(); i++) {
			assertEquals(resCoords.get(i).get(0), actualCoords.get(i).get(0));
			assertEquals(resCoords.get(i).get(1), actualCoords.get(i).get(1));
		}

		assertEquals(resCoords.size(), 1);

		loadConnection.disconnect();
	}

	@Test
	public void redlineBasicKeyword() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?keyword=Stamford");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		assertEquals("FeatureCollection", response.featCollection().type());

		List<Filterer.Feature> featList = response.featCollection().features();
		Filterer.Feature firstFeature = featList.get(0);
		assertEquals("Feature", firstFeature.type());

		Filterer.Geometry geometry = firstFeature.geometry();
		assertEquals("MultiPolygon", geometry.type());
		List<List<Double>> actualCoords = new ArrayList<>();
		List<Double> dimension1 = new ArrayList<>();
		dimension1.add(-73.5);
		dimension1.add(41.1);
		actualCoords.add(dimension1);

		List<List<Double>> resCoords = new ArrayList<>();

		List<List<List<List<Double>>>> coords = geometry.coordinates();
		for (List<List<List<Double>>> c1 : coords) {
			for (List<List<Double>> c2 : c1) {
				resCoords.addAll(c2);
			}
		}

		for (int i = 0; i < resCoords.size(); i++) {
			assertEquals(resCoords.get(i).get(0), actualCoords.get(i).get(0));
			assertEquals(resCoords.get(i).get(1), actualCoords.get(i).get(1));
		}

		assertEquals(resCoords.size(), 1);

		loadConnection.disconnect();
	}

	@Test
	public void redlineBasicFilter() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"redline?minLat=40.1&maxLat=50.1&minLong=-80.1&maxLong=-70.1");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		RedLineHandler.RedLineSuccessResponse response =
				moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		assertEquals("FeatureCollection", response.featCollection().type());

		List<Filterer.Feature> featList = response.featCollection().features();
		Filterer.Feature firstFeature = featList.get(0);
		assertEquals("Feature", firstFeature.type());

		Filterer.Geometry geometry = firstFeature.geometry();
		assertEquals("MultiPolygon", geometry.type());
		List<List<Double>> actualCoords = new ArrayList<>();
		List<Double> dimension1 = new ArrayList<>();
		dimension1.add(-73.5);
		dimension1.add(41.1);
		actualCoords.add(dimension1);

		List<List<Double>> resCoords = new ArrayList<>();

		List<List<List<List<Double>>>> coords = geometry.coordinates();
		for (List<List<List<Double>>> c1 : coords) {
			for (List<List<Double>> c2 : c1) {
				resCoords.addAll(c2);
			}
		}

		for (int i = 0; i < resCoords.size(); i++) {
			assertEquals(resCoords.get(i).get(0), actualCoords.get(i).get(0));
			assertEquals(resCoords.get(i).get(1), actualCoords.get(i).get(1));
		}

		assertEquals(resCoords.size(), 1);

		loadConnection.disconnect();
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

}
