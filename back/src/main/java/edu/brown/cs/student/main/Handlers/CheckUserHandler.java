package edu.brown.cs.student.main.Handlers;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Handlers.DatabaseSearchHandler.ProgramData;
import edu.brown.cs.student.main.Handlers.UserHander.UserSuccessResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import spark.Request;
import spark.Response;
import spark.Route;

public class CheckUserHandler implements Route {

  private List<Document> searchDatabase(String email, MongoCollection<Document> collection) {

    // check nullness too
    email = email.toLowerCase();

    Bson filter = Filters.and(
        Filters.eq("email", email)
    );

    List<Document> results = new ArrayList<>();
    collection.find(filter).forEach(results::add);
    System.out.println(results.size());

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
        String email = request.queryParams("email");

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
          return new edu.brown.cs.student.main.Handlers.CheckUserHandler.UserFailureResponse("Unable to connect to the MongoDB instance due to an error", me.getMessage(), email).serialize();
        }

        // MongoDatabase defines a connection to a specific MongoDB database
        MongoDatabase database = mongoClient.getDatabase(dbName);

        // MongoCollection defines a connection to a specific collection of documents in a specific database
        MongoCollection<Document> collection = database.getCollection(collectionName);

        boolean isMember = !this.searchDatabase(email, collection).isEmpty();

        return new edu.brown.cs.student.main.Handlers.CheckUserHandler.UserSuccessResponse("success", email, isMember);
      }

      /**
       * UserSuccessResponse - a class of type record that contains the success response we want to return
       * @param result - a string, can be success or failure
       */
      public record UserSuccessResponse(String result, String email, boolean isMember ) {
        public UserSuccessResponse(String email, boolean isMember) {
          this("success", email, isMember);
        }
        /**
         * @return this response, serialized as Json
         */
        String serialize() {
          try {
            // Just like in SoupAPIUtilities.
            //   (How could we rearrange these similar methods better?)
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<edu.brown.cs.student.main.Handlers.CheckUserHandler.UserSuccessResponse> adapter = moshi.adapter(
                edu.brown.cs.student.main.Handlers.CheckUserHandler.UserSuccessResponse.class);
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
      public record UserFailureResponse(String result, String exception, String email) {
        //result should contain error and error code
        public UserFailureResponse(String result, String exception, String email) {
          this.result = result;
          this.exception = exception;
          this.email = email;
        }

        /**
         * @return this response, serialized as Json
         */
        String serialize() {
          Moshi moshi = new Moshi.Builder().build();
          return moshi.adapter(edu.brown.cs.student.main.Handlers.CheckUserHandler.UserFailureResponse.class).toJson(this);
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
