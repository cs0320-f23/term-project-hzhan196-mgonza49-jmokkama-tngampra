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
import edu.brown.cs.student.main.Handlers.ProgramFormHander;
import edu.brown.cs.student.main.Handlers.UserHander;
import okio.Buffer;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProgramFormTest {
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
		Spark.get("programform", new ProgramFormHander());
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

	public Map<String, Integer> countOccurences() throws IOException {
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

		String program = "Arava Institute for Environmental Studies: Israel";  // Replace with the specific name you are looking for

		ProgramData programDoc = collection.find(new Document("name", program)).first();

		Map<String, Map<String, Integer>> existingScoreMap = programDoc.userScores;
		List<String> existingComments = programDoc.comment;

		int numberOfScoreMaps = existingScoreMap.size();
		int numberOfComments = existingComments.size();

		mongoClient.close();

		Map<String, Integer> sizes = new HashMap<>();
		sizes.put("score", numberOfScoreMaps);
		sizes.put("comments", numberOfComments);
		return sizes;
	}

	@Test
	public void testProgramForm() throws IOException {
		Map<String, Integer> countBefore = countOccurences();

		HttpURLConnection loadConnection = tryRequest(
				"programform?program=Arava%20Institute%20for%20Environmental%20Studies:%20Israel&username=smellygoat&acceptance=5&safety=5&min=5&learning=5&overall=5&comment=absolutely%20fantastic!");

		assertEquals(200, loadConnection.getResponseCode());
		Moshi moshi = new Moshi.Builder().build();

		UserHander.UserSuccessResponse response =
				moshi.adapter(UserHander.UserSuccessResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

		assertEquals("success", response.result());

		loadConnection.disconnect();

		Map<String, Integer> countAfter = countOccurences();

		assertEquals(countBefore.get("score") + 1, countAfter.get("score"));

		assertEquals(countBefore.get("comments") + 1, countAfter.get("comments"));
	}

	public static class ProgramData {
		private String name;
		private String link;
		private String location;
		private Map<String, Map<String, Integer>> userScores;
		private List<String> comment;

		public ProgramData(String name, String link, String location,
						   Map<String, Map<String, Integer>> userScores, List<String> comment) {
			this.name = name;
			this.link = link;
			this.location = location;
			this.userScores = userScores;
			this.comment = comment;
		}

		public ProgramData() {
			link = "";
			name = "";
			location = "";
			userScores = new HashMap<>();
			comment = new ArrayList<>();
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("ProgramData{");
			sb.append("name='").append(name).append('\'');
			sb.append(", link='").append(link).append('\'');
			sb.append(", location='").append(location).append('\'');
			sb.append(", userScores='").append(userScores).append('\'');
			sb.append(", comment='").append(comment).append('\'');
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
	}
}
