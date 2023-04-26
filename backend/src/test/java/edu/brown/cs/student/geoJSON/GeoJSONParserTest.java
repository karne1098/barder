package edu.brown.cs.student.geoJSON;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.geoJSON.GeoJSONParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

/** class for testing the GeoJSONParser object functionality */
public class GeoJSONParserTest {

  /**
   * tests that parsing works on mock geoJSON! This is a place where mocks can be used.
   *
   * @throws IOException
   */
  @Test
  public void testGeoJSONParser() throws IOException {
    GeoJSONParser geoJSONParser = new GeoJSONParser();
    try {
      String stringJSON =
          new String(
              Files.readAllBytes(
                  Paths.get(
                      "data/redlining/mockGeoJSON.json"))); // We know this line of code is working
      // well because it worked
      // properly with the previous implementation
      geoJSONParser.parse(stringJSON);

      assertEquals(geoJSONParser.getJSONData().type(), "FeatureCollection");
      assertEquals(geoJSONParser.getJSONData().features().size(), 1);
      assertEquals(geoJSONParser.getJSONData().features().get(0).type(), "Feature");
      assertEquals(geoJSONParser.getJSONData().features().get(0).geometry().type(), "MultiPolygon");
      assertEquals(
          geoJSONParser
              .getJSONData()
              .features()
              .get(0)
              .geometry()
              .coordinates()
              .get(0)
              .get(0)
              .size(),
          7);
    } catch (IOException e) {
      System.out.println(e.getMessage()); // TODO: better error handling
    }
  }
}
