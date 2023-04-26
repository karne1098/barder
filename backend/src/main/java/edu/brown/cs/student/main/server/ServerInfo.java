package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.annotations.AnnotationsParser;
import edu.brown.cs.student.main.geoJSON.GeoJSONParser;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data.Annotation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/** Class for our ServerInfo object initialized upon starting the Server. */
public class ServerInfo {
  private static String path;
  private static Data.GeoJSON fullRedliningGeoJSON;
  private static List<Annotation> annotations;

  /**
   * Constructor for our ServerInfo object, that is responsible for loading the GeoJSON redlining
   * data and containing the annotations saved from the frontend.
   */
  public ServerInfo() { // iinitialized as null need to call parse elsewhere
  }

  /**
   * Getter function for the Data.GeoJSON object created when the GeoJSON file is parsed.
   *
   * @return the GeoJSON object stored in the server
   */
  public static Data.GeoJSON getFullRedliningGeoJSON() {
    return fullRedliningGeoJSON;
  }

  /**
   * Getter fucntion for the annotations saved in the server
   *
   * @return a List<Annotation> that is currently stored in the server's field 'annotations'
   */
  public static List<Annotation> getAnnotations() {
    return annotations;
  }

  /**
   * Setter function for the annotations held in the server
   *
   * @param annotationList the List<Annotation> to set the annotations field of the ServerInfo to
   */
  public static void setAnnotations(List<Annotation> annotationList) {
    annotations = annotationList;
  }

  /**
   * Setter function for the Data.GeoJSON object created when the GeoJSON file is parsed.
   *
   * @param json the GeoJSON object to store as the fullredlining geojson
   */
  public static void setFullRedliningGeoJSON(Data.GeoJSON json) {
    fullRedliningGeoJSON = json;
  }

  public String getPath() {
    return path;
  }

  public String setPath(String pathNew) {
    return path = pathNew;
  }

  public void parseGeoJSON(String path) {
    // does the work that ued to be in the constructor
    GeoJSONParser geoJSONParser = new GeoJSONParser();
    AnnotationsParser annotationsParser = new AnnotationsParser();
    // parse the json file into a string
    try {
      String stringJSON =
          new String(
              Files.readAllBytes(
                  Paths.get("data/redlining/" + path))); // We know this line of code is working
      // well because it worked properly with
      // the previous implementation

      geoJSONParser.parse(stringJSON);
      // System.out.println("in the constructor for server info, geojson is parsed as" +
      // geoJSONParser.getJSONData());

      // parse the json string into a Map Object
      this.fullRedliningGeoJSON = geoJSONParser.getJSONData();
      this.annotations = annotationsParser.getAnnotations();
      //      System.out.println(this.annotations);
    } catch (IOException e) {
      System.out.println(e.getMessage()); // TODO: better error handling
    }
  }
}
