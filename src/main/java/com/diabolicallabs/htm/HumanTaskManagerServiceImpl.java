package com.diabolicallabs.htm;

import com.diabolicallabs.htm.persist.HumanTaskPersistMongoServiceImpl;
import com.diabolicallabs.htm.persist.reactivex.HumanTaskDefinitionPersistService;
import com.diabolicallabs.htm.persist.reactivex.HumanTaskPersistService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;

public class HumanTaskManagerServiceImpl implements HumanTaskManagerService {

  Logger logger = LoggerFactory.getLogger(HumanTaskManagerServiceImpl.class);

  Vertx vertx;
  JsonObject config;
  HumanTaskPersistService humanTaskPersistService;

  public HumanTaskManagerServiceImpl(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
    humanTaskPersistService = HumanTaskPersistService.createProxy(io.vertx.reactivex.core.Vertx.newInstance(vertx), com.diabolicallabs.htm.persist.HumanTaskPersistService.DEFAULT_ADDRESS);
  }

  @Override
  public void create(String taskDefinitionId, Handler<AsyncResult<HumanTask>> createHandler) {

    HumanTaskDefinitionPersistService htdService = HumanTaskDefinitionPersistService.createProxy(io.vertx.reactivex.core.Vertx.newInstance(vertx), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);
    htdService.rxRead(taskDefinitionId)
      .doOnSuccess(humanTaskDefinition -> {
        if (humanTaskDefinition == null)
          createHandler.handle(Future.failedFuture("Task definition with id " + taskDefinitionId + " didn't exists"));
      })
      .filter(Objects::nonNull)
      .map(humanTaskDefinition -> new HumanTask()
        .setDefinitionId(taskDefinitionId)
        .setTimeCreated(Instant.now())
        .setState(HumanTaskStateEnum.valueOf("READY"))
      )
    .flatMapSingle(humanTask -> humanTaskPersistService.rxCreate(humanTask).map(humanTask::setId))
    .doOnError(fault -> logger.error("Unable to create task with definition ID {}", fault, taskDefinitionId))
    .subscribe(
      humanTask -> createHandler.handle(Future.succeededFuture(humanTask)),
      fault -> createHandler.handle(Future.failedFuture(fault))
    );


  }

  @Override
  public void assign(String taskId, String actorId, Handler<AsyncResult<Void>> assignHandler) {

    humanTaskPersistService.rxRead(taskId)
      .map(humanTask -> humanTask.setAssignedActorId(actorId)
        .setTimeAssigned(Instant.now())
        .setState(HumanTaskStateEnum.ASSIGNED))
      .flatMapCompletable(humanTask -> humanTaskPersistService.rxUpdate(taskId, humanTask))
      .subscribe(
        () -> assignHandler.handle(Future.succeededFuture()),
        fault -> assignHandler.handle(Future.failedFuture(fault))
      );
  }

  @Override
  public void release(String taskId, Handler<AsyncResult<Void>> releaseHandler) {

    humanTaskPersistService.rxRead(taskId)
      .map(humanTask -> humanTask.setAssignedActorId(null)
        .setTimeAssigned(Instant.now())
        .setState(HumanTaskStateEnum.RELEASED))
      .flatMapCompletable(humanTask -> humanTaskPersistService.rxUpdate(taskId, humanTask))
      .subscribe(
        () -> releaseHandler.handle(Future.succeededFuture()),
        fault -> releaseHandler.handle(Future.failedFuture(fault))
      );
  }

  @Override
  public void start(String taskId, Handler<AsyncResult<Void>> startHandler) {

    logger.info("About to start task with id: {}", taskId);

    humanTaskPersistService.rxRead(taskId)
      .doOnSuccess(ht -> logger.info("Found human task {}", ht.toJson().encode()))
      .map(humanTask -> humanTask.setState(HumanTaskStateEnum.STARTED))
      .flatMapCompletable(humanTask -> humanTaskPersistService.rxUpdate(taskId, humanTask))
      .doOnComplete(() -> logger.info("Human task {} started", taskId))
      .subscribe(
        () -> startHandler.handle(Future.succeededFuture()),
        fault -> startHandler.handle(Future.failedFuture(fault))
      );

  }

  @Override
  public void suspend(String taskId, Handler<AsyncResult<Void>> suspendHandler) {

    humanTaskPersistService.rxRead(taskId)
      .map(humanTask -> humanTask.setState(HumanTaskStateEnum.SUSPENDED))
      .flatMapCompletable(humanTask -> humanTaskPersistService.rxUpdate(taskId, humanTask))
      .subscribe(
        () -> suspendHandler.handle(Future.succeededFuture()),
        fault -> suspendHandler.handle(Future.failedFuture(fault))
      );
  }

  @Override
  public void resume(String taskId, Handler<AsyncResult<Void>> resumeHandler) {

    humanTaskPersistService.rxRead(taskId)
      .map(humanTask -> humanTask.setState(HumanTaskStateEnum.RESUMED))
      .flatMapCompletable(humanTask -> humanTaskPersistService.rxUpdate(taskId, humanTask))
      .subscribe(
        () -> resumeHandler.handle(Future.succeededFuture()),
        fault -> resumeHandler.handle(Future.failedFuture(fault))
      );
  }

  @Override
  public void finish(String taskId, JsonObject outputData, Handler<AsyncResult<Void>> finishHandler) {

    humanTaskPersistService.rxRead(taskId)
      .map(humanTask -> humanTask.setTimeFinished(Instant.now())
        .setState(HumanTaskStateEnum.FINISHED))
      .flatMapCompletable(humanTask -> humanTaskPersistService.rxUpdate(taskId, humanTask))
      .subscribe(
        () -> finishHandler.handle(Future.succeededFuture()),
        fault -> finishHandler.handle(Future.failedFuture(fault))
      );
  }

  @Override
  public void skip(String taskId, Handler<AsyncResult<Void>> skipHandler) {

    humanTaskPersistService.rxRead(taskId)
      .map(humanTask -> humanTask.setState(HumanTaskStateEnum.SKIPPED))
      .flatMapCompletable(humanTask -> humanTaskPersistService.rxUpdate(taskId, humanTask))
      .subscribe(
        () -> skipHandler.handle(Future.succeededFuture()),
        fault -> skipHandler.handle(Future.failedFuture(fault))
      );

  }

  @Override
  public void delegate(String taskId, String targetActorId, Handler<AsyncResult<Void>> delegateHandler) {

    humanTaskPersistService.rxRead(taskId)
      .map(humanTask -> humanTask.setAssignedActorId(targetActorId)
        .setTimeAssigned(Instant.now())
        .setState(HumanTaskStateEnum.ASSIGNED))
      .flatMapCompletable(humanTask -> humanTaskPersistService.rxUpdate(taskId, humanTask))
      .subscribe(
        () -> delegateHandler.handle(Future.succeededFuture()),
        fault -> delegateHandler.handle(Future.failedFuture(fault))
      );
  }
}
