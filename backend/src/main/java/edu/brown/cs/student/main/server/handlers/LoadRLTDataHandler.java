package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.geoJSON.GeoJSONParser;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data;
import edu.brown.cs.student.main.server.ServerInfo;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

// the below imports and where they are used, which turns the json file into a json string, is taken
// from
// https://www.javatpoint.com/convert-json-file-to-string-in-java#:~:text=In%20Java%2C%20we%20can%20easily,intensive%20I%2FO%20operations).

/** Class that holds the handler for the "loadRLTData" server endpoint. */
public class LoadRLTDataHandler implements Route {
  private final ServerInfo serverInfo;

  public LoadRLTDataHandler(ServerInfo serverInfo) {
    this.serverInfo = serverInfo;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    QueryParamsMap queryMap = request.queryMap();
    GeoJSONParser geoJSONParser = new GeoJSONParser();

    // error checking making sure user inputted correct variables and in the form we expect
    if (request.queryParams().size() != 0) {
      return serialize(fail("error_bad_request", "too many parameters"));
    }

    return serialize(success(this.serverInfo.getFullRedliningGeoJSON()));
  }

  /**
   * Method that returns a Map mapping the response for a failed request.
   *
   * @param error the type of error
   * @param message the message associated with the error
   * @return hashmap mapping the response for a failed request
   */
  public static Map<String, Object> fail(String error, String message) {
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
  public static Map<String, Object> success(Data.GeoJSON geoJSONData) {
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
