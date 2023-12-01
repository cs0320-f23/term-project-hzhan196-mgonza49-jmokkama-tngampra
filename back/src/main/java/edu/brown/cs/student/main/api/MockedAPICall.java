package edu.brown.cs.student.main.api;

import org.apache.commons.lang3.tuple.Pair;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * A datasource that never actually calls the ACS API, but always returns a constant
 * dataset depending on which getter is called. This is very useful in testing, and avoiding the costs of
 * real API invocations. The technique is called "mocking".
 */
public class MockedAPICall implements BroadbandDatasource {
	private List<List<String>> stateList;
	private List<List<String>> countyList;
	private APIResponse response;

	/**
	 * Takes in mock data and saves into the object field
	 * @param countyList - list of counties
	 * @param response - mock response object
	 * @param stateList - list of states
	 */
	public MockedAPICall(List<List<String>> stateList, List<List<String>> countyList, APIResponse response) {
		this.stateList = stateList;
		this.countyList = countyList;
		this.response = response;
	}

	/**
	 * Getter that returns mock APIResponse data
	 */
	public APIResponse getData(Pair<String, String> query_in) throws IOException{
		return this.response;
	}

	/**
	 * Getter that returns mock StateCodeList
	 */
	public List<List<String>> getStateCodeList() throws IOException{
		return this.stateList;
	}

	/**
	 * Getter that returns mock CountyCodeList
	 */
	public List<List<String>> getCountyCodeList(String state, HashMap<String, String> stateCodes_in) throws IOException{
		return this.countyList;
	}

}


