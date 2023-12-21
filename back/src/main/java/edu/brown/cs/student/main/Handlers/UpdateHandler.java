package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.Objects;
import spark.Route;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Handler class for handling the /loadcsv endpoint, implements Route
 */
public class UpdateHandler implements Route {
	private String path;
	private String envPath;
	/**
	 * Constructor that initializes loadCalled as false.
	 */
	public UpdateHandler(String path, String envPath) {
		this.path = path;
		this.envPath = envPath;
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
		if (parameters.size() > 0) {
			String error = "error_bad_json: please don't put in any parameters";
			return new LoadFailureResponse(error, "", "").serialize();
		}

        //return serialized filepath and success message if successful
        String path = this.path;
        // "./back/src/main/java/edu/brown/cs/student/main/Handlers/python.py"
        // "./back/src/main/scraping/LoadPrograms.py";

		try {
			String env_path = this.envPath;
            String command = env_path + " " + path;
            Process p = Runtime.getRuntime().exec(command);
            TimeUnit.SECONDS.sleep(60);
            // p.waitFor();
            InputStream stdout = p.getInputStream();

            BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
            String line;
            while ((line = reader.readLine ()) != null) {
                System.out.println ("Stdout: " + line);
            }
            reader.close();
            // System.out.println("here!");

            // System.out.println(p.getOutputStream());
            return new LoadSuccessResponse(path, "").serialize();
		}
		//return error and serialized filepath if unsuccessful
		catch (IOException e) {
			String error = "error_datasource: Cannot find python file";
			return new LoadFailureResponse(error, path, "").serialize();
		}
        catch (InterruptedException e) {
            String error = "thread interrupted: pleas try running again";
			return new LoadFailureResponse(error, path, "").serialize();
        }
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
