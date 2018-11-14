package com.diabolicallabs.htm.persist;

import com.diabolicallabs.htm.HumanTaskDefinition;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ProxyHelper;

@ProxyGen
@VertxGen
public interface HumanTaskDefinitionPersistService {

  String DEFAULT_ADDRESS = "htmd.persist";

  static HumanTaskDefinitionPersistService createProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(HumanTaskDefinitionPersistService.class, vertx, address);
    }

    void create(HumanTaskDefinition htmDefinition, Handler<AsyncResult<String>> createHandler);

    void read(String htmDefinId, Handler<AsyncResult<HumanTaskDefinition>> readHandler);

    void update(String htmDefinId, HumanTaskDefinition htmDefinition, Handler<AsyncResult<Void>> updateHandler);

    void delete(String htmDefinId, Handler<AsyncResult<Void>> deleteHandler);

    void replace(String htmDefinId,HumanTaskDefinition htmDefinition, Handler<AsyncResult<Void>> replaceHandler);


}
