package edu.brown.cs.student.main.CSVClasses;

import java.util.ArrayList;
import java.util.List;

/** Search - searching through the parsed rows from the CSV class to find any inputted keywords */
public class Search {

  private List<List<String>> data; // the parsed rows from the parser class

  private List<List<String>> matches = new ArrayList<>(); // to hold any results from searching

  /**
   * Search - constructor
   *
   * @param data_in - the parsed rows from the CSV parsing class
   */
  public Search(List<List<String>> data_in) {
    this.data = data_in;
  }

  /**
   * getMatches - so other files can access the matches
   *
   * @return - matches from keyword in csv
   */
  public List<List<String>> getMatches() {
    return this.matches;
  }

  // overloaded searching methods - mimicking optional parameters
  /**
   * searchCSV - check full CSV for the keyword the user is searching for
   *
   * @param keyword - the word the user is looking to find in the csv data
   */
  public void searchCSV(String keyword) {

    // go through each row in the narrowed csv data
    for (int i = 0; i < this.data.size(); i++) {
      List<String> row = this.data.get(i);
      // iterate through the row to check each word if it matches the keyword
      for (int j = 0; j < row.size(); j++) {
        String word = row.get(j);
        // checks if words match, not case-sensitive
        if (word.contains(keyword)) {
          this.matches.add(row);
          // break if multiple words/phrases that match keyword in same row, dont want to add same
          // row multiple times
          break;
        }
      }
    }
  }

  /**
   * searchCSV - find CSV for keyword matches with a specific column index identifier
   *
   * @param keyword - the word the user is looking to find in the csv data
   * @param columnIndex - the index of the column to narrow search on
   */
  public void searchCSV(String keyword, int columnIndex) throws IllegalArgumentException{
    // check if column index is valid
    if (columnIndex < 0 || columnIndex > this.data.get(0).size() - 1) {
      throw new IllegalArgumentException(
          "Column index is invalid. Must be between 0 and the number of columns.");
    }
    // at this point column index is valid

    // iterate through rows
    for (int i = 0; i < this.data.size(); i++) {
      List<String> row = this.data.get(i);
      String word = row.get(columnIndex);
      if (word.contains(keyword)) {
        this.matches.add(row);
      }
    }
  }

  // primary methods of this class, uses helper methods above

  /**
   * narrowAndConductSearch - narrows search if applicable and then searches CSV
   *
   * @param keyword_in - the word the user is looking to find in the csv data
   */
  public void narrowAndConductSearch(String keyword_in) {
    this.searchCSV(keyword_in);
    // matches will be in matches data structure after this
  }

  /**
   * narrowAndConductSearch - narrows search if applicable and then searches CSV
   *
   * @param keyword_in - the word the user is looking to find in the csv data
   * @param columnIndex_in - the index of the column to narrow search on
   */
  public void narrowAndConductSearch(String keyword_in, int columnIndex_in) {

    this.searchCSV(keyword_in, columnIndex_in);
    // matches will be in matches data structure after this
  }

  /**
   * narrowAndConductSearch - narrows search if applicable and then searches CSV
   *
   * @param keyword_in - the word the user is looking to find in the csv data
   * @param columnHeader_in - the header name to narrow search on
   * @param headers_in - all of the headers in the CSV data
   */
  public void narrowAndConductSearch(
      String keyword_in, String columnHeader_in, List<String> headers_in) {
    // find index of column header and then pass that index into the searchCSV function
    int columnIndex = -1;
    for (int i = 0; i < headers_in.size(); i++) {
      String columnName = headers_in.get(i);
      if (columnName.equalsIgnoreCase(columnHeader_in)) {
        columnIndex = i;
        // only gets index of first column that matches desired column header
        break;
      }
    }

    if (columnIndex == -1) {
      throw new IllegalArgumentException("The CSV does not contain the specified header.");
    }
    // checks if function is invoked and headers do not exist
    if (headers_in.isEmpty()) {
      throw new IllegalArgumentException("The CSV does not have headers.");
    }

    this.searchCSV(keyword_in, columnIndex);
    // matches will be in matches data structure after this
  }
}
