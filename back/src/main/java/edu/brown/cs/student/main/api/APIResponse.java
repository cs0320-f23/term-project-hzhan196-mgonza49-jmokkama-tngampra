package edu.brown.cs.student.main.api;

/**
 * Custom class that defines the fields that a response from an API should contain
 */
public class APIResponse {

  //string format but will be a number
  public String stateCode;

  //string format but will be a number
  public String countyCode;

  public String timestamp;

  public String percentage;

  /**
   * Constructor for APIResponse
   * @param stateCode - the code corresponding to a state
   * @param countyCode - the code corresponding to a county
   * @param timestamp - the timestamp that a request was made
   * @param percentage - the percentage of households with broadband access.
   */
  public APIResponse(String stateCode, String countyCode, String timestamp, String percentage) {
    this.stateCode = stateCode;
    this.countyCode = countyCode;
    this.timestamp = timestamp;
    this.percentage = percentage;
  }
}


