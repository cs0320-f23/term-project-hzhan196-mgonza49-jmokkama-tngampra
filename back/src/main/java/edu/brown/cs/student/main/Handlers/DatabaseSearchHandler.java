package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import okio.Buffer;
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

//import io.realm.mongodb.App;
//import io.realm.mongodb.AppConfiguration;
//import io.realm.mongodb.Credentials;
//import io.realm.mongodb.User;
//import io.realm.mongodb.functions.Functions;
//

public class DatabaseSearchHandler implements Route {

  public DatabaseSearchHandler() {}
  // /**
  //  * Adapted from the testing code. Helper method to start a connection to a specific API endpoint.
  //  * Modified signature to take in a callRequest string to set the request method to use either GET
  //  * or POST.
  //  *
  //  * @param apiCall the call string for the API endpoint
  //  * @return the connection to the API endpoint
  //  * @throws IOException if there is an error connecting to the API endpoint
  //  */
  // private static HttpURLConnection tryRequest(String apiCall, String callRequest) throws IOException {
  //   URL request = new URL("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*");
  //   HttpURLConnection connection = (HttpURLConnection) request.openConnection();
  //   connection.setRequestMethod(callRequest);
  //   connection.connect();

  //   if (connection.getResponseCode() != 200) {
  //     throw new RuntimeException("HttpResponseCode: " + connection.getResponseCode());
  //   }
  //   return connection;
  // }

   private List<ProgramData> searchDatabase(String keyword, String country, MongoCollection<ProgramData> collection) {

     // check nullness too
     keyword = keyword.toLowerCase();
     if (country != null) {
       country = country.toLowerCase();
     }

	 Bson filter = Filters.and(
			 Filters.eq("name", keyword),
			 Filters.eq("country", country)
	 );

	 List<ProgramData> results = new ArrayList<>();
	 collection.find(filter, ProgramData.class).forEach(results::add);

     return results;
   }

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
    String keyword = request.queryParams("keyword");
    String country = request.queryParams("country");

    if ((keyword == null) && (country == null)) {
      response.status(400);
      // some sort of error
      return "No parameters provided";
    }

    Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.WARNING);
    // TODO changed hardcoded connection string so it doesn't contain the password
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
		return new DatabaseSearchHandler.SearchFailureResponse("Unable to connect to the MongoDB instance due to an error", me.getMessage(), keyword, country).serialize();
    }
    
    // MongoDatabase defines a connection to a specific MongoDB database
    MongoDatabase database = mongoClient.getDatabase(dbName);

    // MongoCollection defines a connection to a specific collection of documents in a specific database
    MongoCollection<ProgramData> collection = database.getCollection(collectionName, ProgramData.class);

    List<ProgramData> searchData = this.searchDatabase(keyword, country, collection);

	  return new DatabaseSearchHandler.SearchSuccessResponse("sucess", searchData, keyword, country);
  }

  /**
	 * SearchSuccessResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param data - the data we are returning
	 */
	public record SearchSuccessResponse(String result, List<ProgramData> data, String keyword, String country) {
		public SearchSuccessResponse(List<ProgramData> data, String keyword, String country) {
			this("success", data, keyword, country);
		}
		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			try {
				// Just like in SoupAPIUtilities.
				//   (How could we rearrange these similar methods better?)
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<DatabaseSearchHandler.SearchSuccessResponse> adapter = moshi.adapter(
						DatabaseSearchHandler.SearchSuccessResponse.class);
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
	 * SearchFailureResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param exception - the exception that caused the failure
	 */
	public record SearchFailureResponse(String result, String exception, String keyword, String country) {
		//result should contain error and error code
		public SearchFailureResponse(String result, String exception, String keyword, String country) {
			this.result = result;
			this.exception = exception;
			this.keyword = keyword;
			this.country = country;
		}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			Moshi moshi = new Moshi.Builder().build();
			return moshi.adapter(SearchHandler.SearchFailureResponse.class).toJson(this);
		}
	}

  public class ProgramData {
    private String name;
    private String link;
    private String location;

    public ProgramData(String name, String link, String location) {
      this.name = name;
      this.link = link;
      this.location = location;
    }
  }
}