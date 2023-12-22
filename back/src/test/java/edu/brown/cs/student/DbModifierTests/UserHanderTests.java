package edu.brown.cs.student.DbModifierTests;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Handlers.DatabaseSearchHandler;
import edu.brown.cs.student.main.Handlers.UpdateHandler;
import edu.brown.cs.student.main.Handlers.UserHander;
import edu.brown.cs.student.updateTests.testUpdateHandler;
import okio.Buffer;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserHanderTests {

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
		Spark.get("/adduser", new UserHander());
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

	public long countOccurences() throws IOException {
		Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.WARNING);
		ConnectionString mongoUri = new ConnectionString("mongodb+srv://tngampra:cs0320-admin@cs0320.chqlqki.mongodb.net/?retryWrites=true&w=majority");

		// Name of data base and collection
		String dbName = "study-abroad";
		String collectionName = "user-profile-data";

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
		MongoCollection<UserHanderTests.UserData> collection = database.getCollection(collectionName, UserHanderTests.UserData.class);

		long count = collection.countDocuments();

		mongoClient.close();

		return count;
	}

	@Test
	public void testUpdateData() throws IOException {
		long countBefore = countOccurences();

		HttpURLConnection loadConnection = tryRequest(
				"adduser?username=testUser123&email=idk@gmail.com&languages=Thai&countries=United%20Kingdom~Thailand&programs=Test%20Program~IDK&ranking=temperature~safety~support~idkbro");

		assertEquals(200, loadConnection.getResponseCode());
		Moshi moshi = new Moshi.Builder().build();

		UserHander.UserSuccessResponse response =
				moshi.adapter(UserHander.UserSuccessResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		loadConnection.disconnect();

		long countAfter = countOccurences();

		assertEquals(countBefore + 1, countAfter);
	}

	@Test
	public void testMissingField() throws IOException {
		HttpURLConnection loadConnection = tryRequest(
				"adduser?email=idk@gmail.com&languages=Thai&countries=United%20Kingdom~Thailand&programs=Test%20Program~IDK&ranking=temperature~safety~support~idkbro");

		assertEquals(200, loadConnection.getResponseCode());
		Moshi moshi = new Moshi.Builder().build();

		UserHander.UserSuccessResponse response =
				moshi.adapter(UserHander.UserSuccessResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("error_bad_json: ", response.result());

		loadConnection.disconnect();
	}


	public static class UserData {
		private String email;
		private String name;

		private String languages;
		private String countries;
		private String programs;
		private String ranking;

		public UserData(String email, String name, String languages, String countries, String programs, String ranking) {
			this.email = email;
			this.name = name;

			this.languages = languages;
			this.countries = countries;
			this.programs = programs;
			this.ranking = ranking;
		}

		public UserData() {
			email = "";
			name = "";
			languages = "";
			countries = "";
			programs = "";
			ranking = "";
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("ProgramData{");
			sb.append("email='").append(email).append('\'');
			sb.append(", name='").append(name).append('\'');
			sb.append(", languages='").append(languages).append('\'');
			sb.append(", countries='").append(countries).append('\'');
			sb.append(", programs='").append(programs).append('\'');
			sb.append(", ranking='").append(ranking).append('\'');
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

		// Getter for email
		public String getEmail() {
			return email;
		}

		// Setter for email
		public void setEmail(String email) {
			this.email = email;
		}

		// Getter for langauges
		public String getLanguages() {
			return languages;
		}

		// Setter for languages
		public void setLanguages(String languages) {
			this.languages = languages;
		}

		// Getter for countries
		public String getCountries() {
			return countries;
		}

		// Setter for languages
		public void setCountries(String countries) {
			this.countries = countries;
		}

		// Getter for programs
		public String setPrograms() {
			return programs;
		}

		// Setter for languages
		public void setPrograms(String programs) {
			this.programs = programs;
		}

		// Getter for ranking
		public String getRanking() {
			return ranking;
		}

		// Setter for ranking
		public void setRanking(String ranking) {
			this.ranking = ranking;
		}
	}
}

