package edu.brown.cs.student.main.Handlers;

import com.mongodb.client.model.Projections;
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
import org.eclipse.jetty.server.Authentication.User;
import org.eclipse.jetty.util.log.Log;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class ViewData implements Route {
    public ViewData() {}

	private List<ProgramData> sortProgramData(String email, List<ProgramData> programData, List<UserData> userData) {
		Integer len = programData.size();
		Integer i = 0;
		while (i < len) {
			ProgramData program = programData.get(i);
			Map<String, Map<String, Integer>> userScores = program.getUserScores();
			List<Map<String, Integer>> scoreValues = new ArrayList<Map<String, Integer>>(userScores.values());
			Integer lenScore = scoreValues.size();
			Integer j = 0;
			float acceptance = 0;
			float safety = 0;
			float minority = 0;
			float learning = 0;
			while (j < lenScore) {
				acceptance += scoreValues.get(j).get("acceptance");
				safety += scoreValues.get(j).get("safety");
				minority += scoreValues.get(j).get("minority");
				learning += scoreValues.get(j).get("learning");
				j = j + 1;
			}
			acceptance = acceptance / lenScore;
			safety = safety / lenScore;
			minority = minority / lenScore;
			learning = learning / lenScore;
			
			UserData userDataOne = userData.get(0);
			List<String> ranks = userDataOne.getRanking();
			Integer k = 0;
			while (k < ranks.size()) {
				if (k == 0) {
					if (ranks.get(k) == "acceptance") {
						acceptance = acceptance * 4;
					} else if (ranks.get(k) == "safety") {
						safety = safety * 4;
					} else if (ranks.get(k) == "minority") {
						minority = minority * 4;
					} else if (ranks.get(k) == "learning") {
						learning = learning * 4;
					}
				} else if (k == 1) {
					if (ranks.get(k) == "acceptance") {
						acceptance = acceptance * 3;
					} else if (ranks.get(k) == "safety") {
						safety = safety * 3;
					} else if (ranks.get(k) == "minority") {
						minority = minority * 3;
					} else if (ranks.get(k) == "learning") {
						learning = learning * 3;
					}
				} else if (k == 2) {
					if (ranks.get(k) == "acceptance") {
						acceptance = acceptance * 2;
					} else if (ranks.get(k) == "safety") {
						safety = safety * 2;
					} else if (ranks.get(k) == "minority") {
						minority = minority * 2;
					} else if (ranks.get(k) == "learning") {
						learning = learning * 2;
					}
				} else if (k == 3) {
					if (ranks.get(k) == "acceptance") {
						acceptance = acceptance * 1;
					} else if (ranks.get(k) == "safety") {
						safety = safety * 1;
					} else if (ranks.get(k) == "minority") {
						minority = minority * 1;
					} else if (ranks.get(k) == "learning") {
						learning = learning * 1;
					}
				}
				k = k + 1;
			}

			float average_value = acceptance + safety + minority + learning;

			i = i + 1;
			Map<String, Float> average;
			if (program.getAverage() == null) {
				average = new HashMap<>();
				average.put(email, average_value);
				program.setAverage(average);
			} else {
				average = program.getAverage();
				if (average.get(email) == null) {
					average.put(email, average_value);
					program.setAverage(average);
				} else {
					// I don't think anything needs to be done here? Average is already set... or do we want to replace the old value?
					average.put(email, average_value);
					program.setAverage(average);
				}				
			}
		}
		// Collections.sort(programData);
		Sort sort = new Sort(email);
		sort.mergeSort(programData, 0, programData.size() - 1);
		return programData;
	}

    public Object handle(Request request, Response response) {
        String email = request.queryParams("email");

//        if ((email == null)) {
//            response.status(400);
//            // some sort of error
//            return new ViewData.ViewFailureResponse("error_bad_json: ", "missing keyword or country", email).serialize();
//        }

        Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.WARNING);

        // TODO changed hardcoded connection string so it doesn't contain the password
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
        	return new ViewData.ViewFailureResponse("Unable to connect to the MongoDB instance due to an error", me.getMessage(), email).serialize();
        }

        // MongoDatabase defines a connection to a specific MongoDB database
        MongoDatabase database = mongoClient.getDatabase(dbName);

        // MongoCollection defines a connection to a specific collection of documents in a specific database
        MongoCollection<ProgramData> collection = database.getCollection(collectionName, ProgramData.class);
		MongoCollection<UserData> collectionUsr = database.getCollection(collectionNameUsr, UserData.class);

		Bson filterUsr;
		List<UserData> resultsUsr = new ArrayList<>();
		if (email != null) {
			filterUsr = Filters.and(
					Filters.eq("email", email)
			);
			collectionUsr.find(filterUsr).forEach(resultsUsr::add);
		}

		// TODO: FILTERING
//		Bson filter = Filters.and(
//			Filters.ne("location", resultsUsr.get(0).getCountries())
//		);
		List<ProgramData> results = new ArrayList<>();
	    collection.find().forEach(results::add);

		if (email != null) {
			List<ProgramData> sorted = sortProgramData(email, results, resultsUsr);
			return new ViewData.ViewSuccessResponse("success", sorted, email);
		} else {

			// Sort results
			return new ViewData.ViewSuccessResponse("success", results, "").serialize();
		}
    }

    /**
	 * SearchSuccessResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param data - the data we are returning
	 */
	public record ViewSuccessResponse(String result, List<ProgramData> data, String email) {
		public ViewSuccessResponse(List<ProgramData> data, String email) {
			this("success", data, email);
		}
		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			try {
				// Just like in SoupAPIUtilities.
				//   (How could we rearrange these similar methods better?)
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<ViewData.ViewSuccessResponse> adapter = moshi.adapter(
						ViewData.ViewSuccessResponse.class);
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
	public record ViewFailureResponse(String result, String exception, String email) {
		//result should contain error and error code
		public ViewFailureResponse(String result, String exception, String email) {
			this.result = result;
			this.exception = exception;
			this.email = email;
		}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			Moshi moshi = new Moshi.Builder().build();
			return moshi.adapter(ViewData.ViewFailureResponse.class).toJson(this);
		}
	}

	public class Sort {
		private String email;

		public Sort(String email) {
			this.email = email;
		}

		private void merge(List<ProgramData> arr, int left, int middle, int right)
		{
			int low = middle - left + 1;                    //size of the left subarray
			int high = right - middle;                      //size of the right subarray
	
			List<ProgramData> L = new ArrayList<>();                             //create the left and right subarray
			List<ProgramData> R = new ArrayList<>();

			int i = 0, j = 0;
	
			for (i = 0; i < low; i++)                               //copy elements into left subarray
			{
				L.add(i, arr.get(left + i));
			}
			for (j = 0; j < high; j++)                              //copy elements into right subarray
			{
				R.add(j, arr.get(middle+1+j));
			}
			
	
			int k = left;                                           //get starting index for sort
			i = 0;                                             //reset loop variables before performing merge
			j = 0;

			while (i < low && j < high)                     //merge the left and right subarrays
			{
				float left_val;
				Map<String, Float> left_avg = L.get(i).getAverage();
				if (left_avg == null) {
					left_val = -1;
				} else {
					if (left_avg.get(this.email) == null) {
						left_val = -1;
					} else {
						left_val = left_avg.get(this.email);
					}
				}

				float right_val;
				Map<String, Float> right_avg = R.get(j).getAverage();
				if (right_avg == null) {
					right_val = -1;
				} else {
					if (right_avg.get(this.email) == null) {
						right_val = -1;
					} else {
						right_val = right_avg.get(this.email);
					}
				}
				if (left_val <= right_val) {
					arr.set(k, L.get(i));
					i++;
				} else {
					arr.set(k, R.get(j));
					j++;
				}
				k++;
			}
	
			while (i < low)                             //merge the remaining elements from the left subarray
			{
				arr.set(k, L.get(i));
				i++;
				k++;
			}
	
			while (j < high)                           //merge the remaining elements from right subarray
			{
				arr.set(k, R.get(j));
				j++;
				k++;
			}
		}
	

		public void mergeSort(List<ProgramData> arr, int left, int right)       //helper function that creates the sub cases for sorting
		{
			int middle;
			if (left < right) {                             //sort only if the left index is lesser than the right index (meaning that sorting is done)
				middle = (left + right) / 2;
	
				mergeSort(arr, left, middle);                    //left subarray
				mergeSort(arr, middle + 1, right);               //right subarray
	
				merge(arr, left, middle, right);                //merge the two subarrays
			}
		}
	
		// void display(int arr[])                 //display the array
		// {  
		// 	for (int i=0; i<arr.length; ++i) 
		// 	{
		// 		System.out.print(arr[i]+" ");
		// 	} 
		// } 

		// public static void main(String args[])
		// {
		// 	int arr[] = { 9, 3, 1, 5, 13, 12 };
		// 	Sort ob = new Sort();
		// 	ob.mergeSort(arr, 0, arr.length - 1);
		// 	ob.display(arr);
		// }
	}

    // Acceptance of participants and surrounding communities (acceptance)
    // Safety of campus and area (safety)
    // Accepting towards minorities (minority)
    // How much did you learn (learning)
    // Overall score
    // Comment
	public static class ProgramData {
		private String name;
		private String link;
        private String location;
        private Map<String, Map<String, Integer>> userScores;
        private Map<String, String> comment;
		private Map<String, Float> average;
		// private String email;

		public ProgramData(String name, String link, String location,
            Map<String, Map<String, Integer>> userScores, Map<String, String> comment, Map<String, Float> average) {
			this.name = name;
			this.link = link;
			this.location = location;
            this.userScores = userScores;
            this.comment = comment;
			this.average = average;
			// this.email = email;
		}

		public ProgramData() {
			link = "";
			name = "";
			location = "";
            userScores = new HashMap<>();
            comment = new HashMap<>();
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
		public Map<String, String> getComment() {
			return comment;
		}

		// Setter for comment
		public void setComment(Map<String, String> comment) {
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

		// // Getter for comment
		// public String getEmail() {
		// 	return email;
		// }

		// // Setter for comment
		// public void setEmail(String email) {
		// 	this.email = email;
		// }

		// @Override
		// public int compareTo(ProgramData program) {
		// 	//let's sort the employee based on an id in ascending order
		// 	//returns a negative integer, zero, or a positive integer as this employee id
		// 	//is less than, equal to, or greater than the specified object.
		// 	if (this.getAverage() == null) {
		// 		return -1;
		// 	} else {
		// 		return Math.round(this.getAverage().get(this.getEmail()) - program.getAverage().get(program.getEmail()));
		// 	}
		// }
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
