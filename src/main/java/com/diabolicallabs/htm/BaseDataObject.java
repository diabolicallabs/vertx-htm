package com.diabolicallabs.htm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public abstract class BaseDataObject {

  protected static Logger logger = LoggerFactory.getLogger(BaseDataObject.class);

  private static ObjectMapper mapper;

  static {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    mapper = new ObjectMapper()
      .registerModule(new ParameterNamesModule())
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule())
      .setDateFormat(formatter)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  public BaseDataObject() {
  }

  public BaseDataObject(JsonObject json) {

    try {
      mapper.readerForUpdating(this).readValue(json.encode());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public JsonObject toJson() {

    try {
      String sJson = mapper.writeValueAsString(this);
      return new JsonObject(sJson);
    } catch (JsonProcessingException e) {
      logger.error("Unable to encode {}", e, this);
      throw new RuntimeException(e);
    }
  }

  public JsonObject toBson() {
    return this.toJson();
  }

  protected static void encodeBsonId(String key, JsonObject json) {

    if (json.containsKey(key)) {
      json.put("_id", new JsonObject().put("$oid", json.getString(key)));
      json.remove(key);
    }
  }

  protected static void decodeBsonId(String key, JsonObject json) {

    if (json.containsKey("_id")) {
      if (json.getValue("_id") instanceof String) {
        json.put(key, json.getString("_id"));
      } else {
        json.put(key, json.getJsonObject("_id").getString("$oid"));
      }
      json.remove("_id");
    }
  }

  protected static void encodeBsonOid(String key, JsonObject json) {

    if (json.containsKey(key)) {
      json.put(key, new JsonObject().put("$oid", json.getString(key)));
    }
  }

  protected static void decodeBsonOid(String key, JsonObject json) {

    if (json.containsKey(key)) {
      json.put(key, json.getJsonObject(key).getString("$oid"));
    }
  }

  protected static JsonObject encodeBsonBinary(String key, JsonObject json) {
    return new JsonObject().put("$binary", json.getValue(key));
  }

  protected static byte[] decodeBsonBinary(String key, JsonObject json) {

    if (json.containsKey(key)) {
      try {
        return json.getJsonObject(key).getBinary("$binary");
      } catch (ClassCastException cce) {
        return json.getBinary(key);
      }
    }

    return null;
  }

  protected static void encodeBsonDateTime(String key, JsonObject json) {

    if (json.containsKey(key)) {
      json.put(key, new JsonObject().put("$date", json.getString(key)));
    }
  }

  protected static void decodeBsonDateTime(String key, JsonObject json) {

    if (json.containsKey(key))
      json.put(key, json.getJsonObject(key).getString("$date"));
  }
}
