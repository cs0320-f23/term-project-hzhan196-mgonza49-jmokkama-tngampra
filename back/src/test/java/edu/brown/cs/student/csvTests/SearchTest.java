package edu.brown.cs.student.csvTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.main.CSVClasses.FactoryFailureException;
import edu.brown.cs.student.main.CSVClasses.Parse;
import edu.brown.cs.student.main.CSVClasses.Search;
import edu.brown.cs.student.main.creators.CreatorFromRowString;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SearchTest {

  // generic test for searching by header
  @Test
  public void columnHeaderTest() throws IOException, FactoryFailureException {
    File file = new File("/Users/evani/Downloads/csci320/csv-evdalal/data/stars/ten-star.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> parsedRows = myParse.getParsedRows();

    Search mySearch = new Search(parsedRows);
    List<String> columnHeaders = new ArrayList<>(List.of("StarID", "ProperName", "X", "Y", "Z"));
    mySearch.narrowAndConductSearch("Rigel Kentaurus B", "ProperName", columnHeaders);

    List<List<String>> expected = new ArrayList<>();
    List<String> sublist1 =
        new ArrayList<>(List.of("71454", "Rigel Kentaurus B", "-0.50359", "-0.42128", "-1.1767"));
    expected.add(sublist1);

    assertEquals(expected, mySearch.getMatches());
  }

  // generic test for searching by column index
  @Test
  public void columnIndexTest() throws IOException, FactoryFailureException {
    File file = new File("/Users/evani/Downloads/csci320/csv-evdalal/data/stars/ten-star.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> parsedRows = myParse.getParsedRows();

    Search mySearch = new Search(parsedRows);
    mySearch.narrowAndConductSearch("1.55643", 3);

    List<List<String>> expected = new ArrayList<>();
    List<String> sublist1 =
        new ArrayList<>(List.of("3759", "96 G. Psc", "7.26388", "1.55643", "0.68697"));
    expected.add(sublist1);

    assertEquals(expected, mySearch.getMatches());
  }

  @Test
  // no narrowing search
  public void genericSearchTest() throws IOException, FactoryFailureException {
    File file = new File("/Users/evani/Downloads/csci320/csv-evdalal/data/stars/ten-star.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> parsedRows = myParse.getParsedRows();

    Search mySearch = new Search(parsedRows);
    mySearch.narrowAndConductSearch("96 G. Psc");

    List<List<String>> expected = new ArrayList<>();
    List<String> sublist1 =
        new ArrayList<>(List.of("3759", "96 G. Psc", "7.26388", "1.55643", "0.68697"));
    expected.add(sublist1);

    assertEquals(expected, mySearch.getMatches());
  }

  @Test
  // header has same name as keyword, multiple results, keyword contained in cell (not just equals)
  public void headerSameNameAsKeywordTest() throws IOException, FactoryFailureException {
    File file =
        new File("/Users/evani/Downloads/csci320/csv-evdalal/data/sampleCSVs/sampleCSV1.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> parsedRows = myParse.getParsedRows();

    Search mySearch = new Search(parsedRows);
    List<String> columnHeaders =
        new ArrayList<>(
            List.of("hi", "pretend heading 2", "pretend heading 3", "pretend heading 4"));
    mySearch.narrowAndConductSearch("hi", "hi", columnHeaders);

    List<List<String>> expected = new ArrayList<>();
    List<String> sublist1 = new ArrayList<>(List.of("hi what's up", "hello", "go", "blue"));
    List<String> sublist2 = new ArrayList<>(List.of("hi", "hi", "hi", "hi"));
    expected.add(sublist1);
    expected.add(sublist2);

    assertEquals(expected, mySearch.getMatches());
  }

  @Test
  // column index too low, out of bounds
  public void columnIndexOutOfBoundsTest()
      throws IOException, FactoryFailureException, IllegalArgumentException {
    File file =
        new File("/Users/evani/Downloads/csci320/csv-evdalal/data/sampleCSVs/sampleCSV1.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> parsedRows = myParse.getParsedRows();

    Search mySearch = new Search(parsedRows);

    // mySearch.narrowAndConductSearch("hi", -3);
    assertThrows(IllegalArgumentException.class, () -> mySearch.narrowAndConductSearch("hi", -3));
  }

  @Test
  // header does not exist
  public void invalidHeaderTest() throws IOException, FactoryFailureException {
    File file =
        new File("/Users/evani/Downloads/csci320/csv-evdalal/data/sampleCSVs/sampleCSV1.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> parsedRows = myParse.getParsedRows();

    Search mySearch = new Search(parsedRows);
    List<String> columnHeaders =
        new ArrayList<>(
            List.of("hi", "pretend heading 2", "pretend heading 3", "pretend heading 4"));

    assertThrows(
        IllegalArgumentException.class,
        () -> mySearch.narrowAndConductSearch("hi", "bowling green", columnHeaders));
  }

  @Test
  // no results are found
  public void emptyResultsTest() throws IOException, FactoryFailureException {
    File file =
        new File("/Users/evani/Downloads/csci320/csv-evdalal/data/sampleCSVs/sampleCSV1.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    // changed to have no header
    Parse myParse = new Parse(fileReader, stringCreator, false);

    List<List<String>> parsedRows = myParse.getParsedRows();

    Search mySearch = new Search(parsedRows);

    mySearch.narrowAndConductSearch("popopopopopop", 2);

    List<List<String>> expected = new ArrayList<>();

    assertEquals(expected, mySearch.getMatches());
  }

  @Test
  // word exists but not in column specified, using index search
  public void keywordInDifferentColumnTest() throws IOException, FactoryFailureException {
    File file =
        new File("/Users/evani/Downloads/csci320/csv-evdalal/data/sampleCSVs/sampleCSV1.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> parsedRows = myParse.getParsedRows();

    Search mySearch = new Search(parsedRows);
    mySearch.narrowAndConductSearch("hello", 2);

    List<List<String>> expected = new ArrayList<>();

    assertEquals(expected, mySearch.getMatches());
  }

  @Test
  // word exists but not in column specified, using header search
  public void keywordInDifferentColumnHeaderTest() throws IOException, FactoryFailureException {
    File file = new File("/Users/evani/Downloads/csci320/csv-evdalal/data/stars/ten-star.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> parsedRows = myParse.getParsedRows();

    List<String> columnHeaders = new ArrayList<>(List.of("StarID", "ProperName", "X", "Y", "Z"));

    Search mySearch = new Search(parsedRows);
    mySearch.narrowAndConductSearch("Rigel Kentaurus B", "StarID", columnHeaders);

    List<List<String>> expected = new ArrayList<>();

    assertEquals(expected, mySearch.getMatches());
  }
}
