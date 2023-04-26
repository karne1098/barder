package edu.brown.cs.student.server.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.ServerInfo;
import edu.brown.cs.student.main.server.handlers.LoadRLTDataHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** Integration testing suite that tests the handler for the "loadRLTData" server endpoint. */
public class LoadRLTDataHandlerTest {
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
    Spark.get("/loadRLTData", new LoadRLTDataHandler(this.serverInfo));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** The method that unmaps the routes for each endpoint and stops the server gracefully. */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadRLTData");
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

  /** tests loadHandler without the filepath parameter */
  @Test
  public void testTooManyParam() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadRLTData?hello=hello");
    HashMap<String, String> errorMessages = new HashMap<>();
    errorMessages.put("error_bad_request", "input path to json required");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", resp.get("result"));
    assertEquals("too many parameters", resp.get("message"));

    clientConnection.disconnect();
  }

  /** tests loadHandler for a successful call */
  @Test
  public void testSuccess() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadRLTData");
    HashMap<String, String> errorMessages = new HashMap<>();

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", resp.get("result"));
    clientConnection.disconnect();
  }

  /** tests loading the same json twice */
  @Test
  public void testLoadTwiceSame() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadRLTData");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", resp.get("result"));

    HttpURLConnection clientConnection1 = tryRequest("loadRLTData");

    assertEquals(200, clientConnection.getResponseCode());

    Map resp1 = adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals(resp1, resp);
    assertEquals(resp1.get("result"), resp.get("result"));
    assertEquals(resp1.get("geojson"), resp.get("geojson"));

    clientConnection.disconnect();
  }

  /** tests loading two difference jsons */
  @Test
  public void testLoadTwiceDifferent() throws IOException {
    this.serverInfo.parseGeoJSON("mockGeoJSON.json");
    HttpURLConnection clientConnection = tryRequest("loadRLTData");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", resp.get("result"));

    this.serverInfo.parseGeoJSON("mockGeoJSON2.json");
    HttpURLConnection clientConnection1 = tryRequest("loadRLTData");

    assertEquals(200, clientConnection.getResponseCode());

    Map resp1 = adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertNotEquals(resp1, resp);
    assertEquals(resp1.get("result"), resp.get("result"));
    assertNotEquals(resp1.get("geojson"), resp.get("geojson"));

    clientConnection.disconnect();
  }

  /** tests loading the full redlining geojson */
  @Test
  public void testSuccessRealGeoJSON() throws IOException {
    this.serverInfo.parseGeoJSON("fullDownload.json");
    HttpURLConnection clientConnection = tryRequest("loadRLTData");
    HashMap<String, String> errorMessages = new HashMap<>();

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", resp.get("result"));
    clientConnection.disconnect();
  }

  /** tests loading the full redlining geojson twice */
  @Test
  public void testLoadRealGeoJSONTwice() throws IOException {
    this.serverInfo.parseGeoJSON("fullDownload.json");
    HttpURLConnection clientConnection = tryRequest("loadRLTData");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));

    Map resp = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", resp.get("result"));

    HttpURLConnection clientConnection1 = tryRequest("loadRLTData");

    assertEquals(200, clientConnection.getResponseCode());

    Map resp1 = adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals(resp1, resp);
    assertEquals(resp1.get("result"), resp.get("result"));
    assertEquals(resp1.get("geojson"), resp.get("geojson"));

    clientConnection.disconnect();
  }
}
