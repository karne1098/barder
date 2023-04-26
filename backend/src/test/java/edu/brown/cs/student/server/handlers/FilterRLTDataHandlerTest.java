package edu.brown.cs.student.server.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.ServerInfo;
import edu.brown.cs.student.main.server.handlers.FilterRLTDataHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** Integration testing suite that tests the handler for the "filterRLTData" server endpoint. */
public class FilterRLTDataHandlerTest {
  private ServerInfo serverInfo = new ServerInfo();
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
    this.serverInfo.parseGeoJSON("mockGeoJSON.json");
    // In fact, restart the entire Spark server for every test!
    //    Spark.get("/loadRLTData", new LoadRLTDataHandler());
    Spark.get("/filterRLTData", new FilterRLTDataHandler(this.serverInfo));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** The method that unmaps the routes for each endpoint and stops the server gracefully. */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    //    Spark.unmap("/loadRLTData");
    Spark.unmap("/filterRLTData");
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

  /** tests error response for too many queries */
  @Test
  public void testTooLittleOrTooManyParam() throws IOException {
    HttpURLConnection clientConnection = tryRequest("filterRLTData");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_json", resp.get("result"));
    assertEquals("need 4 inputs", resp.get("message"));

    clientConnection = tryRequest("filterRLTData?1=hi&2=hillo&3=bye&4=skjd&5=aksdj");
    resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_json", resp.get("result"));
    assertEquals("need 4 inputs", resp.get("message"));

    clientConnection.disconnect();
  }

  /** tests error response for missing query parameters */
  @Test
  public void testMissingParam() throws IOException {
    HttpURLConnection clientConnection = tryRequest("filterRLTData?minLat=40");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_json", resp.get("result"));
    assertEquals("need a min and max latitude and longitude", resp.get("message"));

    clientConnection.disconnect();
  }

  /** tests error response for invalid query parameters (number out of bounds) */
  @Test
  public void testInValidLatLonParam() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("filterRLTData?minLat=1000&maxLat=42&minLon=-71.5&maxLon=-71");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", resp.get("result"));
    assertEquals("use valid latitude and longitude", resp.get("message"));

    clientConnection.disconnect();
  }

  /** tests error response for invalid query parameters (not a number) */
  @Test
  public void testNotNumberParam() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("filterRLTData?minLat=hi&maxLat=42&minLon=-71.5&maxLon=-71");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", resp.get("result"));
    assertEquals("input must be a number", resp.get("message"));

    clientConnection.disconnect();
  }

  /** tests successresponse for valid filter */
  @Test
  public void testSuccess() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("filterRLTData?minLat=40&maxLat=42&minLon=-71.5&maxLon=-71");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", resp.get("result"));

    clientConnection.disconnect();
  }

  /** test get the same successresponse if filter using the same bounds twice */
  @Test
  public void testLoadTwiceSame() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("filterRLTData?minLat=40&maxLat=42&minLon=-71.5&maxLon=-71");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    HttpURLConnection clientConnection1 =
        tryRequest("filterRLTData?minLat=40&maxLat=42&minLon=-71.5&maxLon=-71");
    Map resp1 = adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));

    assertEquals(resp, resp1);
    assertEquals(resp1.get("result"), resp.get("result"));
    assertEquals(resp1.get("geojson"), resp.get("geojson"));

    clientConnection.disconnect();
  }

  /** tests that get different success responses if filtering with different bounds */
  @Test
  public void testLoadTwiceDifferent() throws IOException {
    this.serverInfo.parseGeoJSON("fullDownload.json");
    HttpURLConnection clientConnection =
        tryRequest("filterRLTData?minLat=30&maxLat=30&minLon=-86.5&maxLon=-86");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    HttpURLConnection clientConnection1 =
        tryRequest("filterRLTData?minLat=40&maxLat=42&minLon=-71.5&maxLon=-71");
    Map resp1 = adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));

    assertNotEquals(resp, resp1);
    assertEquals(resp1.get("result"), resp.get("result"));
    assertNotEquals(resp1.get("geojson"), resp.get("geojson"));

    clientConnection.disconnect();
  }
}
