package edu.brown.cs.student.main.geoJSON;

/** Class to have mocked JSON strings that we might expect to need to parse. */
public class GeoJSONMocks {
  public String mock1;
  public String mock2;
  public String mock3;

  /** Constructor for GeoJSONMocks class, assigning the fields to a JSON string. */
  public GeoJSONMocks() {
    this.mock1 = "{\"type\": \"FeatureCollection\"}";
    this.mock2 =
        "{\n"
            + "  \"type\": \"FeatureCollection\",\n"
            + "  \"features\": [\n"
            + "    {\n"
            + "      \"type\": \"Feature\",\n"
            + "      \"properties\": {},\n"
            + "      \"geometry\": {\n"
            + "        \"coordinates\": [\n"
            + "          80.78780107739601,\n"
            + "          7.24388304715724\n"
            + "        ],\n"
            + "        \"type\": \"Point\"\n"
            + "      }\n"
            + "    },\n"
            + "    {\n"
            + "      \"type\": \"Feature\",\n"
            + "      \"properties\": {},\n"
            + "      \"geometry\": {\n"
            + "        \"coordinates\": [\n"
            + "          118.93901446878175,\n"
            + "          31.662726637385276\n"
            + "        ],\n"
            + "        \"type\": \"Point\"\n"
            + "      }\n"
            + "    },\n"
            + "    {\n"
            + "      \"type\": \"Feature\",\n"
            + "      \"properties\": {},\n"
            + "      \"geometry\": {\n"
            + "        \"coordinates\": [\n"
            + "          -143.20284028007845,\n"
            + "          16.071395435903483\n"
            + "        ],\n"
            + "        \"type\": \"Point\"\n"
            + "      }\n"
            + "    }\n"
            + "  ]\n"
            + "}";
    this.mock3 =
        "{\n"
            + "  \"type\": \"Feature\",\n"
            + "  \"geometry\": {\n"
            + "    \"type\": \"Point\",\n"
            + "    \"coordinates\": [125.6, 10.1]\n"
            + "  },\n"
            + "  \"properties\": {\n"
            + "    \"name\": \"Dinagat Islands\"\n"
            + "  }\n"
            + "}";
  }
}
