package edu.brown.cs.student.main.geoJSON;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.geoJSON.geoJSONData.*;
import java.io.IOException;

/** Class to parse the GeoJSON string and store it as a Data.GeoJSON object. */
public class GeoJSONParser {
  private Data.GeoJSON geoJSONData;

  /**
   * Function that parses a GeoJSON string to a Data.GeoJSON object.
   *
   * @param json the GeoJSON string we need to parse
   * @throws IOException
   */
  public void parse(String json) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    this.geoJSONData = moshi.adapter(Data.GeoJSON.class).fromJson(json);
  }

  /**
   * getter function for the private json data in the parser
   *
   * @return the Map<String, Object> that is our internal representation of the geojson
   */
  public Data.GeoJSON getJSONData() {
    return this.geoJSONData;
  }
}
