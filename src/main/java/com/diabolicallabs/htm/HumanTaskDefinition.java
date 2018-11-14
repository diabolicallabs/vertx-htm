package com.diabolicallabs.htm;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@DataObject
public class HumanTaskDefinition extends BaseDataObject {

  private String id;
  private String name;
  private String description;
  private List<String> potentialOwnerIds;
  private Duration expectedDuration;

  public HumanTaskDefinition() {
    super();
  }

  public HumanTaskDefinition(JsonObject json) {
    super(json);
  }

  public static HumanTaskDefinition fromBson(JsonObject json) {

    logger.info("JSON before BSON decode {}", json);

    decodeBsonId("id", json);

    logger.info("JSON after BSON decode {}", json);

    return new HumanTaskDefinition(json);
  }

  public JsonObject toBson() {

    JsonObject bson = super.toBson();

    logger.info("JSON before BSON encode {}", bson);

    encodeBsonId("id", bson);

    logger.info("JSON after BSON encode {}", bson);

    return bson;
  }

  public String getId() {
    return id;
  }

  public HumanTaskDefinition setId(String id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public HumanTaskDefinition setName(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public HumanTaskDefinition setDescription(String description) {
    this.description = description;
    return this;
  }

  public List<String> getPotentialOwnerIds() {
    return potentialOwnerIds;
  }

  public HumanTaskDefinition setPotentialOwnerIds(List<String> potentialOwnerIds) {
    this.potentialOwnerIds = potentialOwnerIds;
    return this;
  }

  public Duration getExpectedDuration() {
    return expectedDuration;
  }

  public HumanTaskDefinition setExpectedDuration(Duration expectedDuration) {
    this.expectedDuration = expectedDuration;
    return this;
  }

  @Override
  public String toString() {
    return "HumanTaskDefinition{" +
      "id='" + id + '\'' +
      ", name='" + name + '\'' +
      ", description='" + description + '\'' +
      ", potentialOwnerIds=" + potentialOwnerIds +
      ", expectedDuration=" + expectedDuration +
      '}';
  }
}
