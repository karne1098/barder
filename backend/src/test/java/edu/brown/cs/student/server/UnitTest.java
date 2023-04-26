package edu.brown.cs.student.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.LoadRLTDataHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** unit testing serializing miscellaneous other code related to the server */
public class UnitTest {
  /** unit testing serializing */
  @Test
  public void serializeTest() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));
    Map example = new HashMap();
    example.put("hi", "kathryn");
    example.put("hey", "jessica");

    Map resp = adapter.fromJson(LoadRLTDataHandler.serialize(example));

    assertEquals("kathryn", resp.get("hi"));
    assertEquals("jessica", resp.get("hey"));
  }

  /** unit testing fail construction */
  @Test
  public void failTest() {
    String path = "data/stars/ten-sta.csv";
    Map failedResponse = LoadRLTDataHandler.fail("error_bad_request", "invalid filepath");

    assertEquals("error_bad_request", failedResponse.get("result"));
    assertEquals("invalid filepath", failedResponse.get("message"));
  }
}
