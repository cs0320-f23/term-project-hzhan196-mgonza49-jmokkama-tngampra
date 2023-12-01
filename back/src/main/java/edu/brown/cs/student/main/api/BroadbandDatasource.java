package edu.brown.cs.student.main.api;

import edu.brown.cs.student.main.api.APIResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * An object that can be used to make a variety of API calls
 */
public interface BroadbandDatasource {
	APIResponse getData(Pair<String,String> query_in) throws IOException;

	List<List<String>> getStateCodeList() throws IOException;

	List<List<String>> getCountyCodeList(String state, HashMap<String, String> stateCodes_in) throws IOException;
}
