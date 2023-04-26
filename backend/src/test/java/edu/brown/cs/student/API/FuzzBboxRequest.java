package edu.brown.cs.student.API;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.API.BboxRequest;
import edu.brown.cs.student.main.geoJSON.GeoJSONParser;
import edu.brown.cs.student.main.geoJSON.geoJSONData.Data;
import edu.brown.cs.student.main.server.ServerInfo;
import edu.brown.cs.student.main.server.handlers.FilterRLTDataHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** class for fuzz/random testing the BboxRequest object functionality */
public class FuzzBboxRequest {
  // TODO: For the Random Testing Badge:
  // fuzz test the filter at the unit-testing level; and
  // property-based test the filter to confirm that all points in the returned area set do fall
  // within the bounding box.

  ServerInfo serverInfo = new ServerInfo();
  FilterRLTDataHandler filterRLTDataHandler = new FilterRLTDataHandler(serverInfo);

  /** fuzz test for generating a BboxRequest object for the filtering function */
  @Test
  public void fuzzBboxRequestGen() {
    int times = 1000;
    for (int i = 0; i < times; i++) {
      BboxRequest bboxRequest = makeRndBboxRequest();
      assertNotNull(bboxRequest);
    }
  }

  /**
   * function that makes the random BboxRequest object. Note that trailing zeroes are truncated
   * because of the way Double.parseDouble is written. This is okay because our BboxRequest can
   * handle doubles that represent numbers with 'less than 6 decimal places'.
   */
  public BboxRequest makeRndBboxRequest() {
    // generate lat between -90 and 90 NONINCLUSIVE and lon between -180 and 180.
    Random r = new Random();

    Double lat1;
    Double lat2;
    Double lon1;
    Double lon2;

    if (r.nextBoolean()) { // true -> *-1
      lat1 = r.nextDouble() * -90;
    } else {
      lat1 = r.nextDouble() * 90;
    }
    if (r.nextBoolean()) { // true -> *-1
      lat2 = r.nextDouble() * -90;
    } else {
      lat2 = r.nextDouble() * 90;
    }

    if (r.nextBoolean()) { // true -> *-1
      lon1 = r.nextDouble() * -180;
    } else {
      lon1 = r.nextDouble() * 180;
    }
    if (r.nextBoolean()) { // true -> *-1
      lon2 = r.nextDouble() * -180;
    } else {
      lon2 = r.nextDouble() * 180;
    }

    // make the min the smaller one and max the larger one
    // make the bbox
    // assert that the bbox is not null
    Double minLat = Double.min(lat1, lat2);
    Double maxLat = Double.max(lat1, lat2);
    Double minLon = Double.min(lon1, lon2);
    Double maxLon = Double.max(lon1, lon2);

    Double minLat6Dec = Double.parseDouble(new DecimalFormat("##.000000").format(minLat));
    Double maxLat6Dec = Double.parseDouble(new DecimalFormat("##.000000").format(maxLat));
    Double minLon6Dec = Double.parseDouble(new DecimalFormat("###.000000").format(minLon));
    Double maxLon6Dec = Double.parseDouble(new DecimalFormat("###.000000").format(maxLon));

    return new BboxRequest(minLat6Dec, minLon6Dec, maxLat6Dec, maxLon6Dec);
  }

  /** fuzz testing at the unit level for the filtering */
  @Test
  public void fuzzBboxFilter() {
    int times = 1000;
    for (int i = 0; i < times; i++) {
      //            System.out.println(i);
      testRndBboxFilter();
    }
  }

  /** test for a filtering upon a given randomly generated bboxRequest */
  public void testRndBboxFilter() {
    BboxRequest currBboxRequest = makeRndBboxRequest();
    GeoJSONParser geoJSONParser = new GeoJSONParser();

    try {
      String stringJSON =
          new String(
              Files.readAllBytes(
                  Paths.get(
                      "data/redlining/fullDownload.json"))); // We know this line of code is working

      geoJSONParser.parse(stringJSON);
      ServerInfo.setFullRedliningGeoJSON(geoJSONParser.getJSONData());
      List<Data.Feature> filteredFeatures = filterRLTDataHandler.filterBy(currBboxRequest);

      int featureCount = 0;
      for (Data.Feature feature : serverInfo.getFullRedliningGeoJSON().features()) {
        if (feature.geometry() != null) {
          if (currBboxRequest.contains(feature.geometry().coordinates().get(0).get(0))) {
            featureCount = featureCount + 1;
          }
        }
      }

      // tests that the amount is what we expect
      assertEquals(filteredFeatures.size(), featureCount);

      // tests that each feature is in fact in the bbox
      for (Data.Feature feature : filteredFeatures) {
        if (feature.geometry() != null) {
          assertTrue(currBboxRequest.contains(feature.geometry().coordinates().get(0).get(0)));
        }
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
