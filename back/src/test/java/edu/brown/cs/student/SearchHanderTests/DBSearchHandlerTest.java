package edu.brown.cs.student.SearchHanderTests;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Handlers.DatabaseSearchHandler;
import okio.Buffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBSearchHandlerTest {

	@BeforeAll
	public static void setupOnce() {
		// Pick an arbitrary free port
		Spark.port(0);
		// Eliminate logger spam in console for test suite
		Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
	}

	@BeforeEach
	public void setup() {
		// Re-initialize parser, state, etc. for every test method
		Spark.get("/searchprograms", new DatabaseSearchHandler());
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
	public void testBasicSuccess() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"searchprograms?keyword=Institute");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		DatabaseSearchHandler.SearchSuccessResponse response =
				moshi.adapter(DatabaseSearchHandler.SearchSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		assertEquals(response.data().size(), 5);
		loadConnection.disconnect();
	}

	@Test
	public void testBasicFailure() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"searchprograms?");

		assertEquals(200, loadConnection.getResponseCode());

		Moshi moshi = new Moshi.Builder().build();
		DatabaseSearchHandler.SearchSuccessResponse response =
				moshi.adapter(DatabaseSearchHandler.SearchSuccessResponse.class).fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());

		loadConnection.disconnect();
	}
}
