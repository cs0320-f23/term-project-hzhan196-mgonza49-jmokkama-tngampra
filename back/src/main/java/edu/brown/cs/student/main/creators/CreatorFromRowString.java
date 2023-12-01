package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.CSVClasses.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into a List of
 * Strings. <List<String>> - what is created
 *
 * @param
 */
public class CreatorFromRowString implements CreatorFromRow<List<String>> {
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    // System.out.println("row: " + row);
    List<String> newRow = new ArrayList<>();
    for (String word : row) {
      newRow.add(word.trim());
    }
    return newRow;
  }
}
