package com.diabolicallabs.htm.humantask;

import com.diabolicallabs.htm.HumanTask;
import com.diabolicallabs.htm.HumanTaskManagerVerticle;
import com.diabolicallabs.htm.HumanTaskStateEnum;
import com.diabolicallabs.htm.persist.reactivex.HumanTaskPersistService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.reactivex.core.Vertx;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.time.OffsetDateTime;

@RunWith(io.vertx.ext.unit.junit.VertxUnitRunner.class)
public class HumanTaskJUnitTest {
  String deploymentId;
  JsonObject config;
  MongoClient mongoClient;

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @BeforeClass
  public static void beforeClass() {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
  }

  @Before
  public void before(TestContext context) {

    Async async = context.async();

    //This is the mongo configuration
    config = new JsonObject()
      .put("host", "127.0.0.1")
      .put("port", 27017)
      .put("db_name", "diabolical_htm")
      .put("useObjectId", true);

    DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("mongo", config));

    Vertx rxVertx = Vertx.newInstance(rule.vertx());
    rxVertx.rxDeployVerticle(HumanTaskManagerVerticle.class.getName(), options)
      .map(id -> io.vertx.reactivex.ext.mongo.MongoClient.createShared(Vertx.newInstance(rule.vertx()), config))
      .flatMap(client -> client.rxDropCollection("htm.definition").toSingleDefault(client))
      .flatMap(client -> client.rxDropCollection("htm.task").toSingleDefault(client))
      .subscribe(
        client -> async.complete(),
        fault -> System.err.println("Unable to complete Before action " + fault.getMessage())
      );

  }

  @Test
  public void testCreate(TestContext context) {

    Async async = context.async();

    HumanTaskPersistService service = HumanTaskPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskPersistService.DEFAULT_ADDRESS);

    HumanTask humanTask = new HumanTask();
    humanTask.setDefinitionId("htd001");
    humanTask.setPriority(1);
    humanTask.setTimeCreated(Instant.now());
    humanTask.setTimeAssigned(Instant.now());
    humanTask.setTimeFinished(Instant.now());
    humanTask.setTimeWorked(Instant.now());
    humanTask.setTimeOfExpiration(Instant.now());
    humanTask.setParentTaskId(new ObjectId().toHexString());
    humanTask.setAssignedActorId("Amit");
    humanTask.setPayload("createMongoTable complete for HumanTaskManager");
    humanTask.setState(HumanTaskStateEnum.READY);

    service.rxCreate(humanTask)
      .doOnSuccess(context::assertNotNull)
      .subscribe(
        id -> async.complete(),
        context::fail
      );

  }

  @Test
  public void testRead(TestContext context) {


    Async async = context.async();

    //Get the proxy for the service at the address it was registered on
    HumanTaskPersistService service = HumanTaskPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskPersistService.DEFAULT_ADDRESS);

    HumanTask humanTask = new HumanTask();
    humanTask.setDefinitionId("createMongoTable");
    humanTask.setPriority(1);
    humanTask.setTimeCreated(Instant.now());
    humanTask.setTimeAssigned(Instant.now());
    humanTask.setTimeFinished(Instant.now());
    humanTask.setTimeWorked(Instant.now());
    humanTask.setTimeOfExpiration(Instant.now());
    humanTask.setParentTaskId(new ObjectId().toHexString());
    humanTask.setAssignedActorId("Amit");
    humanTask.setPayload("createMongoTable complete for HumanTaskManager");
    humanTask.setState(HumanTaskStateEnum.READY);

    service.rxCreate(humanTask)
      .doOnSuccess(context::assertNotNull)
      .flatMap(service::rxRead)
      .doOnSuccess(context::assertNotNull)
      .subscribe(
        id -> async.complete(),
        context::fail
      );


  }

  @Test
  public void testDelete(TestContext context) {

    Async async = context.async();

    //Get the proxy for the service at the address it was registered on
    HumanTaskPersistService service = HumanTaskPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskPersistService.DEFAULT_ADDRESS);

    HumanTask humanTask = new HumanTask();
    humanTask.setDefinitionId("createMongoTable");
    humanTask.setPriority(1);
    humanTask.setTimeCreated(Instant.now());
    humanTask.setTimeAssigned(Instant.now());
    humanTask.setTimeFinished(Instant.now());
    humanTask.setTimeWorked(Instant.now());
    humanTask.setTimeOfExpiration(Instant.now());
    humanTask.setParentTaskId(new ObjectId().toHexString());
    humanTask.setAssignedActorId("Amit");
    humanTask.setPayload("createMongoTable complete for HumanTaskManager");
    humanTask.setState(HumanTaskStateEnum.READY);

    service.rxCreate(humanTask)
      .doOnSuccess(context::assertNotNull)
      .flatMapCompletable(service::rxDelete)
      .subscribe(
        async::complete,
        context::fail
      );


  }

  @Test
  public void testUpdate(TestContext context) {

    Async async = context.async();

    HumanTaskPersistService service = HumanTaskPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskPersistService.DEFAULT_ADDRESS);

    HumanTask humanTask = new HumanTask();
    humanTask.setDefinitionId("createMongoTable");
    humanTask.setPriority(1);
    humanTask.setTimeCreated(Instant.now());
    humanTask.setTimeAssigned(Instant.now());
    humanTask.setTimeFinished(Instant.now());
    humanTask.setTimeFinished(Instant.now());
    humanTask.setTimeOfExpiration(Instant.now());
    humanTask.setParentTaskId(new ObjectId().toHexString());
    humanTask.setAssignedActorId("Amit");
    humanTask.setPayload("createMongoTable complete for HumanTaskManager");
    humanTask.setState(HumanTaskStateEnum.READY);

    service.rxCreate(humanTask)
      .doOnSuccess(context::assertNotNull)
      .flatMap(service::rxRead)
      .map(ht -> ht.setPriority(7))
      .flatMapCompletable(ht -> service.rxUpdate(ht.getId(), ht))
      .subscribe(
        async::complete,
        context::fail
      );

  }

  @Test
  public void testReplace(TestContext context) {

    Async async = context.async();

    //Get the proxy for the service at the address it was registered on
    HumanTaskPersistService service = HumanTaskPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskPersistService.DEFAULT_ADDRESS);

    HumanTask humanTask = new HumanTask();
    humanTask.setDefinitionId("createMongoTable");
    humanTask.setPriority(1);
    humanTask.setTimeCreated(Instant.now());
    humanTask.setTimeAssigned(Instant.now());
    humanTask.setTimeFinished(Instant.now());
    humanTask.setTimeFinished(Instant.now());
    humanTask.setTimeOfExpiration(Instant.now());
    humanTask.setParentTaskId(new ObjectId().toHexString());
    humanTask.setAssignedActorId("Amit");
    humanTask.setPayload("createMongoTable complete for HumanTaskManager");
    humanTask.setState(HumanTaskStateEnum.READY);

    service.rxCreate(humanTask)
      .doOnSuccess(context::assertNotNull)
      .flatMap(service::rxRead)
      .map(ht -> ht.setPriority(7))
      .flatMapCompletable(ht -> service.rxReplace(ht.getId(), ht))
      .subscribe(
        async::complete,
        context::fail
      );

  }


}
