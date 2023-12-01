package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.CSVClasses.FactoryFailureException;
import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into a number
 * that counts the number of characters.
 *
 * @param
 */
// idea developed during debugging hours with peers
public class CreatorFromRowCharCount implements CreatorFromRow<Integer> {
  @Override
  public Integer create(List<String> row) throws FactoryFailureException {
    Integer count = 0;
    for (int i = 0; i < row.size(); i++) {
      count += row.get(i).length();
    }
    return count;
  }
}
