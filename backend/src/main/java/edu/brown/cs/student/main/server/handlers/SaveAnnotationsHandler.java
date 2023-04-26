package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.annotations.AnnotationsParser;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data.Annotation;
import edu.brown.cs.student.main.server.ServerInfo;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class that holds the handler for the "saveAnnotations" server endpoint. */
public class SaveAnnotationsHandler implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    QueryParamsMap queryMap = request.queryMap();
    String annotations = queryMap.value("annotations");
    HashMap<String, String> errorMessages = new HashMap<>();
    AnnotationsParser annotationsParser = new AnnotationsParser();

    if (annotations == null || annotations == "") {
      return serialize(fail("error_bad_request", "need annotations to save"));
    }
    annotationsParser.parse(annotations);
    ServerInfo.setAnnotations(annotationsParser.getAnnotations());
    System.out.println(ServerInfo.getAnnotations());
    return serialize(success(annotationsParser.getAnnotations()));
  }

  /**
   * Method that returns a Map mapping the response for a failed request.
   *
   * @param message the message associated with the error
   * @param error the error messages we are returning
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
   * @param annotations the list of Annotation objects associated with the request
   * @return the hashmap mapping the response for a successful request.
   */
  public static Map<String, Object> success(List<Annotation> annotations) {
    Map<String, Object> successful = new HashMap<>();
    successful.put("result", "success");
    successful.put("annotations", annotations);
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
