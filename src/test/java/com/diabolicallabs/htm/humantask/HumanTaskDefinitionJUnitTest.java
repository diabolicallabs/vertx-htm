package com.diabolicallabs.htm.humantask;

import com.diabolicallabs.htm.HumanTaskDefinition;
import com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistVerticle;
import com.diabolicallabs.htm.persist.reactivex.HumanTaskDefinitionPersistService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.reactivex.core.Vertx;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

@RunWith(io.vertx.ext.unit.junit.VertxUnitRunner.class)
public class HumanTaskDefinitionJUnitTest {

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

    DeploymentOptions options = new DeploymentOptions().setConfig(config);

    Vertx rxVertx = Vertx.newInstance(rule.vertx());
    rxVertx.rxDeployVerticle(HumanTaskDefinitionPersistVerticle.class.getName(), options)
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

    //Get the proxy for the service at the address it was registered on
    HumanTaskDefinitionPersistService service = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    service.rxCreate(htmDefin)
      .doOnSuccess(context::assertNotNull)
      .subscribe(
        id -> async.complete(),
        context::fail
      );

  }

  @Test
  public void readTest(TestContext context) {

    Async async = context.async();

    //Get the proxy for the service at the address it was registered on
    HumanTaskDefinitionPersistService service = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    service.rxCreate(htmDefin)
      .doOnSuccess(context::assertNotNull)
      .flatMap(id -> service.rxRead("htm001"))
      .doOnSuccess(ht -> {
        context.assertEquals("htm001", htmDefin.getId());
        context.assertEquals("createMongoTable", htmDefin.getName());
        context.assertEquals("Create Mongo Persist Service", htmDefin.getDescription());
        context.assertEquals("Amit", htmDefin.getPotentialOwnerId());
        context.assertEquals(100l, htmDefin.getExpectedDuration());
        context.assertEquals("Dummy", htmDefin.getPayloadType());
        context.assertNotNull(htmDefin.getEffectiveStartTime());
        context.assertNotNull(htmDefin.getEffectiveEndTime());
      })
      .subscribe(
        ht -> async.complete(),
        context::fail
      );

  }

  @Test
  public void updateTest(TestContext context) {

    Async async = context.async();

    //Get the proxy for the service at the address it was registered on
    HumanTaskDefinitionPersistService service = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    service.rxCreate(htmDefin)
      .doOnSuccess(context::assertNotNull)
      .flatMap(id -> service.rxRead("htm001"))
      .doOnSuccess(ht -> {
        context.assertEquals("htm001", ht.getId());
        context.assertEquals("createMongoTable", ht.getName());
        context.assertEquals("Create Mongo Persist Service", ht.getDescription());
        context.assertEquals("Amit", ht.getPotentialOwnerId());
        context.assertEquals(100l, ht.getExpectedDuration());
        context.assertEquals("Dummy", ht.getPayloadType());
        context.assertNotNull(ht.getEffectiveStartTime());
        context.assertNotNull(ht.getEffectiveEndTime());
      })
      .map(ht -> ht.setName("Goose"))
      .flatMapCompletable(ht -> service.rxUpdate("htm001", ht))
      .andThen(service.rxRead("htm001"))
      .doOnSuccess(ht -> {
        context.assertEquals("Goose", ht.getName());
      })
      .subscribe(
        ht -> async.complete(),
        context::fail
      );

  }

  @Test
  public void deleteTest(TestContext context) {

    Async async = context.async();

    //Get the proxy for the service at the address it was registered on
    HumanTaskDefinitionPersistService service = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    service.rxCreate(htmDefin)
      .doOnSuccess(context::assertNotNull)
      .flatMapCompletable(id -> service.rxDelete("htm001"))
      .andThen(service.rxRead("htm001"))
      .doOnSuccess(context::assertNull)
      .subscribe(
        ht -> async.complete(),
        context::fail
      );

  }

  @Test
  public void replaceTest(TestContext context) {

    Async async = context.async();

    HumanTaskDefinitionPersistService service = HumanTaskDefinitionPersistService.createProxy(Vertx.newInstance(rule.vertx()), com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);

    HumanTaskDefinition htmDefin = new HumanTaskDefinition();
    htmDefin.setId("htm001");
    htmDefin.setName("createMongoTable");
    htmDefin.setDescription("Create Mongo Persist Service");
    htmDefin.setPotentialOwnerId("Amit");
    htmDefin.setExpectedDuration(100l);
    htmDefin.setPayloadType("Dummy");
    htmDefin.setEffectiveStartTime(Instant.now());
    htmDefin.setEffectiveEndTime(Instant.now());

    HumanTaskDefinition htmDefinRep = new HumanTaskDefinition();
    htmDefinRep.setId("htm001");
    htmDefinRep.setName("createMongoTable1");
    //htmDefinRep.description = "Create Mongo Persist Service1 ";
    //htmDefinRep.potentialOwnerId = "Amit 1";
    //htmDefinRep.expectedDuration = 100l;
    //htmDefinRep.payloadType = "Dummy";
    //htmDefinRep.effectiveStartTime = OffsetDateTime.now();
    //htmDefinRep.effectiveEndTime = OffsetDateTime.now();

    service.rxCreate(htmDefin)
      .doOnSuccess(context::assertNotNull)
      .flatMapCompletable(id -> service.rxUpdate("htm001", htmDefinRep))
      .andThen(service.rxRead("htm001"))
      .doOnSuccess(ht -> context.assertEquals("createMongoTable1", ht.getName()))
      .subscribe(
        ht -> async.complete(),
        context::fail
      );

  }


}
