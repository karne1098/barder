package edu.brown.cs.student.annotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.annotations.AnnotationsParser;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data.Annotation;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

/** class for testing the AnnotationsParser object functionality */
public class AnnotationsParserTest {
  /**
   * tests that annotations parser works to turn a mock annotation json string into a List of
   * Annotation objects
   */
  @Test
  public void testAnnotationParserNotEmpty() throws IOException {
    String annotationString =
        "{\"annotations\":[{\"latitude\":41.839348739598194,\"longitude\":-71.39829461364837,\"annotation\":\"akdjjf\"}]}";
    AnnotationsParser annotationsParser = new AnnotationsParser();
    annotationsParser.parse(annotationString);

    ArrayList<Annotation> annotations = new ArrayList<>();
    Data.Annotation annotation1 = new Annotation(41.839348739598194, -71.39829461364837, "akdjjf");
    annotations.add(annotation1);

    assertEquals(annotationsParser.getAnnotations(), annotations);
  }

  /** tests that a mock json with an empty annotations parses correctly */
  @Test
  public void testAnnotationParserEmpty() throws IOException {
    String annotationString = "{\"annotations\": []}";
    AnnotationsParser annotationsParser = new AnnotationsParser();
    annotationsParser.parse(annotationString);
    ArrayList<Annotation> annotations = new ArrayList<>();
    assertEquals(annotationsParser.getAnnotations(), annotations);
  }

  /** tests that missing fields in the mock json string are parsed as null in the backend */
  @Test
  public void testAnnotationParserBlankFields() throws IOException {
    String annotationString =
        "{\"annotations\":[{\"longitude\":-71.39829461364837,\"annotation\":\"akdjjf\"}]}";
    AnnotationsParser annotationsParser = new AnnotationsParser();
    annotationsParser.parse(annotationString);
    ArrayList<Annotation> annotations = new ArrayList<>();
    Data.Annotation annotation1 = new Annotation(null, -71.39829461364837, "akdjjf");
    annotations.add(annotation1);
    assertEquals(annotationsParser.getAnnotations(), annotations);
  }
}
