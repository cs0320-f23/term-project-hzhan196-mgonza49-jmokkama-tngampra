package edu.brown.cs.student.updateTests;

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
import edu.brown.cs.student.main.Handlers.UpdateHandler;

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


public class testUpdateHandler {
    // private final Type listOfListString = Types.newParameterizedType(List.class,
	// 		Types.newParameterizedType(List.class, String.class));
	private UpdateHandler updateHandler;
    private String path;
    private String envPath;

	@BeforeAll
	public static void setupOnce() {
		// Pick an arbitrary free port
		Spark.port(0);
		// Eliminate logger spam in console for test suite
		Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
	}

	@BeforeEach
	public void setup() {
        this.path = "./src/main/scraping/LoadPrograms.py";
        this.envPath = "./src/main/scraping/scrape_venv/bin/python3";
		this.updateHandler = new UpdateHandler(this.path, this.envPath);

		Spark.get("/updatedatabase", this.updateHandler);
		Spark.awaitInitialization(); // don't continue until the server is listening


		// // New Moshi adapter for responses (and requests, too; see a few lines below)
		// //   For more on this, see the Server gearup.
		// Moshi moshi = new Moshi.Builder().build();
		// this.listListStringadapter = moshi.adapter(this.listOfListString);
	}

	@AfterEach
	public void tearDown() {
		// Gracefully stop Spark listening on both endpoints
		Spark.unmap("/updatedatabase");
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
				"updatedatabase?ig=smelly_fisherman");

		assertEquals(200, loadConnection.getResponseCode());
		Moshi moshi = new Moshi.Builder().build();

        UpdateHandler.LoadFailureResponse response =
				moshi.adapter(UpdateHandler.LoadFailureResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: please don't put in any parameters", response.result());

		loadConnection.disconnect();
    }

    @Test
    public void testUpdateData() throws IOException {
        Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.WARNING);
        ConnectionString mongoUri = new ConnectionString("mongodb+srv://tngampra:cs0320-admin@cs0320.chqlqki.mongodb.net/?retryWrites=true&w=majority");

        // Name of data base and collection
        String dbName = "study-abroad";
        String collectionName = "program-data";

        // a CodecRegistry tells the Driver how to move data between Java POJOs (Plain Old Java Objects) and MongoDB documents
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // The MongoClient defines the connection to our MongoDB datastore instance (Atlas) using MongoClientSettings
        // You can create a MongoClientSettings with a Builder to configure codecRegistries, connection strings, and more
        MongoClientSettings settings = MongoClientSettings.builder()
            .codecRegistry(pojoCodecRegistry)
            .applyConnectionString(mongoUri).build();

        MongoClient mongoClient = null;
        try {
        	mongoClient = MongoClients.create(settings);
        } catch (MongoException me) {
            assertEquals(0, 1); // test fails... cannot connect to mongo
        }

        // MongoDatabase defines a connection to a specific MongoDB database
        MongoDatabase database = mongoClient.getDatabase(dbName);

        // MongoCollection defines a connection to a specific collection of documents in a specific database
        MongoCollection<ProgramData> collection = database.getCollection(collectionName, ProgramData.class);

        Bson filter;
        filter = Filters.and(
                Filters.eq("name", "CASA: Brown Global Program / CASA Havana")
        );
        // ProgramData value = collection.findOneAndDelete(filter);
        // assertEquals(value.getName(), "CASA: Brown Global Program / CASA Havana");

        // ProgramData value2 = collection.findOneAndDelete(filter);
        // assertEquals(value2, null);

        Bson filter2;
        filter2 = Filters.and(
                Filters.eq("name", "American Council: Advanced Russian Language & Area Studies Program (RLASP)")
        );
        List<ProgramData> results = new ArrayList<>();
        collection.find(filter2).forEach(results::add);

        assertEquals(results.get(0).getUserScores().get("smellygoat").get("safety"), 5);

        // SETUP DONE

        HttpURLConnection loadConnection = tryRequest(
				"updatedatabase");

		assertEquals(200, loadConnection.getResponseCode());
		Moshi moshi = new Moshi.Builder().build();

        UpdateHandler.LoadSuccessResponse response =
				moshi.adapter(UpdateHandler.LoadSuccessResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));


        String currentPath = new java.io.File(".").getCanonicalPath();
        System.out.println("Current dir:" + currentPath);
        assertEquals(currentPath, "/Users/earth/Documents/CS 0320/term-project-hzhan196-mgonza49-jmokkama-tngampra/back");

		assertEquals(this.path, response.filepath());
        assertEquals("success", response.result());
		loadConnection.disconnect();

        // DONE CALLING API

        List<ProgramData> results_after = new ArrayList<>();
        collection.find(filter).forEach(results_after::add);

        assertEquals(results_after.get(0).getName(), "CASA: Brown Global Program / CASA Havana");

        List<ProgramData> results_after2 = new ArrayList<>();
        collection.find(filter2).forEach(results_after2::add);

        assertEquals(results.get(0).getUserScores().get("smellygoat").get("safety"), 5);
    }




    public static class ProgramData implements Comparable<ProgramData> {
		private String name;
		private String link;
        private String location;
        private Map<String, Map<String, Integer>> userScores;
        private List<String> comment; 
		private Map<String, Float> average;
		private String email;

		public ProgramData(String name, String link, String location,
            Map<String, Map<String, Integer>> userScores, List<String> comment, Map<String, Float> average,
			String email) {
			this.name = name;
			this.link = link;
			this.location = location;
            this.userScores = userScores;
            this.comment = comment;
			this.average = average;
			this.email = email;
		}

		public ProgramData() {
			link = "";
			name = "";
			location = "";
            userScores = new HashMap<>();
            comment = new ArrayList<>();
			average = new HashMap<>();
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("ProgramData{");
			sb.append("name='").append(name).append('\'');
			sb.append(", link='").append(link).append('\'');
			sb.append(", location='").append(location).append('\'');
            sb.append(", userScores='").append(userScores).append('\'');
            sb.append(", comment='").append(comment).append('\'');
			sb.append(", average='").append(comment).append('\'');
			sb.append('}');
			return sb.toString();
		}

		// Getter for name
		public String getName() {
			return name;
		}

		// Setter for name
		public void setName(String name) {
			this.name = name;
		}

		// Getter for link
		public String getLink() {
			return link;
		}

		// Setter for link
		public void setLink(String link) {
			this.link = link;
		}

		// Getter for location
		public String getLocation() {
			return location;
		}

		// Setter for location
		public void setLocation(String location) {
			this.location = location;
		}

        // Getter for user scores
		public Map<String, Map<String, Integer>> getUserScores() {
			return userScores;
		}

		// Setter for user scores
		public void setUserScores(Map<String, Map<String, Integer>> userScores) {
			this.userScores = userScores;
		}

        // Getter for comment
		public List<String> getComment() {
			return comment;
		}

		// Setter for comment
		public void setComment(List<String> comment) {
			this.comment = comment;
		}

		// Getter for comment
		public Map<String, Float> getAverage() {
			return average;
		}

		// Setter for comment
		public void setAverage(Map<String, Float> average) {
			this.average = average;
		}

		// Getter for comment
		public String getEmail() {
			return email;
		}

		// Setter for comment
		public void setEmail(String email) {
			this.email = email;
		}

		@Override
		public int compareTo(ProgramData program) {
			//let's sort the employee based on an id in ascending order
			//returns a negative integer, zero, or a positive integer as this employee id
			//is less than, equal to, or greater than the specified object.
			return Math.round(this.getAverage().get(this.getEmail()) - program.getAverage().get(program.getEmail()));
		}
	}
    
}
