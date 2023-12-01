package edu.brown.cs.student.redlineTests;

import edu.brown.cs.student.main.Handlers.LoadHandler;
import edu.brown.cs.student.main.Handlers.RedLineHandler;
import edu.brown.cs.student.main.Handlers.SearchHandler;
import okio.Buffer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.squareup.moshi.Moshi;

import spark.Spark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;


public class FuzzTesting {
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

	private List<Integer> generate() {
		Random rand = new Random();
		int min_lat = rand.nextInt(201)-100;
		int max_lat = rand.nextInt(201)-100;
		int min_lon = rand.nextInt(401)-200;
		int max_lon = rand.nextInt(401)-200;

		List<Integer> result = new ArrayList<Integer>();
		result.add(min_lat);
		result.add(max_lat);
		result.add(min_lon);
		result.add(max_lon);

		return result;
	}

	@Test
	public void fuzzTest() throws IOException {
		int i = 0;
		while (i < 100) {
			List<Integer> inputs = generate();
			Integer minLatParsed = inputs.get(0);
			Integer maxLatParsed = inputs.get(1);
			Integer minLongParsed = inputs.get(2);
			Integer maxLongParsed = inputs.get(3);
			// System.out.println(inputs.toString());

			HttpURLConnection loadConnection = tryRequest(
				"redline?minLat=" + minLatParsed.toString() + 
				"&maxLat=" + maxLatParsed.toString() +
				"&minLong=" + minLongParsed.toString() +
				"&maxLong=" + maxLongParsed.toString());

			assertEquals(200, loadConnection.getResponseCode());

			Moshi moshi = new Moshi.Builder().build();
			RedLineHandler.RedLineSuccessResponse response =
					moshi.adapter(RedLineHandler.RedLineSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

			if ((Math.abs(minLatParsed) > 90) || (Math.abs(maxLatParsed) > 90) || (Math.abs(minLongParsed) > 180) || (Math.abs(maxLongParsed) > 180)) {
				assertEquals("error_bad_json: Latitude or longitude was out of bounds!", response.result());
				continue;
			}

			if ((minLatParsed > maxLatParsed) || (minLongParsed > maxLongParsed)) {
				assertEquals("error_bad_json: Minimum latitude or longitude greater than maximum!", response.result());				
				continue;
			}

			assertEquals("success", response.result());
			assertEquals("FeatureCollection", response.featCollection().type());
			i++;
		}
		
	}
}
