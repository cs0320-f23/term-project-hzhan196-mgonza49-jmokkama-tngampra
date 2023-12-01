package edu.brown.cs.student.main.CSVClasses;

import edu.brown.cs.student.main.creators.CreatorFromRow;
import edu.brown.cs.student.main.creators.CreatorFromRowString;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * parses inputted CSV
 *
 * @param <T> - generic type T so any users can parse through any type of data as desired
 */
public class Parse<T> {
  private BufferedReader bufferedReader; // object to read file
  private CreatorFromRow<T>
      rowCreator; // using to figure out which object each row of the data should be
  private Boolean headerExists; // used to see if there is a header in this csv

  private List<T> parsedRows = new ArrayList<>(); // list of each parsed row
  private List<String> headers; // header which will be populated if the header exists

  // regex taken from class livecode & lecture notes from Professor Nelson to split csv rows
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  // constructor if there are headers
  // third argument is bool whether headers are present

  /**
   * Parse - constructor
   *
   * @param reader_in stream of the CSV to read data
   * @param rowCreator_in class to create objects from a row in a CSV
   * @param headerExists_in checks if CSV contains headers
   */
  public Parse(Reader reader_in, CreatorFromRow<T> rowCreator_in, Boolean headerExists_in) {
    this.bufferedReader = new BufferedReader(reader_in);
    this.rowCreator = rowCreator_in;
    this.headerExists = headerExists_in;

    // calls parse and any exceptions from file not opening or misreading CSV get caught and printed
    // here
    try {
      this.parseCSV();
    } catch (IOException | FactoryFailureException e) {
      System.err.println(e);
    }
  }

  /**
   * getParsedRows() - so parsed rows can be accessed from any callers such as Main or unit tests
   *
   * @return parsedRows - list containing objects from creator object
   */
  public List<T> getParsedRows() {
    return this.parsedRows;
  }

  /**
   * parseCSV() - parses the csv data to store in a csv parsing object to be searched after
   *
   * @throws FactoryFailureException if data cannot be read from the csv because the csv is invalid
   * @throws IOException if file is invalid for csv
   */
  public void parseCSV() throws FactoryFailureException, IOException {
    // first get header if it exists
    // if (this.headerExists) {
    //   // call row creator to get header as list of strings
    //   CreatorFromRow<List<String>> stringRowCreator = new CreatorFromRowString();

    //   String headerSingleLine = this.bufferedReader.readLine();

    //   // create object from reader passed in and add header var
    //   this.headers =
    //       stringRowCreator.create(Arrays.asList(regexSplitCSVRow.split(headerSingleLine)));
    // }

    // if header does not exist, loop through rest of csv
    int i = 0;
    String line = this.bufferedReader.readLine();
    while (line != null) {
      if (headerExists){
        if (i == 0){
          i++;
          CreatorFromRow<List<String>> stringRowCreator = new CreatorFromRowString();
          this.headers =  stringRowCreator.create(Arrays.asList(regexSplitCSVRow.split(line)));
        }
      }
      // splits line and creates into object based on row creator passed in
      T addToRow = this.rowCreator.create(Arrays.asList(regexSplitCSVRow.split(line)));
      // adds to parsed row member variable
      this.parsedRows.add(addToRow);
      // reads next line
      line = this.bufferedReader.readLine();
    }
  }

  /**
   * parseAndSearchCSV - calls search class to combine parse and search functionality
   *
   * @param columnSearchIndex - index to narrow searching functionality
   * @param keyword - word(s) that user is looking to find in CSV
   * @param headerToSearch - header String to narrow searching functionality
   * @return matches - a list of rows in the CSV where the keyword was contained
   * @throws IllegalArgumentException - if searching is not conducted on String objects
   */
  public List<List<String>> parseAndSearchCSV(
      int columnSearchIndex, String keyword, String headerToSearch)
      throws IllegalArgumentException {
    // column search index will be -1 if user does not want to search or will be valid

    List<List<String>> parsedRows_in;
    try {
      parsedRows_in = (List<List<String>>) this.parsedRows;
    } catch (ClassCastException e) {
      throw new IllegalArgumentException(
          "The CSV data must contain strings in order to search and the entered data does not.");
    }

    Search mySearch = new Search(parsedRows_in);
    if (this.headerExists) {
      // if header specified is not null then we will narrow search by header
      if (!Objects.equals(headerToSearch, "")) {
        mySearch.narrowAndConductSearch(keyword, headerToSearch, this.headers);
      }
      // no header to search by so we can search by column index or nothing
      else {
        // no column index specified so cannot narrow search
        if (columnSearchIndex == -1) {
          mySearch.narrowAndConductSearch(keyword);
        }
        // column index is specified
        else {
          mySearch.narrowAndConductSearch(keyword, columnSearchIndex);
        }
      }
    }
    // header does not exist
    else {
      if (columnSearchIndex == -1) {
        mySearch.narrowAndConductSearch(keyword);
      }
      // column index was specified
      else {
        mySearch.narrowAndConductSearch(keyword, columnSearchIndex);
      }
    }
    return mySearch.getMatches();
  }

  // for testing purposes

  /**
   * getSize - checks size of parsed rows list
   *
   * @return the size of the parsed rows list
   */
  public int getSize() {
    return this.parsedRows.size();
  }
}
