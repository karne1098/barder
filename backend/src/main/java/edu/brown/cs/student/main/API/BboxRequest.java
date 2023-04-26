package edu.brown.cs.student.main.API;

import java.util.List;

/** Class that represents a BboxRequest made to the backend. */
public class BboxRequest {
  private double minLat;
  private double minLon;

  private double maxLat;
  private double maxLon;

  /**
   * Constructor for a BboxRequest Object.
   *
   * @param maxLat the maximum latitude set in the bounding box
   * @param maxLon the maximum longitude set in the bounding box
   * @param minLat the minimum latitude set in the bounding box
   * @param minLon the minimum longitue set in the bounding box
   */
  public BboxRequest(double minLat, double minLon, double maxLat, double maxLon) {
    this.minLat = minLat;
    this.minLon = minLon;
    this.maxLat = maxLat;
    this.maxLon = maxLon;
  }

  /**
   * Function that checks if the coordinates of a polygon in the redlining GeoJSON data are within
   * the bounding box
   *
   * @param featureCoordinates the coordinates being checked against the conditions of the bounding
   *     box
   * @return true if all the coordinates are inside the bounding box, true if featureCoordinates is
   *     an empty list. false otherwise
   */
  public boolean contains(List<List<Double>> featureCoordinates) {
    for (List<Double> coordinate : featureCoordinates) {
      Double currLon = coordinate.get(0);
      Double currLat =
          coordinate.get(1); // TODO: check if the order is right on which is lat and which is lon
      // if this coordinate is out of bounds, return false
      if (!(currLon > minLon && currLon < maxLon && currLat > minLat && currLat < maxLat)) {
        return false;
      }
    }
    // at this point all the points are within the bbox
    return true;
  }
}
