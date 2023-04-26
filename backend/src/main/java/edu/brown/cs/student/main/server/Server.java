package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.FilterRLTDataHandler;
import edu.brown.cs.student.main.server.handlers.LoadRLTDataHandler;
import edu.brown.cs.student.main.server.handlers.SaveAnnotationsHandler;
import spark.Spark;

/** Class that holds our Server (entry point). */
public class Server {

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    Spark.port(6969);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // serverInfo contains the shared states, including the full redlining geoJSON
    ServerInfo serverInfo = new ServerInfo();
    serverInfo.parseGeoJSON("fullDownload.json");

    // set up redliningdata and filteredredliningdata endpoints
    Spark.get("loadRLTData", new LoadRLTDataHandler(serverInfo));
    Spark.get("filterRLTData", new FilterRLTDataHandler(serverInfo));
    Spark.get("saveAnnotations", new SaveAnnotationsHandler());

    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}
