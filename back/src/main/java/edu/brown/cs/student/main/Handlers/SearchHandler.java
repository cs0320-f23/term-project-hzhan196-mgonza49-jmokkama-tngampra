package edu.brown.cs.student.main.Handlers;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Handlers.LoadHandler.LoadFailureResponse;
import edu.brown.cs.student.main.Handlers.ViewHandler.ViewFailureResponse;
import edu.brown.cs.student.main.Handlers.ViewHandler.ViewSuccessResponse;

import java.util.Objects;
import spark.Route;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.List;
import spark.Request;
import spark.Response;

/**
 * Handler class for handling the /searchcsv endpoint, implements Route
 */
public class SearchHandler implements Route{
	public LoadHandler loadHandler;

	/**
	 * Constructor of SearchHandler
	 * @param loadHandler_in - the LoadHandler object.
	 */
	public SearchHandler(LoadHandler loadHandler_in) {
		//System.out.println("View handler started");
		this.loadHandler = loadHandler_in;
	}

	/**
	 * Handles the search api call.
	 * @param request the request to handle, contains the query parameters input.
	 * @param response use to modify properties of the response
	 * @return serialized response object, can be success or failure
	 */
	@Override
	public Object handle(Request request, Response response) {


		Set<String> parameters = request.queryParams();
		if (!parameters.contains("columnIndex") || !parameters.contains("keyword") || !parameters.contains("headerName")) {
			String error = "error_bad_json";
			String exception = "One or more parameters are missing";
			return new SearchFailureResponse(error, exception, "", "", "").serialize();
		}
		if (parameters.size() != 3) {
			String error = "error_bad_request";
			String exception = "Too many or too little requests are passed in";
			return new SearchFailureResponse(error, exception, "", "", "").serialize();

		}


		String columnSearchIndexString = request.queryParams("columnIndex");
		String keyword = request.queryParams("keyword");
		String header = request.queryParams("headerName");

		if (Objects.equals(keyword, "")) {
			String error = "error_bad_request";
			String exception = "Need keyword input to search in csv";
			return new SearchFailureResponse(error, exception, columnSearchIndexString, keyword, header).serialize();

		}

		try {
			if (loadHandler.loadCalled) {
				Integer columnSearchIndex = Integer.parseInt(columnSearchIndexString);
			List<List<String>> matches = this.loadHandler.parser.parseAndSearchCSV(columnSearchIndex,
					keyword, header);
			return new SearchSuccessResponse(matches, columnSearchIndexString, keyword, header).serialize();
			}
			//return error if unsuccessful
			else {
				String error = "error_bad_request";
				String exception = "loadcsv endpoint must be called first";
				return new SearchFailureResponse(error, exception, columnSearchIndexString, keyword, header).serialize();
			}
		}
		//check if column search index is an int - add defensive programming
		catch (NumberFormatException e) {
			String error = "error_bad_request";
			String exception = "The column index is not a valid integer";
			return new SearchFailureResponse(error, exception, columnSearchIndexString, keyword, header).serialize();
		}
		catch (Exception e) {
			String error = "error_bad_json";
			return new SearchFailureResponse(error, e.getMessage(), columnSearchIndexString, keyword, header).serialize();

		}
	}

	//stencil from gearup code
	/**
	 * SearchSuccessResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param data - the data we are returning
	 */
	public record SearchSuccessResponse(String result, List<List<String>> data, String columnIndex, String keyword, String headerName) {
		public SearchSuccessResponse(List<List<String>> data, String columnIndex, String keyword, String headerName) {
			this("success", data, columnIndex, keyword, headerName);
		}
		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			try {
				// Just like in SoupAPIUtilities.
				//   (How could we rearrange these similar methods better?)
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<SearchHandler.SearchSuccessResponse> adapter = moshi.adapter(
						SearchHandler.SearchSuccessResponse.class);
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
	public record SearchFailureResponse(String result, String exception, String columnIndex, String keyword, String headerName) {
		//result should contain error and error code
		public SearchFailureResponse(String result, String exception, String columnIndex, String keyword, String headerName) {
			this.result = result;
			this.exception = exception;
			this.columnIndex = columnIndex;
			this.keyword = keyword;
			this.headerName = headerName;
		}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			Moshi moshi = new Moshi.Builder().build();
			return moshi.adapter(SearchHandler.SearchFailureResponse.class).toJson(this);
		}
	}
}
