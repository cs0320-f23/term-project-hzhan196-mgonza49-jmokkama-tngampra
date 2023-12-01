package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonReader;
import edu.brown.cs.student.main.CSVClasses.Parse;
import edu.brown.cs.student.main.RedlineObjects.CoordCache;
import edu.brown.cs.student.main.RedlineObjects.CoordinateKey;
import edu.brown.cs.student.main.RedlineObjects.Filterer;
import edu.brown.cs.student.main.RedlineObjects.Filterer.Feature;
import edu.brown.cs.student.main.RedlineObjects.Filterer.FeatureCollection;
import edu.brown.cs.student.main.RedlineObjects.Filterer.Geometry;
import edu.brown.cs.student.main.RedlineObjects.Filterer.Property;
import edu.brown.cs.student.main.api.Cache;
import edu.brown.cs.student.main.creators.CreatorFromRow;
import edu.brown.cs.student.main.creators.CreatorFromRowString;
import spark.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okio.Okio;
import okio.BufferedSource;


import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSVClasses.Parse;
import edu.brown.cs.student.main.creators.CreatorFromRow;
import edu.brown.cs.student.main.creators.CreatorFromRowString;

import java.io.IOException;
import spark.Request;
import spark.Response;
import java.io.FileReader;
import java.util.List;
import java.util.Set;
import java.lang.Math;



public class RedLineHandler implements Route {
	private FeatureCollection featureCollection;
	private Filterer filterer;

	private static CoordCache cache;

	private HashMap<String, FeatureCollection> areaMap;

	static {
		cache = new CoordCache(1, 10, null);
	}

	public RedLineHandler(String filepath) {
		try {
			this.filterer = new Filterer(filepath);
			updateCacheWithApiCall();
			this.areaMap = new HashMap<>();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


	/**
	 * Function to set the static cache variable to a new cache object with
	 * custom cacheSize and apiCall object.
	 */
	private void updateCacheWithApiCall() throws IOException {
		// Update the cache with the apiCall parameter
		cache = new CoordCache(10, 10, this.filterer);
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

		//min and max lat and long
		String minLat = request.queryParams("minLat");
		String minLong = request.queryParams("minLong");

		String maxLat = request.queryParams("maxLat");
		String maxLong = request.queryParams("maxLong");

		String keyword = request.queryParams("keyword");

		try {
		 if (!parameters.contains("minLat") && !parameters.contains("minLong")
				&& !parameters.contains("maxLat") && !parameters.contains("maxLong") ) {
			 FeatureCollection res;
			 if (!parameters.contains("keyword")) {
				 res = filterer.getAllData();
			 } else {
				 res = filterer.getAreaData(keyword);
				 this.areaMap.put(keyword, res);
			 }
			 return new RedLineSuccessResponse(res).serialize();
		 } else if (!parameters.contains("minLat") || !parameters.contains("minLong")
					|| !parameters.contains("maxLat") || !parameters.contains("maxLong")) {
			 if (!parameters.contains("keyword")) {
				 String error = "error_bad_json: One or more parameters of bounding box missing";
				 return new RedLineHandler.RedLineFailureResponse(error).serialize();
			 } else {
				 String error = "error_bad_json: Can only search by area or bounding box, not both!";
				 return new RedLineHandler.RedLineFailureResponse(error).serialize();
			 }
		 } else {
			 if (!parameters.contains("keyword")) {
				 double minLatParsed = Double.parseDouble(minLat);
				 double maxLatParsed = Double.parseDouble(maxLat);
				 double minLongParsed = Double.parseDouble(minLong);
				 double maxLongParsed = Double.parseDouble(maxLong);

				 if ((Math.abs(minLatParsed) > 90) || (Math.abs(maxLatParsed) > 90) || (Math.abs(minLongParsed) > 180) || (Math.abs(maxLongParsed) > 180)) {
					 String error = "error_bad_json: Latitude or longitude was out of bounds!";
					 return new RedLineHandler.RedLineFailureResponse(error).serialize();
				 }

				 if ((minLatParsed > maxLatParsed) || (minLongParsed > maxLongParsed)) {
					 String error = "error_bad_json: Minimum latitude or longitude greater than maximum!";
					 return new RedLineHandler.RedLineFailureResponse(error).serialize();
				 }

				 CoordinateKey coordinateKey = new CoordinateKey(minLatParsed, maxLatParsed, minLongParsed, maxLongParsed);

				 FeatureCollection res = cache.search(coordinateKey);
				 return new RedLineHandler.RedLineSuccessResponse(res).serialize();
			 } else {
				 String error = "error_bad_json: Can only search by area or bounding box, not both!";
				 return new RedLineHandler.RedLineFailureResponse(error).serialize();
			 }
		 }
		}
		//return error and serialized filepath if unsuccessful
		catch (Exception e) {
			String error = e.getMessage();
			System.out.println("Exception message: " + e.getMessage());
			return new RedLineHandler.RedLineFailureResponse(error).serialize();
		}
	}

	/**
	 * SearchSuccessResponse - a class of type record that contains the success response we want to return
	 * @param result - a string, can be success or failure
	 * @param featCollection - the feature collection to return
	 */
	public record RedLineSuccessResponse(String result, FeatureCollection featCollection) {
		public RedLineSuccessResponse(FeatureCollection featCollection) {
			this("success", featCollection);
		}
		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			try {
				// Just like in SoupAPIUtilities.
				//   (How could we rearrange these similar methods better?)
				Moshi moshi = new Moshi.Builder().build();
				JsonAdapter<RedLineHandler.RedLineSuccessResponse> adapter = moshi.adapter(
						RedLineHandler.RedLineSuccessResponse.class);
				return adapter.toJson(this);
			} catch(Exception e) {
				// For debugging purposes, show in the console _why_ this fails
				// Otherwise we'll just get an error 500 from the API in integration
				// testing.
				System.out.println("Exception message: " + e.getMessage());
				System.out.println(e.getMessage());
				throw e;
			}
		}
	}

	/**
	 * LoadFailureResponse - a class of type record that contains the response we want to return
	 * @param result - a string, can be success or failure
	 */
	public record RedLineFailureResponse(String result) {
		public RedLineFailureResponse(String result) {
			this.result = result;
		}

		/**
		 * @return this response, serialized as Json
		 */
		String serialize() {
			Moshi moshi = new Moshi.Builder().build();
			return moshi.adapter(RedLineHandler.RedLineFailureResponse.class).toJson(this);
		}
	}

}
