package com.diabolicallabs.htm.persist;

import com.diabolicallabs.htm.HumanTask;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mongo.MongoClient;

import java.util.Objects;

public class HumanTaskPersistMongoServiceImpl implements HumanTaskPersistService {

  final static String COLLECTION_NAME = "htm.task";
  final static Logger logger = LoggerFactory.getLogger(HumanTaskPersistMongoServiceImpl.class);

  Vertx vertx;
  JsonObject config;
  MongoClient mongoClient;

  public HumanTaskPersistMongoServiceImpl(io.vertx.core.Vertx vertx, JsonObject config) {
    logger.info("New HumanTaskPersistMongoServiceImpl created with {}", config.encode());
    this.vertx = Vertx.newInstance(vertx);
    this.config = config;
    mongoClient = MongoClient.createShared(this.vertx, config);
  }

  @Override
  public void create(HumanTask htm, Handler<AsyncResult<String>> createHandler) {

    mongoClient.rxSave(COLLECTION_NAME, htm.toBson())
      .subscribe(
        id -> createHandler.handle(Future.succeededFuture(id)),
        fault -> createHandler.handle(Future.failedFuture(fault))
      );

  }

  @Override
  public void read(String htmId, Handler<AsyncResult<HumanTask>> readHandler) {

    JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", htmId));
    JsonObject fields = new JsonObject();   //This will match all HumanTaskManager objects

    logger.info("About to find human task with query {}", query.encode());

    mongoClient.rxFindOne(COLLECTION_NAME, query, fields)
      .filter(Objects::nonNull)
      .doOnSuccess(jsonObject -> logger.info("Found human task {}", jsonObject.encode()))
      .map(HumanTask::fromBson)
      .doOnError(fault -> logger.error("Unable to read {}", fault, htmId))
      .subscribe(
        humanTask -> readHandler.handle(Future.succeededFuture(humanTask)),
        fault -> readHandler.handle(Future.failedFuture(fault)),
        () -> readHandler.handle(Future.succeededFuture())
      );

  }

  @Override
  public void delete(String htmId, Handler<AsyncResult<Void>> deleteHandler) {

    JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", htmId));

    mongoClient.rxRemove(COLLECTION_NAME, query)
      .subscribe(
        () -> deleteHandler.handle(Future.succeededFuture()),
        fault -> deleteHandler.handle(Future.failedFuture(fault))
      );
  }

  @Override
  public void update(String htmId, HumanTask htm, Handler<AsyncResult<Void>> updateHandler) {

    JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", htmId));
    JsonObject update = new JsonObject().put("$set", htm.toBson());

    mongoClient.rxUpdate(COLLECTION_NAME, query, update)
      .subscribe(
        () -> updateHandler.handle(Future.succeededFuture()),
        fault -> updateHandler.handle(Future.failedFuture(fault))
      );

  }

  @Override
  public void replace(String htmId, HumanTask htm, Handler<AsyncResult<Void>> replaceHandler) {

    JsonObject query = new JsonObject().put("_id", new JsonObject().put("$oid", htmId));

    mongoClient.rxReplace(COLLECTION_NAME, query, htm.toBson())
      .subscribe(
        () -> replaceHandler.handle(Future.succeededFuture()),
        fault -> replaceHandler.handle(Future.failedFuture(fault))
      );

  }

}
