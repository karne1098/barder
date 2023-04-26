package edu.brown.cs.student.server.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data.Annotation;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data.GeoJSON;
import edu.brown.cs.student.main.server.ServerInfo;
import edu.brown.cs.student.main.server.handlers.SaveAnnotationsHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** Integration testing suite that tests the handler for the "saveAnnotations" server endpoint. */
public class SaveAnnotationsHandlerTest {
  /** Method that sets up the port and logger at the start of the testing suite execution. */
  @BeforeAll
  public static void beforeAll() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  /**
   * Method that initalizes all the global variables and sets up the route mapping for the endpoints
   * before every test.
   */
  @BeforeEach
  public void setup() {
    ServerInfo serverInfo = new ServerInfo();
    // In fact, restart the entire Spark server for every test!
    Spark.get("/saveAnnotations", new SaveAnnotationsHandler());
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** The method that unmaps the routes for each endpoint and stops the server gracefully. */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/saveAnnotations");
    Spark.stop();
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Method that starts a connection to a given API endpoint
   *
   * @param apiCall the request being made
   * @return the connection for the given URL, just after connecting
   * @throws IOException when the connection fails or cannot be made
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.connect();
    return clientConnection;
  }

  /** tests annotations Handler without providing annotations returns bad request response */
  @Test
  public void testNoParam() throws IOException {
    HttpURLConnection clientConnection = tryRequest("saveAnnotations");
    HashMap<String, String> errorMessages = new HashMap<>();

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", resp.get("result"));
    assertEquals("need annotations to save", resp.get("message"));

    clientConnection = tryRequest("saveAnnotations?annotations=");
    Map resp1 = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", resp1.get("result"));
    assertEquals("need annotations to save", resp1.get("message"));

    clientConnection.disconnect();
  }

  /** tests that annotations persist in backend */
  @Test
  public void testAnnotationPersistance() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest(
            "saveAnnotations?annotations={\"annotations\":[{\"latitude\":41.839348739598194,\"longitude\":-71.39829461364837,\"annotation\":\"akdjjf\"}]}");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(
            Types.newParameterizedType(Map.class, String.class, Object.class, GeoJSON.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", resp.get("result"));

    ArrayList<Annotation> annotations = new ArrayList<>();
    Annotation annotation1 = new Annotation(41.839348739598194, -71.39829461364837, "akdjjf");
    annotations.add(annotation1);
    assertEquals(1, ((List) resp.get("annotations")).size());
    assertEquals(
        41.839348739598194,
        ((Map<String, Object>) ((List) resp.get("annotations")).get(0)).get("latitude"));
    assertEquals(
        -71.39829461364837,
        ((Map<String, Object>) ((List) resp.get("annotations")).get(0)).get("longitude"));
    assertEquals(
        "akdjjf",
        ((Map<String, Object>) ((List) resp.get("annotations")).get(0)).get("annotation"));
    clientConnection.disconnect();
  }

  /** tests that annotations on the backend change after a different annotations is saved */
  @Test
  public void testLoadTwiceDifferent() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest(
            "saveAnnotations?annotations={\"annotations\":[{\"latitude\":41.839348739598194,\"longitude\":-71.39829461364837,\"annotation\":\"akdjjf\"}]}");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(
            Types.newParameterizedType(Map.class, String.class, Object.class, GeoJSON.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    HttpURLConnection clientConnection1 =
        tryRequest(
            "saveAnnotations?annotations={\"annotations\":[{\"latitude\":41.839348739598194,\"longitude\":-71.39829461364837,\"annotation\":\"HHAHAHAHHA\"}]}");

    assertEquals(200, clientConnection1.getResponseCode());

    Map resp1 = adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertNotEquals(resp1, resp);
    assertEquals(resp.get("result"), resp1.get("result"));
    assertNotEquals(resp.get("annotations"), resp1.get("annotations"));

    clientConnection.disconnect();
  }

  /** tests that annotations on the backend are the same after the same annotations is saved */
  @Test
  public void testLoadTwiceSame() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest(
            "saveAnnotations?annotations={\"annotations\":[{\"latitude\":41.839348739598194,\"longitude\":-71.39829461364837,\"annotation\":\"ello\"}]}");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(
            Types.newParameterizedType(Map.class, String.class, Object.class, GeoJSON.class));
    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    HttpURLConnection clientConnection1 =
        tryRequest(
            "saveAnnotations?annotations={\"annotations\":[{\"latitude\":41.839348739598194,\"longitude\":-71.39829461364837,\"annotation\":\"ello\"}]}");

    assertEquals(200, clientConnection1.getResponseCode());

    Map resp1 = adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals(resp1, resp);
    assertEquals(resp.get("result"), resp1.get("result"));
    assertEquals(resp.get("annotations"), resp1.get("annotations"));

    clientConnection.disconnect();
  }
}
