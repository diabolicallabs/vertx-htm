package com.diabolicallabs.htm;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

@ProxyGen
@VertxGen
public interface HumanTaskManagerService {

    String DEFAULT_ADDRESS = "htm.service";

    static HumanTaskManagerService createProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(HumanTaskManagerService.class, vertx, address);
    }

    void create(String taskDefinitionId, Handler<AsyncResult<HumanTask>> createHandler);
    void assign(String taskId, String actorId, Handler<AsyncResult<Void>> assignHandler);
    void release(String taskId, Handler<AsyncResult<Void>> releaseHandler);

    void start(String taskId, Handler<AsyncResult<Void>> startHandler);
    void suspend(String taskId, Handler<AsyncResult<Void>> suspendHandler);
    void resume(String taskId, Handler<AsyncResult<Void>> resumeHandler);
    void finish(String taskId, JsonObject outputData, Handler<AsyncResult<Void>> finishHandler);

    void skip(String taskId, Handler<AsyncResult<Void>> skipHandler);
    void delegate(String taskId, String targetActorId, Handler<AsyncResult<Void>> delegateHandler);

}
