package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.API.BboxRequest;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data;
import edu.brown.cs.student.main.server.ServerInfo;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class that holds the handler for the "filterRLTData" server endpoint. */
public class FilterRLTDataHandler implements Route {
  private final ServerInfo serverInfo;

  public FilterRLTDataHandler(ServerInfo serverInfo) {
    this.serverInfo = serverInfo;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    QueryParamsMap queryMap = request.queryMap();
    // TODO: with this code wouldn't you print previous errors
    // like if there was a bad request then a bad data it'd return both?
    // error checking making sure user inputted correct variables
    String minLat = queryMap.value("minLat");
    String maxLat = queryMap.value("maxLat");
    String minLon = queryMap.value("minLon");
    String maxLon = queryMap.value("maxLon");
    if (ServerInfo.getFullRedliningGeoJSON() == null) {
      return serialize(fail("error_bad_request", "please load a geoJSON first"));
    }
    if (request.queryParams().size() == 0 || request.queryParams().size() > 4) {
      return serialize(fail("error_bad_json", "need 4 inputs"));
    }
    if (minLat == null || maxLat == null || minLon == null || maxLon == null) {
      return serialize(fail("error_bad_json", "need a min and max latitude and longitude"));
    }

    // error checking making sure the user inputs variables in the form we expect
    Double doubleMinLat; // TODO: change to primitive double?
    Double doubleMinLon;
    Double doubleMaxLat;
    Double doubleMaxLon;
    try {
      doubleMinLat = Double.parseDouble(minLat);
      doubleMinLon = Double.parseDouble(minLon);
      doubleMaxLat = Double.parseDouble(maxLat);
      doubleMaxLon = Double.parseDouble(maxLon);
      // TODO: consider whether the line below is still necessary
      if (Math.abs(doubleMinLat) > 90
          || Math.abs(doubleMinLon) > 180
          || Math.abs(doubleMaxLat) > 90
          || Math.abs(doubleMaxLon) > 180) {
        return serialize(fail("error_bad_request", "use valid latitude and longitude"));
      }
    } catch (NumberFormatException nfe) {
      return serialize(fail("error_bad_request", "input must be a number"));
    }

    // by this point, we know lat and lon are valid, so we need to form the response
    try {
      BboxRequest bboxRequest =
          new BboxRequest(
              doubleMinLat, doubleMinLon,
              doubleMaxLat, doubleMaxLon);

      List<Data.Feature> filteredFeatures = filterBy(bboxRequest);

      // create new geojson with new list of features
      Data.GeoJSON geoJSONData = new Data.GeoJSON("FeatureCollection", filteredFeatures);

      return serialize(success(geoJSONData));

    } catch (Exception e) {
      return serialize(fail("error_datasource", e.getMessage()));
    }
  }

  /**
   * helper function that filters a list of features into the features that are contained within the
   * bbox
   *
   * @param bboxRequest represents the boundary box that we want to filter by
   * @return a filtered list of features
   */
  public List<Data.Feature> filterBy(BboxRequest bboxRequest) {
    // 1. initialize a new List<Feature> to populate with filter-specific feature
    List<Data.Feature> filteredFeatures = new ArrayList<>();
    // 2. go through each feature of the full redlining geoJSON,
    for (Data.Feature feature : this.serverInfo.getFullRedliningGeoJSON().features()) {
      // if the feature is 'within' the boundary box, add to filteredFeatures
      if (feature.geometry() != null) {
        // TODO: see, could coordinates also be null? or, an unshapen list?
        if (bboxRequest.contains(feature.geometry().coordinates().get(0).get(0))) {
          filteredFeatures.add(feature);
        }
      }
    }
    return filteredFeatures;
  }

  /**
   * Method that returns a Map mapping the response for a failed request.
   *
   * @param error - the error messages we are returning
   * @return hashmap mapping the response for a failed request
   */
  public Map<String, Object> fail(String error, String message) {
    Map<String, Object> failed = new HashMap<>();
    failed.put("result", error);
    failed.put("message", message);
    return failed;
  }

  /**
   * Method that returns a Map mapping the response for a successful request.
   *
   * @param geoJSONData - geoJSON that is returned by a successful request
   * @return the hashmap mapping the response for a successful request.
   */
  public Map<String, Object> success(Data.GeoJSON geoJSONData) {
    Map<String, Object> successful = new HashMap<>();
    successful.put("result", "success");
    successful.put("geojson", geoJSONData);
    return successful;
  }

  /**
   * Method that serializes a HashMap mapping strings to objects (also known as the response
   * objects) as a Json Object
   *
   * @param response - the response that is being converted to a json
   * @return the json object representing the response
   */
  public static String serialize(Map<String, Object> response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapOfJSONResponse =
        Types.newParameterizedType(Map.class, String.class, Object.class, Data.GeoJSON.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapOfJSONResponse);
    return adapter.toJson(response);
  }
}
