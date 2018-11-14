package com.diabolicallabs.htm;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

@DataObject
public class HumanTask extends BaseDataObject {

  private String id;
  private String definitionId;
  private Integer priority;
  private Instant timeCreated = Instant.now();
  private Instant timeAssigned;
  private Instant timeFinished;
  private Instant timeWorked;
  private Instant timeOfExpiration;
  private String parentTaskId;
  private String assignedActorId;
  private String payload;
  private HumanTaskStateEnum state;

  public HumanTask() {
    super();
  }

  public HumanTask(JsonObject json) {
    super(json);
  }

  public JsonObject toBson() {

    JsonObject bson = super.toBson();

    logger.info("JSON before BSON encode {}", bson);

    encodeBsonId("id", bson);
    encodeBsonOid("parentTaskId", bson);
    encodeBsonDateTime("timeCreated", bson);
    encodeBsonDateTime("timeAssigned", bson);
    encodeBsonDateTime("timeFinished", bson);
    encodeBsonDateTime("timeWorked", bson);
    encodeBsonDateTime("timeOfExpiration", bson);

    logger.info("JSON after BSON encode {}", bson);

    return bson;
  }

  public static HumanTask fromBson(JsonObject json) {

    logger.info("JSON before BSON decode {}", json);

    decodeBsonId("id", json);
    decodeBsonOid("parentTaskId", json);
    decodeBsonDateTime("timeCreated", json);
    decodeBsonDateTime("timeAssigned", json);
    decodeBsonDateTime("timeFinished", json);
    decodeBsonDateTime("timeWorked", json);
    decodeBsonDateTime("timeOfExpiration", json);

    logger.info("JSON after BSON decode {}", json);

    return new HumanTask(json);
  }

  public String getId() {
    return id;
  }

  public HumanTask setId(String id) {
    this.id = id;
    return this;
  }

  public String getDefinitionId() {
    return definitionId;
  }

  public HumanTask setDefinitionId(String definitionId) {
    this.definitionId = definitionId;
    return this;
  }

  public Integer getPriority() {
    return priority;
  }

  public HumanTask setPriority(Integer priority) {
    this.priority = priority;
    return this;
  }

  public Instant getTimeCreated() {
    return timeCreated;
  }

  public HumanTask setTimeCreated(Instant timeCreated) {
    this.timeCreated = timeCreated;
    return this;
  }

  public Instant getTimeAssigned() {
    return timeAssigned;
  }

  public HumanTask setTimeAssigned(Instant timeAssigned) {
    this.timeAssigned = timeAssigned;
    return this;
  }

  public Instant getTimeFinished() {
    return timeFinished;
  }

  public HumanTask setTimeFinished(Instant timeFinished) {
    this.timeFinished = timeFinished;
    return this;
  }

  public Instant getTimeWorked() {
    return timeWorked;
  }

  public HumanTask setTimeWorked(Instant timeWorked) {
    this.timeWorked = timeWorked;
    return this;
  }

  public Instant getTimeOfExpiration() {
    return timeOfExpiration;
  }

  public HumanTask setTimeOfExpiration(Instant timeOfExpiration) {
    this.timeOfExpiration = timeOfExpiration;
    return this;
  }

  public String getParentTaskId() {
    return parentTaskId;
  }

  public HumanTask setParentTaskId(String parentTaskId) {
    this.parentTaskId = parentTaskId;
    return this;
  }

  public String getAssignedActorId() {
    return assignedActorId;
  }

  public HumanTask setAssignedActorId(String assignedActorId) {
    this.assignedActorId = assignedActorId;
    return this;
  }

  public String getPayload() {
    return payload;
  }

  public HumanTask setPayload(String payload) {
    this.payload = payload;
    return this;
  }

  public HumanTaskStateEnum getState() {
    return state;
  }

  public HumanTask setState(HumanTaskStateEnum state) {
    this.state = state;
    return this;
  }

  @Override
  public String toString() {
    return "HumanTask{" +
      "id='" + id + '\'' +
      ", definitionId='" + definitionId + '\'' +
      ", priority=" + priority +
      ", timeCreated=" + timeCreated +
      ", timeAssigned=" + timeAssigned +
      ", timeFinished=" + timeFinished +
      ", timeWorked=" + timeWorked +
      ", timeOfExpiration=" + timeOfExpiration +
      ", parentTaskId='" + parentTaskId + '\'' +
      ", assignedActorId='" + assignedActorId + '\'' +
      ", payload='" + payload + '\'' +
      ", state=" + state +
      '}';
  }
}
