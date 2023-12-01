package edu.brown.cs.student.csvTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.main.CSVClasses.FactoryFailureException;
import edu.brown.cs.student.main.CSVClasses.Parse;
import edu.brown.cs.student.main.RedlineObjects.Star;
import edu.brown.cs.student.main.creators.CreatorFromRowCharCount;
import edu.brown.cs.student.main.creators.CreatorFromRowStar;
import edu.brown.cs.student.main.creators.CreatorFromRowString;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ParseTest {

  // tests whether parse can work on different creators from row, using charCountCreator and uses
  // stringReader
  @Test
  public void creatorFromRowCountTest() throws FactoryFailureException, IOException {
    String myString = "hi,hi,hi\nhello,hi,hey\nhey,ho,hi";
    StringReader reader = new StringReader(myString);
    CreatorFromRowCharCount myCreator = new CreatorFromRowCharCount();
    Parse myParse = new Parse(reader, myCreator, false);

    List<Integer> expectedOutput = new ArrayList<Integer>(List.of(6, 10, 7));
    assertEquals(expectedOutput, myParse.getParsedRows());
  }

  // tests what happens when csv does not have pairs of double quotes
  // if quote does not close before next comma, row will not be split correctly
  @Test
  public void regexTest() throws FactoryFailureException, IOException {
    String myString = "\"hi\",hi,\"woo\nhello,\"hi,hey\nhey,\"\"ho\",hi";
    StringReader reader = new StringReader(myString);
    CreatorFromRowString myCreator = new CreatorFromRowString();
    Parse myParse = new Parse(reader, myCreator, false);

    List<List<String>> expectedOutput = new ArrayList<>();

    List<String> sublist1 = new ArrayList<>(List.of("\"hi\",hi,\"woo"));
    List<String> sublist2 = new ArrayList<>(List.of("hello,\"hi", "hey"));
    List<String> sublist3 = new ArrayList<>(List.of("hey,\"\"ho\"", "hi"));
    expectedOutput.add(sublist1);
    expectedOutput.add(sublist2);
    expectedOutput.add(sublist3);

    assertEquals(expectedOutput, myParse.getParsedRows());
  }

  @Test
  // testing if headers get omitted from parsed csv content
  public void headersTest() throws FactoryFailureException, IOException {
    String myString = "number, color, letter\n3, blue, a\n2, green, j";
    StringReader reader = new StringReader(myString);
    CreatorFromRowString myCreator = new CreatorFromRowString();
    Parse myParse = new Parse(reader, myCreator, true);

    List<List<String>> expectedOutput = new ArrayList<>();
    List<String> sublist1 = new ArrayList<>(List.of("3", "blue", "a"));
    List<String> sublist2 = new ArrayList<>(List.of("2", "green", "j"));
    expectedOutput.add(sublist1);
    expectedOutput.add(sublist2);

    assertEquals(expectedOutput, myParse.getParsedRows());
  }

  @Test
  // using starCreator to parse and uses Filereader
  public void starCreatorFromRowTest() throws FactoryFailureException, IOException {
    File file = new File("/Users/evani/Downloads/csci320/csv-evdalal/data/stars/stardata.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowStar starCreator = new CreatorFromRowStar();
    Parse myParse = new Parse(fileReader, starCreator, true);
    int expectedSize = 119617;

    // checking that each row got added to the parseCSV list
    assertEquals(expectedSize, myParse.getSize());

    int expectedSecondID = 1;
    // setting as star to be able to check id for testing purposes
    Star secondStar = (Star) myParse.getParsedRows().get(1);
    assertEquals(expectedSecondID, secondStar.getID());
  }

  @Test
  // generic parse and search function test with search by column index
  public void parseAndSearchTest() throws IOException {
    File file =
        new File("/Users/evani/Downloads/csci320/csv-evdalal/data/sampleCSVs/sampleCSV1.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    // changed to have no header
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> expected = new ArrayList<>();
    List<String> sublist1 = new ArrayList<>(List.of("hi what's up", "hello", "go", "blue"));
    expected.add(sublist1);

    assertEquals(expected, myParse.parseAndSearchCSV(2, "go", ""));
  }

  @Test
  // testing if keyword exists but wrong column search index is given
  public void parseAndSearchWrongIndexTest() throws IOException {
    File file =
        new File("/Users/evani/Downloads/csci320/csv-evdalal/data/sampleCSVs/sampleCSV1.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowString stringCreator = new CreatorFromRowString();
    // changed to have no header
    Parse myParse = new Parse(fileReader, stringCreator, true);

    List<List<String>> expected = new ArrayList<>();

    assertEquals(expected, myParse.parseAndSearchCSV(0, "go", ""));
  }

  @Test
  // data type error for search since search must be done on a string object
  public void parseAndSearchDataTypeErrorTest() throws IOException {
    File file = new File("/Users/evani/Downloads/csci320/csv-evdalal/data/stars/ten-star.csv");
    FileReader fileReader = new FileReader(file);
    CreatorFromRowStar starCreator = new CreatorFromRowStar();
    // changed to have no header
    Parse myParse = new Parse(fileReader, starCreator, true);

    assertThrows(ClassCastException.class, () -> myParse.parseAndSearchCSV(1, "Sol", ""));
  }

  @Test
  // checks if file is not found
  public void fileNotFoundTest() {
    // creating filereader for invalid file
    assertThrows(
        IOException.class,
        () -> {
          FileReader fileReader = new FileReader("kajsdklsa");
        });
  }
}
