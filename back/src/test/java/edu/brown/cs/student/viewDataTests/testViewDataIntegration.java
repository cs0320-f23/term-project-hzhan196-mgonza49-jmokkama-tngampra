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

import edu.brown.cs.student.main.Handlers.ProgramFormHander;
import edu.brown.cs.student.main.Handlers.UpdateHandler;
import edu.brown.cs.student.main.Handlers.UserHander;
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

public class testViewDataIntegration {
    private ViewData viewData;
	private UserHander userHander;
	private ProgramFormHander programFormHander;

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
		this.userHander = new UserHander();
		this. programFormHander = new ProgramFormHander();

		Spark.get("/viewdata", this.viewData);
		Spark.get("/adduser", this.userHander);
		Spark.get("/programform", this.programFormHander);
		Spark.awaitInitialization();
	}

	@AfterEach
	public void tearDown() {
		Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.WARNING);
        ConnectionString mongoUri = new ConnectionString("mongodb+srv://tngampra:cs0320-admin@cs0320.chqlqki.mongodb.net/?retryWrites=true&w=majority");

        // Name of data base and collection
        String dbName = "study-abroad";
        String collectionName = "program-data";
		String collectionNameUsr = "user-profile-data";

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
		MongoCollection<UserData> collectionUsr = database.getCollection(collectionNameUsr, UserData.class);

        Bson filterUsr;
        filterUsr = Filters.and(
                Filters.eq("email", "testIntegration@gmail.com")
        );

		collectionUsr.findOneAndDelete(filterUsr);

		Bson filterPro;
        filterPro = Filters.or(
			Filters.eq("name", "CASA: Brown Global Program / CASA Havana"),
			Filters.eq("name", "University of the West Indies")
        );

		List<ProgramData> results = new ArrayList<>();
		collection.find(filterPro).forEach(results::add);

		int i = 0;
		while (i < results.size()) {
			ProgramData program = results.get(i);
			Map<String, Map<String, Integer>> userScores = program.getUserScores();
			userScores.remove("testIntegration");
			program.setUserScores(userScores);

			List<Map<String,String>> comments = program.getComments();
			int j = 0;
			while (j < comments.size()) {
				Map<String,String> comment = comments.get(j);
				if (comment.get("username") == "testIntegration") {
					comments.remove(j);
				} else {
					j++;
				}
			}
			program.setComments(comments);
			i++;
		}

		// Gracefully stop Spark listening on both endpoints
		Spark.unmap("/viewdata");
		Spark.unmap("/adduser");
		Spark.unmap("/programform");
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
    public void testIntegration() throws IOException {
        // SETUP DONE
        HttpURLConnection loadConnection0 = tryRequest(
			"adduser?username=testIntegration&email=testIntegration%40gmail.com&languages=Thai&countries=Thailand&programs=ProgramX&ranking=acceptance%7Esafety%7Eminority%7Elearning"
		);

		assertEquals(200, loadConnection0.getResponseCode());
		Moshi moshi0 = new Moshi.Builder().build();

        UserHander.UserSuccessResponse responseUser =
				moshi0.adapter(UserHander.UserSuccessResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection0.getInputStream()));
		
		assertEquals("success", responseUser.result());
		assertEquals("testIntegration", responseUser.username());

		loadConnection0.disconnect();

		HttpURLConnection loadConnection1 = tryRequest(
			"programform?program=CASA:%20Brown%20Global%20Program%20/%20CASA%20Havana&username=testIntegration&acceptance=5&safety=4&min=4&learning=4&overall=5&comment=great"
		);

		assertEquals(200, loadConnection1.getResponseCode());
		Moshi moshi1 = new Moshi.Builder().build();

        ProgramFormHander.ProgramFormSuccessResponse responseProgram =
				moshi1.adapter(ProgramFormHander.ProgramFormSuccessResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection1.getInputStream()));
		
		assertEquals("success", responseProgram.result());
		assertEquals("CASA: Brown Global Program / CASA Havana", responseProgram.program());

		loadConnection1.disconnect();

		HttpURLConnection loadConnection2 = tryRequest(
			"programform?program=University%20of%20the%20West%20Indies&username=testIntegration&acceptance=4&safety=4&min=4&learning=5&overall=4&comment=good"
		);

		assertEquals(200, loadConnection2.getResponseCode());
		Moshi moshi2 = new Moshi.Builder().build();
		
		ProgramFormHander.ProgramFormSuccessResponse responseProgram2 =
				moshi2.adapter(ProgramFormHander.ProgramFormSuccessResponse.class)
						.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));
		
		assertEquals("success", responseProgram2.result());
		assertEquals("University of the West Indies", responseProgram2.program());

		loadConnection2.disconnect();

		HttpURLConnection loadConnection3 = tryRequest("viewdata?email=testIntegration%40gmail.com");

		assertEquals(200, loadConnection3.getResponseCode());
		// Moshi moshi3 = new Moshi.Builder().build();

        // ViewData.ViewSuccessResponse responseView =
		// 		moshi3.adapter(ViewData.ViewSuccessResponse.class)
		// 				.fromJson(new Buffer().readFrom(loadConnection3.getInputStream()));
		
		// assertEquals("success", responseView.result());
		// assertEquals("testIntegration@gmail.com", responseView.usrData().get(0).getEmail());

		// assertEquals("CASA: Brown Global Program / CASA Havana", responseView.data().get(1).getName());
		// assertEquals("University of the West Indies", responseView.data().get(2).getName());

		loadConnection3.disconnect();
    }


	public static class ProgramData {
		private String name;
		private String link;
        private String location;
        private Map<String, Map<String, Integer>> userScores;
        private List<Map<String,String>> comments; 
		private Map<String, Float> average;

		public ProgramData(String name, String link, String location,
            Map<String, Map<String, Integer>> userScores, List<Map<String,String>> comments, Map<String, Float> average) {
			this.name = name;
			this.link = link;
			this.location = location;
            this.userScores = userScores;
            this.comments = comments;
			this.average = average;
		}

		public ProgramData() {
			link = "";
			name = "";
			location = "";
            userScores = new HashMap<>();
            comments = new ArrayList<>();
			average = new HashMap<>();
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("ProgramData{");
			sb.append("name='").append(name).append('\'');
			sb.append(", link='").append(link).append('\'');
			sb.append(", location='").append(location).append('\'');
            sb.append(", userScores='").append(userScores).append('\'');
            sb.append(", comments='").append(comments).append('\'');
			sb.append(", average='").append(average).append('\'');
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
		public List<Map<String,String>> getComments() {
			return comments;
		}

		// Setter for comment
		public void setComments(List<Map<String,String>> comments) {
			this.comments = comments;
		}

		// Getter for comment
		public Map<String, Float> getAverage() {
			return average;
		}

		// Setter for comment
		public void setAverage(Map<String, Float> average) {
			this.average = average;
		}
	}

	public static class UserData {
		private String email;
		private String name; 

		private List<String> languages;
		private List<String> countries;
		private List<String> programs; 
		private List<String> ranking;

		public UserData(String email, String name, 
		List<String> languages, List<String> countries, List<String> programs, List<String> ranking) {
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
			languages = new ArrayList<>();
			countries = new ArrayList<>();
			programs = new ArrayList<>();
			ranking = new ArrayList<>();
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
		public List<String> getLanguages() {
			return languages;
		}

		// Setter for languages
		public void setLanguages(List<String> languages) {
			this.languages = languages;
		}

		// Getter for countries
		public List<String> getCountries() {
			return countries;
		}

		// Setter for languages
		public void setCountries(List<String> countries) {
			this.countries = countries;
		}

		// Getter for programs
		public List<String> setPrograms() {
			return programs;
		}

		// Setter for languages
		public void setPrograms(List<String> programs) {
			this.programs = programs;
		}

		// Getter for ranking
		public List<String> getRanking() {
			return ranking;
		}

		// Setter for ranking
		public void setRanking(List<String> ranking) {
			this.ranking = ranking;
		}
	}
}