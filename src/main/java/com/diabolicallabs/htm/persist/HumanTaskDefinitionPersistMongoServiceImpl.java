package com.diabolicallabs.htm.persist;

import com.diabolicallabs.htm.HumanTaskDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mongo.MongoClient;

import java.util.Objects;

public class HumanTaskDefinitionPersistMongoServiceImpl implements HumanTaskDefinitionPersistService {

  final static String COLLECTION_NAME = "htm.definition";

  final static Logger logger = LoggerFactory.getLogger(HumanTaskDefinitionPersistMongoServiceImpl.class);
  Vertx vertx;
  JsonObject config;
  MongoClient mongoClient;

  public HumanTaskDefinitionPersistMongoServiceImpl(io.vertx.core.Vertx vertx, JsonObject config) {
    logger.info("New HumanTaskDefinitionPersistMongoServiceImpl created with {}", config.encode());
    this.vertx = Vertx.newInstance(vertx);
    this.config = config;
    mongoClient = MongoClient.createShared(this.vertx, config);
  }

  @Override
  public void create(HumanTaskDefinition htmDefin, Handler<AsyncResult<String>> createHandler) {

    mongoClient.rxSave(COLLECTION_NAME, htmDefin.toBson())
      .subscribe(
        id -> createHandler.handle(Future.succeededFuture(id)),
        fault -> createHandler.handle(Future.failedFuture(fault))
      );
  }

  @Override
  public void read(String htmDefinId, Handler<AsyncResult<HumanTaskDefinition>> readHandler) {

    JsonObject query = new JsonObject().put("id", htmDefinId);
    JsonObject fields = new JsonObject();   //This will match all HumanTaskManagerDefinition objects

    mongoClient.rxFindOne(COLLECTION_NAME, query, fields)
      .doOnSuccess(jsonObject -> {
        if (jsonObject == null) readHandler.handle(Future.succeededFuture());
      })
      .filter(Objects::nonNull)
      .map(json -> {
        if (json.containsKey("effectiveStartTime"))
          json.put("effectiveStartTime", json.getJsonObject("effectiveStartTime").getString("$date"));
        if (json.containsKey("effectiveEndTime"))
          json.put("effectiveEndTime", json.getJsonObject("effectiveEndTime").getString("$date"));
        return json;
      })
      .map(HumanTaskDefinition::new)
      .subscribe(
        definition -> readHandler.handle(Future.succeededFuture(definition)),
        fault -> readHandler.handle(Future.failedFuture(fault))
      );

  }

  @Override
  public void update(String htmDefinId, HumanTaskDefinition htmDefin, Handler<AsyncResult<Void>> updateHandler) {

    JsonObject query = new JsonObject().put("id", htmDefinId);
    JsonObject update = new JsonObject().put("$set", htmDefin.toBson());

    mongoClient.rxUpdate(COLLECTION_NAME, query, update)
      .subscribe(
        () -> updateHandler.handle(Future.succeededFuture()),
        fault -> updateHandler.handle(Future.failedFuture(fault))
      );

  }

  @Override
  public void delete(String htmDefinId, Handler<AsyncResult<Void>> deleteHandler) {

    JsonObject query = new JsonObject().put("id", htmDefinId);

    mongoClient.rxRemove(COLLECTION_NAME, query)
      .subscribe(
        () -> deleteHandler.handle(Future.succeededFuture()),
        fault -> deleteHandler.handle(Future.failedFuture(fault))
      );

  }

  @Override
  public void replace(String htmDefinId, HumanTaskDefinition htmDefin, Handler<AsyncResult<Void>> replaceHandler) {

    JsonObject query = new JsonObject().put("id", htmDefinId);

    mongoClient.rxReplace(COLLECTION_NAME, query, htmDefin.toBson())
      .subscribe(
        () -> replaceHandler.handle(Future.succeededFuture()),
        fault -> replaceHandler.handle(Future.failedFuture(fault))
      );

  }

}
