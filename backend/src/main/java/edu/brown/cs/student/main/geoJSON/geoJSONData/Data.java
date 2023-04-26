package edu.brown.cs.student.main.geoJSON.geoJSONData;

import java.util.List;
import java.util.Map;

/** Class for converting the GeoJSON to a Data object. */
public class Data {

  /**
   * Record for the overall GeoJSON data structure, which contains a type and features field
   *
   * @param type the GeoJSON type name
   * @param features the List of Feature objects making up the features of the map
   */
  public record GeoJSON(String type, List<Feature> features) {}

  /**
   * record for Annotations stored on the backend
   *
   * @param result the result
   * @param annotations a list of annotation objects
   */
  public record Annotations(String result, List<Annotation> annotations) {}

  /**
   * an annotation associated with a point on a map
   *
   * @param latitude the latitutde of the point
   * @param longitude the longitude of the point
   * @param annotation the annotation at the lat-lon point
   */
  public record Annotation(Double latitude, Double longitude, String annotation) {}

  /**
   * Record for an individual Feature object, which contains a type, geometry, and properties field
   *
   * @param type the Feature type
   * @param geometry the shape of the Feature
   * @param properties the properties of the Feature
   */
  public record Feature(String type, FeatureGeometry geometry, Map<String, Object> properties) {}

  /**
   * Record for a FeatureGeometry object, which contains a type and coordinates of the geometry
   * (make up the polygon points)
   *
   * @param type the FeatureGeometry type
   * @param coordinates the coordinates making up the polygon shape
   */
  public record FeatureGeometry(String type, List<List<List<List<Double>>>> coordinates) {}
}
