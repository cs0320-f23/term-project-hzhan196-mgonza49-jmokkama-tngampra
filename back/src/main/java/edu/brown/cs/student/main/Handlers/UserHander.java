package edu.brown.cs.student.main.Handlers;

import com.mongodb.client.result.InsertOneResult;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import okio.Buffer;
import org.bson.types.ObjectId;
import spark.Request;
import spark.Response;
import spark.Route;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.eclipse.jetty.util.log.Log;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class UserHander implements Route {
  public UserHander(){}

  // Maps the state codes to their names by calling the 2010 census API
  /**
   * Adapted from the other handler classes. Expected Behavior: 1. Calls the methods to map the
   * state and county codes to their names 2. Makes a call to the 2021 census API to get the
   * broadband data for the state and county 3. Stores the broadband data in a list of lists 4.
   * Returns the broadband data
   *
   * @param request The request to handle
   * @param response The response object providing functionality for modifying the response
   * @return
   * @throws Exception
   */
  
  @Override
  public Object handle(Request request, Response response) {
    // Query parameters for the API call, county code is optional
    String username = request.queryParams("username");
    String email = request.queryParams("email");

	String languages = request.queryParams("languages");
	String countries = request.queryParams("countries");
	String programs = request.queryParams("programs");
	String ranking = request.queryParams("ranking");

    if ((username == null) && (email == null)) {
      response.status(400);
      // some sort of error
		return new UserHander.UserFailureResponse("error_bad_json: ", "missing keyword or country", username, email).serialize();
    }

	if (languages == null) {
		response.status(400);
		// some sort of error
		return new UserHander.UserFailureResponse("error_bad_json: ", "missing language", username, email).serialize();
	}

	if (countries == null) {
		response.status(400);
		  // some sort of error
		return new UserHander.UserFailureResponse("error_bad_json: ", "missing countries", username, email).serialize();
	}

	if (programs == null) {
		response.status(400);
		// some sort of error
		return new UserHander.UserFailureResponse("error_bad_json: ", "missing programs", username, email).serialize();
	}

	if (ranking == null) {
		response.status(400);
		  // some sort of error
		return new UserHander.UserFailureResponse("error_bad_json: ", "missing ranking", username, email).serialize();
	}

    Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.WARNING);
    // TODO changed hardcoded connection string so it doesn't contain the password
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
		return new UserHander.UserFailureResponse("Unable to connect to the MongoDB instance due to an error", me.getMessage(), username, email).serialize();
    }
    
    // MongoDatabase defines a connection to a specific MongoDB database
    MongoDatabase database = mongoClient.getDatabase(dbName);

    // MongoCollection defines a connection to a specific collection of documents in a specific database
    MongoCollection<Document> collection = database.getCollection(collectionName);

	String symbol = "~";
	List<String> languagesParsed = splitOnSymbol(languages, symbol);
	List<String> countriesParsed = splitOnSymbol(countries, symbol);
	List<String> programsParsed = splitOnSymbol(programs, symbol);
	List<String> rankingParsed = splitOnSymbol(ranking, symbol);

	try {
			// Inserts a sample document describing a movie into the collection
			InsertOneResult result = collection.insertOne(new Document()
					.append("_id", new ObjectId())
					.append("email", email)
					.append("username", username)
					.append("languages", languagesParsed)
					.append("countries", countriesParsed)
					.append("programs", programsParsed)
					.append("ranking", rankingParsed));
			// Prints the ID of the inserted document
			System.out.println("Success! Inserted document id: " + result.getInsertedId());
		
		// Prints a message if any exceptions occur during the operation
		} catch (MongoException me) {
			System.err.println("Unable to insert due to an error: " + me);
			return new UserHander.UserFailureResponse("Unable to insert due to an error", me.getMessage(), username, email).serialize();
		}


	return new UserHander.UserSuccessResponse("success", username, email);
  }

  public List<String> splitOnSymbol(String input, String symbol){
	// Split the string using the regex and convert the array to ArrayList
	ArrayList<String> resultList = new ArrayList<>(Arrays.asList(input.split(symbol)));
	
	// Display the result using ArrayList
	for (String part : resultList) {
		System.out.println(part);
	}

	return resultList;
  }

  /**
	 * UserSuccessResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param username - username of the user
	 */
	public record UserSuccessResponse(String result, String username, String email ) {
		public UserSuccessResponse(String username, String email) {
			this("success", username, email);
		}
		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			try {
				// Just like in SoupAPIUtilities.
				//   (How could we rearrange these similar methods better?)
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<UserHander.UserSuccessResponse> adapter = moshi.adapter(
						UserHander.UserSuccessResponse.class);
				return adapter.toJson(this);
			} catch(Exception e) {
				// For debugging purposes, show in the console _why_ this fails
				// Otherwise we'll just get an error 500 from the API in integration
				// testing.
				e.printStackTrace();
				throw e;
			}
		}
	}

	/**
	 * UserFailureResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param exception - the exception that caused the failure
	 */
	public record UserFailureResponse(String result, String exception, String username, String email) {
		//result should contain error and error code
		public UserFailureResponse(String result, String exception, String username, String email) {
			this.result = result;
			this.exception = exception;
			this.username = username;
			this.email = email;
		}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			Moshi moshi = new Moshi.Builder().build();
			return moshi.adapter(UserHander.UserFailureResponse.class).toJson(this);
		}
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
