package edu.brown.cs.student.main.Handlers;

import edu.brown.cs.student.main.api.APIResponse;
import edu.brown.cs.student.main.api.BroadbandDatasource;
import edu.brown.cs.student.main.api.Cache;
import org.apache.commons.lang3.tuple.Pair;
import spark.Route;
import java.io.IOException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Properties;

/**
 * Handler class for handling the /broadband endpoint, implements Route
 */
public class BroadbandHandler implements Route {

	private static HashMap<String, String> stateCodes = new HashMap<>();

	//making cache static so that there is one no matter how many times broadband class is created
	private static Cache cache;

	private BroadbandDatasource apiCall;
	private int cacheSize;

	static {
		cache = new Cache(1, 10, null);
	}

	/**
	 * Getter that returns the value contained in stateCodes.
	 */
	public HashMap<String, String> getStateCodes() {
		return stateCodes;
	}

	/**
	 * Constructor that takes in a BroadbandDatasource object and integer size.
	 * @param apiCall - the object use to make API calls, can be mock or real
	 * @param size - an integer denoting the size of the cache.
	 */
	public BroadbandHandler(BroadbandDatasource apiCall, int size) {
		try {
			//put this as static variable
			this.apiCall = apiCall;
			this.saveStateCode();
			this.cacheSize = size;

			updateCacheWithApiCall();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Function to set the static cache variable to a new cache object with
	 * custom cacheSize and apiCall object.
	 */
	private void updateCacheWithApiCall() throws IOException {
		// Update the cache with the apiCall parameter
		cache = new Cache(this.cacheSize, 10, this.apiCall);
	}

	/**
	 * Handles the broadband api call.
	 * @param request the request to handle, contains the query parameters input.
	 * @param response use to modify properties of the response
	 * @return response object, can be success or failure
	 */
	@Override
	public Object handle(Request request, Response response) {
		String state = request.queryParams("state");
		String county = request.queryParams("county");

		InputParams inputParams = new InputParams(state, county);

		if (request.queryParams().size() != 2) {
			return new BroadbandFailureResponse("Missing state or county in query", inputParams).serialize();
		}

		try {
			// check for valid state
			if (!(stateCodes.containsKey(state))) {
				throw new IOException("Input state: " + state + " does not exist");
			}

			String stateCode = stateCodes.get(state);

			String countyCode = getCountyCode(state, county);
			//check cache, if cache returns null then we call get data and store this result in the cache

			Pair<String, String> stateCountyPair = Pair.of(stateCode, countyCode);
			APIResponse result = cache.search(stateCountyPair);

			return new BroadbandSuccessResponse(result, inputParams).serialize();
		} catch (Exception e) {
			return new BroadbandFailureResponse(e.getMessage(), inputParams).serialize();
		}
	}

	/**
	 * saveStateCode - saves the state codes list into a hashmap
	 * 				    that maps String stateName -> String code
	 * @throws IOException - when the IOException is propagated from the apiCall object
	 * 					and states were failed to be saved.
	 */
	private void saveStateCode() throws IOException {
		List<List<String>> codes = this.apiCall.getStateCodeList();
		if (codes == null) {
			throw new IOException("State codes have not been saved");
		}

		for (List<String> codeList : codes) {
			if (codeList.get(0).equals("NAME") && codeList.get(1).equals("state")) {
				continue;
			}
			stateCodes.put(codeList.get(0), codeList.get(1));
		}
	}

	/**
	 * getCountyCode - a function to return the code corresponding to an input county located in
	 * 				   the input state.
	 * @param state - the state that the county is in.
	 * @param county - the county that we are trying to find the code for.
	 * @throws IOException - when the county is not in the input state (we know the input state is valid
	 * 						 because we checked that before calling this function).
	 */
	private String getCountyCode(String state, String county) throws IOException {
		List<List<String>> codes = this.apiCall.getCountyCodeList(state, stateCodes);

		for (List<String> codeList : codes) {
			if (codeList.get(0).equalsIgnoreCase(county)) {
				return codeList.get(2);
			}
		}
		throw new IOException("Input county: " + county + " does not exist in " + state);
	}


	/**
	 * BroadbandFailureResponse - a class of type record that contains the response we want to return
	 * @param result - a string, in this case "error_bad_json: " because this is a failure response
	 *               caused by faulty input.
	 * @param inputParams - an InputParams object that contains the query parameters State and County
	 * @param exception - the Java exception message that caused the failure
	 */
	public record BroadbandFailureResponse(String result, InputParams inputParams, String exception) {

		public BroadbandFailureResponse(String exception, InputParams inputParams) {
			this("error_bad_json: ", inputParams, exception);
		}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			Moshi moshi = new Moshi.Builder().build();
			return moshi.adapter(BroadbandHandler.BroadbandFailureResponse.class).toJson(this);
		}
	}

	/**
	 * BroadbandSuccessResponse - a class of type record that contains the response we want to return
	 * @param result - a string, in this case success because this is a success response
	 * @param data - an APIResponse object containing the data we want to return
	 * @param inputParams - an InputParams object that contains the query parameters State and County
	 */
	public record BroadbandSuccessResponse(String result, APIResponse data, InputParams inputParams) {

		public BroadbandSuccessResponse(APIResponse data, InputParams inputParams) {
			this("success", data, inputParams);
		}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			try {
				// Just like in SoupAPIUtilities.
				//   (How could we rearrange these similar methods better?)
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<BroadbandHandler.BroadbandSuccessResponse> adapter = moshi.adapter(
						BroadbandHandler.BroadbandSuccessResponse.class);
				return adapter.toJson(this);
			} catch (Exception e) {
				// For debugging purposes, show in the console _why_ this fails
				// Otherwise we'll just get an error 500 from the API in integration
				// testing.
				e.printStackTrace();
				throw e;
			}
		}
	}
	public record InputParams(String state, String county) { }

}

