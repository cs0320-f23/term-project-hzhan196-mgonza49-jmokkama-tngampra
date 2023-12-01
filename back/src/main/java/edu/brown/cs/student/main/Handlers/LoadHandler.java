package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVClasses.Parse;
import edu.brown.cs.student.main.creators.CreatorFromRow;
import edu.brown.cs.student.main.creators.CreatorFromRowString;
import java.util.ArrayList;
import java.util.Objects;
import spark.Route;
import java.io.IOException;
import spark.Request;
import spark.Response;
import java.io.FileReader;
import java.util.List;
import java.util.Set;


/**
 * Handler class for handling the /loadcsv endpoint, implements Route
 */
public class LoadHandler implements Route {

	//TODO: error if no filepath is passed in when load endpoint is called
	private List<List<String>> parsedRows = new ArrayList<>();

	public Parse<List<String>> parser;

	//only set to true when successful load occurs
	public Boolean loadCalled;


	/**
	 * Constructor that initializes loadCalled as false.
	 */
	public LoadHandler() {
		//System.out.println("Load handler started");
    this.loadCalled = false;
  	}

	/**
	 * getter that returns the value of loadCalled.
	 */
	public Boolean getLoadCalled() {
		return loadCalled;
	}

	/**
	 * Handles the load api call.
	 * @param request the request to handle, contains the query parameters input.
	 * @param response use to modify properties of the response
	 * @return response object, can be success or failure
	 */
	@Override
	public Object handle(Request request, Response response) {
		//defensive programming, checking request
		Set<String> parameters = request.queryParams();
		if (!parameters.contains("filepath")) {
			String error = "error_bad_json: Filepath must be passed in";
			return new LoadFailureResponse(error, "", "").serialize();
		}
		if (!parameters.contains("hasHeader")) {
			String error = "error_bad_json: hasHeader must be passed in";
			return new LoadFailureResponse(error, "", "").serialize();
		}
		if (parameters.size() > 2) {
			String error = "error_bad_json: too many parameters passed in";
			return new LoadFailureResponse(error, "", "").serialize();

		}
		if (parameters.size() < 2) {
			String error = "error_bad_json: too little parameters passed in";
			return new LoadFailureResponse(error, "", "").serialize();

		}

		//restricting to data from the data folder
		String filepath = request.queryParams("filepath");

		//deals with header functionality
		//must be Y or N
		String headersBool = request.queryParams("hasHeader");
		boolean hasHeader = headersBool.equalsIgnoreCase("y");

    //return serialized filepath and success message if successful
		try {
			FileReader filereader = new FileReader(filepath);
			CreatorFromRow<List<String>> stringRowCreator = new CreatorFromRowString();

			//parsed rows
			this.parser = new Parse<>(filereader, stringRowCreator, hasHeader);
			this.parsedRows = this.parser.getParsedRows();
			this.loadCalled = true;
			return new LoadSuccessResponse(filepath, headersBool).serialize();

		}
		//return error and serialized filepath if unsuccessful
		catch (IOException e) {
			String error = "error_datasource: No such file or directory";

			return new LoadFailureResponse(error, filepath, headersBool).serialize();
		}
	}


	/**
	 * Getter that returns parsedRows
	 */
	public List<List<String>> getParsedRows() {
		return parsedRows;
	}

	//stencil from gearup code
	/**
	 * LoadSuccessResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param filepath - the filepath of the file to be parsed
	 * @param hasHeader - boolean that tells us whether the data has headers
	 */
	public record LoadSuccessResponse(String result, String filepath, String hasHeader) {
		public LoadSuccessResponse(String filepath, String hasHeader) {
			this("success", filepath, hasHeader);
		}
		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			try {
				// Just like in SoupAPIUtilities.
				//   (How could we rearrange these similar methods better?)
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<LoadSuccessResponse> adapter = moshi.adapter(LoadSuccessResponse.class);
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
	 * LoadFailureResponse - a class of type record that contains the response we want to return
	 * @param result - a string, can be success or failure
	 * @param filepath - the filepath of the file to be parsed
	 * @param hasHeader - boolean that tells us whether the data has headers
	 */
	public record LoadFailureResponse(String result, String filepath, String hasHeader) {
		public LoadFailureResponse(String result, String filepath, String hasHeader) {
			this.result = result;
			this.filepath = filepath;
			this.hasHeader = hasHeader;
			}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			Moshi moshi = new Moshi.Builder().build();
			return moshi.adapter(LoadFailureResponse.class).toJson(this);
		}
	}

}
