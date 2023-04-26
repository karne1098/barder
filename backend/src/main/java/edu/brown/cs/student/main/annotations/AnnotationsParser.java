package edu.brown.cs.student.main.annotations;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data.Annotation;
import java.io.IOException;
import java.util.List;

/**
 * Class for parsing a JSON string of saved annotations from the frontend into a List of Annotation
 * Objects in the backend.
 */
public class AnnotationsParser {
  private List<Annotation> annotations;

  /**
   * Function to parse a String json object into a List of Annotation objects.
   *
   * @param json the JSON string we need to parse
   * @throws IOException
   */
  public void parse(String json) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Data.Annotations annotations = moshi.adapter(Data.Annotations.class).fromJson(json);
    this.annotations = annotations.annotations();
  }

  /**
   * Getter function to get the List of Annotation objects.
   *
   * @return the list of annotations stored in this annotation parser object
   */
  public List<Annotation> getAnnotations() {
    return this.annotations;
  }
}
