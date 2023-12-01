package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Handlers.LoadHandler.LoadFailureResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import spark.Route;
import spark.Request;
import spark.Response;

/**
 * Handler class for handling the /viewcsv endpoint, implements Route
 */
public class ViewHandler implements Route {
	public List<List<String>> parsedRows = new ArrayList<>();
	//public Boolean loadCalled = false;

	private LoadHandler loadHandler;

	/**
	 * Constructor of ViewHandler
	 * @param loadHandler_in - the LoadHandler object.
	 */
	public ViewHandler(LoadHandler loadHandler_in) {
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
		//return serialized parsed rows and success message if successful
		//System.out.println("View handler load called variable: " + loadHandler.loadCalled);

		Set<String> parameters = request.queryParams();
		if (!parameters.isEmpty()) {
			String error = "error_bad_request: no parameters can be passed in";
			return new ViewFailureResponse(error).serialize();
		}

		try {
			this.parsedRows = loadHandler.getParsedRows();

			if (loadHandler.loadCalled) {
				return new ViewSuccessResponse(this.parsedRows).serialize();
			}
			//return error if unsuccessful
			else {
				return new ViewFailureResponse("error_bad_request: loadcsv endpoint must be called first").serialize();
			}
		} catch (Exception e) {
			return new ViewFailureResponse("error_bad_request: loadcsv endpoint must be called first").serialize();
		}
	}

	//stencil from gearup code
	/**
	 * ViewSuccessResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param data - the data we are returning
	 */
	public record ViewSuccessResponse(String result, List<List<String>> data) {
		public ViewSuccessResponse(List<List<String>> data) {
			this("success", data);
		}
		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			try {
				// Just like in SoupAPIUtilities.
				//   (How could we rearrange these similar methods better?)
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<ViewHandler.ViewSuccessResponse> adapter = moshi.adapter(
						ViewHandler.ViewSuccessResponse.class);
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
	 * ViewFailureResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or error
	 */
	public record ViewFailureResponse(String result) {
		public ViewFailureResponse(String result) {
			this.result = result;
		}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			Moshi moshi = new Moshi.Builder().build();
			return moshi.adapter(ViewHandler.ViewFailureResponse.class).toJson(this);
		}
	}
}
