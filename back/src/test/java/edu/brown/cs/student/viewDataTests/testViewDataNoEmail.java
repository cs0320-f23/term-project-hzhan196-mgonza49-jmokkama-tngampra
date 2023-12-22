package edu.brown.cs.student.viewDataTests;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.Handlers.ViewData;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class testViewDataNoEmail {
    private ViewData viewData;
    // private String path;
    // private String envPath;

	@BeforeAll
	public static void setupOnce() {
		// Pick an arbitrary free port
		Spark.port(0);
		// Eliminate logger spam in console for test suite
		Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
	}

	@BeforeEach
	public void setup() {
		this.viewData = new ViewData();

		Spark.get("/viewdata", this.viewData);
		Spark.awaitInitialization(); // don't continue until the server is listening


		// // New Moshi adapter for responses (and requests, too; see a few lines below)
		// //   For more on this, see the Server gearup.
		// Moshi moshi = new Moshi.Builder().build();
		// this.listListStringadapter = moshi.adapter(this.listOfListString);
	}

	@AfterEach
	public void tearDown() {
		// Gracefully stop Spark listening on both endpoints
		Spark.unmap("/viewdata");
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

    @Test
	public void testTooManyQueries() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"viewdata?ig=smelly_fisherman&email=fish@fish.com");

		assertEquals(200, loadConnection.getResponseCode());
		Moshi moshi = new Moshi.Builder().build();

        ViewData.ViewFailureResponse response =
				moshi.adapter(ViewData.ViewFailureResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());
        assertEquals("too many queries", response.exception());

		loadConnection.disconnect();
    }

    @Test
	public void testNoEmailQueryFail() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"viewdata?ig=smelly_fisherman");

		assertEquals(200, loadConnection.getResponseCode());
		Moshi moshi = new Moshi.Builder().build();

        ViewData.ViewFailureResponse response =
				moshi.adapter(ViewData.ViewFailureResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());
        assertEquals("please use query 'email' or no query", response.exception());

		loadConnection.disconnect();
    }

    @Test
	public void testNoEmailQuery() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"viewdata");

		assertEquals(200, loadConnection.getResponseCode());
		Moshi moshi = new Moshi.Builder().build();

        ViewData.ViewSuccessResponse response =
				moshi.adapter(ViewData.ViewSuccessResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());
        assertEquals("American Council: Advanced Russian Language & Area Studies Program (RLASP)", response.data().get(0).getName());
        assertEquals("ARMENIA", response.data().get(0).getLocation());

		loadConnection.disconnect();
    }
}