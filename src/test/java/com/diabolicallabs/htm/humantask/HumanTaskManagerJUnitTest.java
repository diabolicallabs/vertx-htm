package com.diabolicallabs.htm.humantask;

import com.diabolicallabs.htm.HumanTaskDefinition;
import com.diabolicallabs.htm.HumanTaskManagerVerticle;
import com.diabolicallabs.htm.persist.reactivex.HumanTaskDefinitionPersistService;
import com.diabolicallabs.htm.reactivex.HumanTaskManagerService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mongo.MongoClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

@RunWith(io.vertx.ext.unit.junit.VertxUnitRunner.class)
public class HumanTaskManagerJUnitTest {

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
    JsonObject config = new JsonObject()
      .put("host", "127.0.0.1")
      .put("port", 27017)
      .put("db_name", "diabolical_htm")
      .put("useObjectId", true);

    DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("mongo", config));

    Vertx rxVertx = Vertx.newInstance(rule.vertx());
    rxVertx.rxDeployVerticle(HumanTaskManagerVerticle.class.getName(), options)
      .map(id -> MongoClient.createShared(Vertx.newInstance(rule.vertx()), config))
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

    HumanTaskDefinitionPersistService htdService = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);
    HumanTaskManagerService service = HumanTaskManagerService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.HumanTaskManagerService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    htdService.rxCreate(htmDefin)
      .flatMap(id -> service.rxCreate("htm001"))
      .doOnSuccess(context::assertNotNull)
      .doOnError(fault -> System.err.println(fault.getMessage()))
      .subscribe(
        id -> async.complete(),
        context::fail
      );
  }

  @Test
  public void testAssign(TestContext context) {
    Async async = context.async();

    HumanTaskDefinitionPersistService htdService = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);
    HumanTaskManagerService htService = HumanTaskManagerService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.HumanTaskManagerService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    htdService.rxCreate(htmDefin)
      .flatMap(id -> htService.rxCreate("htm001"))
      .flatMapCompletable(humanTask -> htService.rxAssign(humanTask.getId(), "Amit"))
      .doOnError(fault -> System.err.println(fault.getMessage()))
      .subscribe(
        () -> async.complete(),
        context::fail
      );

  }

  @Test
  public void testRelease(TestContext context) {
    Async async = context.async();

    HumanTaskDefinitionPersistService htdService = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);
    HumanTaskManagerService htService = HumanTaskManagerService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.HumanTaskManagerService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    htdService.rxCreate(htmDefin)
      .flatMap(id -> htService.rxCreate("htm001")
        .flatMap(humanTask -> htService.rxAssign(humanTask.getId(), "Amit")
          .toSingleDefault(humanTask.getId()))
      )

      .flatMapCompletable(id -> htService.rxRelease(id))
      .doOnError(fault -> {
        fault.printStackTrace();
        System.err.println(fault.getMessage());
      })
      .subscribe(
        () -> async.complete(),
        context::fail
      );
  }

  @Test
  public void testStart(TestContext context) {
    Async async = context.async();

    HumanTaskDefinitionPersistService htdService = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);
    HumanTaskManagerService service = HumanTaskManagerService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.HumanTaskManagerService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    htdService.rxCreate(htmDefin)
      .flatMap(id -> service.rxCreate("htm001"))
      .doOnSuccess(humanTask -> System.out.println("Created human task: " + humanTask.toString()))
      .flatMapCompletable(ht -> service.rxStart(ht.getId()))
      .subscribe(
        async::complete,
        context::fail
      );

  }

  @Test
  public void testSuspend(TestContext context) {
    Async async = context.async();

    HumanTaskDefinitionPersistService htdService = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);
    HumanTaskManagerService service = HumanTaskManagerService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.HumanTaskManagerService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    htdService.rxCreate(htmDefin)
      .flatMap(id -> service.rxCreate("htm001"))
      .flatMapCompletable(ht -> service.rxStart(ht.getId()).andThen(service.rxSuspend(ht.getId())))
      .subscribe(
        async::complete,
        context::fail
      );

  }

  @Test
  public void testResume(TestContext context) {
    Async async = context.async();

    HumanTaskDefinitionPersistService htdService = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);
    HumanTaskManagerService service = HumanTaskManagerService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.HumanTaskManagerService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    htdService.rxCreate(htmDefin)
      .flatMap(id -> service.rxCreate("htm001"))
      .flatMapCompletable(ht -> service.rxStart(ht.getId())
        .andThen(service.rxSuspend(ht.getId())
          .andThen(service.rxResume(ht.getId()))
        ))
      .subscribe(
        async::complete,
        context::fail
      );
  }


}
