package edu.brown.cs.student.main.CSVClasses;

import edu.brown.cs.student.main.creators.CreatorFromRow;
import edu.brown.cs.student.main.creators.CreatorFromRowString;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {}

  private void run() {
    // read in user input
    Scanner scanner = new Scanner(System.in);

    // get file path
    System.out.println("Enter CSV file path: ");
    String filepath = scanner.nextLine();

    // input information structure developed during debugging hours
    try {
      List<List<String>> resultsfound;

      // check if header exists
      System.out.println("Headers in CSV? (Y/N): ");
      String headerIndicator = scanner.nextLine();
      // if user enters y or n or Y or N, should be the same
      boolean headerExistence = false;

      if (headerIndicator.equalsIgnoreCase("y")) {
        headerExistence = true;
      }

      // read in the file and create parse object
      FileReader fileReader = new FileReader(filepath);

      // row creator object for strings
      CreatorFromRow<List<String>> stringRowCreatorMain = new CreatorFromRowString();
      Parse myParse = new Parse(fileReader, stringRowCreatorMain, headerExistence);

      System.out.println("Enter search keywords (case sensitive): ");
      String keywords = scanner.nextLine();

      System.out.println(
          "Search inside column name, index, or nothing? Enter C, I, or N respectively: ");
      String searchMethod = scanner.nextLine();

      // search by column name
      if (searchMethod.equalsIgnoreCase("C")) {
        System.out.println("Enter column name to search in (not case sensitive): ");
        String columnName = scanner.nextLine();
        resultsfound = myParse.parseAndSearchCSV(-1, keywords, columnName);

        // search by column index
      } else if (searchMethod.equalsIgnoreCase("I")) {
        System.out.println("Enter column index to search in: ");
        int columnIndex = Integer.parseInt(scanner.nextLine());
        resultsfound = myParse.parseAndSearchCSV(columnIndex, keywords, null);

        // no narrowing search
      } else {
        resultsfound = myParse.parseAndSearchCSV(-1, keywords, null);
      }

      // checks if empty and informs user
      if (resultsfound.isEmpty()) {
        System.out.println("No results found");
      } else {
        System.out.println("Results: ");
        // printing out results
        for (List<String> row : resultsfound) {
          System.out.println(row);
        }
      }

    }

    // catch exceptions thrown in parse and catch classes here
    catch (Exception e) {
      System.err.println(e);
      // stack trace printing for testing and debugging purposes
      // e.printStackTrace();
    }
  }
}
