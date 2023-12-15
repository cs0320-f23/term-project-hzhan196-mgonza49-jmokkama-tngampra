package edu.brown.cs.student.main.Handlers;


import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
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

import static java.lang.Integer.parseInt;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
public class ProgramFormHander implements Route {
	public ProgramFormHander(){}

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
		String program = request.queryParams("program");
		String username = request.queryParams("username");

		String acceptance = request.queryParams("acceptance");
		String safety = request.queryParams("safety");
		String minorities = request.queryParams("min");
		String learning = request.queryParams("learning");
		String overall = request.queryParams("overall");
		String comment = request.queryParams("comment");

		if (program == null) {
			response.status(400);
			// some sort of error
			return new ProgramFormHander.ProgramFormFailureResponse("error_bad_json: ", "missing program name", program).serialize();
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
			return new ProgramFormHander.ProgramFormFailureResponse("Unable to connect to the MongoDB instance due to an error", me.getMessage(), program).serialize();
		}

		// MongoDatabase defines a connection to a specific MongoDB database
		MongoDatabase database = mongoClient.getDatabase(dbName);

		// MongoCollection defines a connection to a specific collection of documents in a specific database
		MongoCollection<ProgramData> collection = database.getCollection(collectionName, ProgramData.class);

		try {
			HashMap<String, Integer> scoreMap = new HashMap<>();

			scoreMap.put("acceptance", parseInt(acceptance));
			scoreMap.put("safety", parseInt(safety));
			scoreMap.put("minorities", parseInt(minorities));
			scoreMap.put("learning", parseInt(learning));
			scoreMap.put("overall", parseInt(overall));

			HashMap<String, String> commentMap = new HashMap<>();
			commentMap.put("username", username);
			commentMap.put("comment", comment);

			// Update the existing document with the new values
			ProgramData programDoc = collection.find(new Document("name", program)).first();

			if (programDoc == null) {
				return new ProgramFormHander.ProgramFormFailureResponse("input_bad_json: ", "matching document not found", program).serialize();
			}

			// Extract the current HashMap from the document
			HashMap<String, HashMap<String, Integer>> existingHashMap = programDoc.userScores;

			existingHashMap.put(username, scoreMap);

			// Creates instructions to update the values of three document fields
			Bson updates = Updates.combine(
					Updates.set("userScores", existingHashMap),
					Updates.addToSet("comments", commentMap));

			// Perform the update
			UpdateResult result = collection.updateOne(new Document("name", program), updates);

			// Check if the document was modified
			if (result.getModifiedCount() > 0) {
				return new ProgramFormHander.ProgramFormSuccessResponse("success", program);
			} else {
				return new ProgramFormHander.ProgramFormFailureResponse("input_bad_json: ", "matching document not found", program).serialize();
			}
		} catch (MongoException me) {
			System.err.println("Unable to update due to an error: " + me);
			return new ProgramFormHander.ProgramFormFailureResponse("Unable to update due to an error", me.getMessage(), program).serialize();
		} catch (Exception e) {
			return new ProgramFormHander.ProgramFormFailureResponse("Unable to update due to an error", e.getMessage(), program).serialize();
		}
	}

	/**
	 * ProgramFormSuccessResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param program - name of the program being updated
	 */
	public record ProgramFormSuccessResponse(String result, String program) {
		public ProgramFormSuccessResponse(String program) {
			this("success", program);
		}
		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			try {
				// Just like in SoupAPIUtilities.
				//   (How could we rearrange these similar methods better?)
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<ProgramFormHander.ProgramFormSuccessResponse> adapter = moshi.adapter(
						ProgramFormHander.ProgramFormSuccessResponse.class);
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
	public record ProgramFormFailureResponse(String result, String exception, String program) {
		//result should contain error and error code
		public ProgramFormFailureResponse(String result, String exception, String program) {
			this.result = result;
			this.exception = exception;
			this.program = program;
		}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			Moshi moshi = new Moshi.Builder().build();
			return moshi.adapter(ProgramFormHander.ProgramFormFailureResponse.class).toJson(this);
		}
	}

	public static class ProgramData {
		private String name;
		private String link;
		private String location;
		private HashMap<String, HashMap<String, Integer>> userScores;
		private List<String> comment;

		public ProgramData(String name, String link, String location,
						   HashMap<String, HashMap<String, Integer>> userScores, List<String> comment) {
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
		public HashMap<String, HashMap<String, Integer>> getUserScores() {
			return userScores;
		}

		// Setter for user scores
		public void setUserScores(HashMap<String, HashMap<String, Integer>> userScores) {
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
