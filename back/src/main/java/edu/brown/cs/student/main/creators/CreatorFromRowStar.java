package edu.brown.cs.student.main.creators;

import edu.brown.cs.student.main.CSVClasses.FactoryFailureException;
import edu.brown.cs.student.main.RedlineObjects.Star;

import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into a number
 * that counts the number of characters.
 *
 * @param
 */
// idea developed during debugging hours with peers
public class CreatorFromRowStar implements CreatorFromRow<Star> {
  @Override
  public Star create(List<String> row) throws FactoryFailureException {
    return new Star(
        Integer.parseInt(row.get(0)),
        row.get(1),
        Double.parseDouble(row.get(2)),
        Double.parseDouble(row.get(3)),
        Double.parseDouble(row.get(4)));
  }
}
