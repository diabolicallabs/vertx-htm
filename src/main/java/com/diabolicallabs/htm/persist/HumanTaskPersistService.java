package com.diabolicallabs.htm.persist;

import com.diabolicallabs.htm.HumanTask;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.serviceproxy.ProxyHelper;
import io.vertx.core.Vertx;

@ProxyGen
@VertxGen
public interface HumanTaskPersistService {

  String DEFAULT_ADDRESS = "ht.persist";

  static HumanTaskPersistService createProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(HumanTaskPersistService.class, vertx, address);
    }

    void create(HumanTask htm, Handler<AsyncResult<String>> createHandler);

    void read(String htmId, Handler<AsyncResult<HumanTask>> readHandler);

    void delete(String htmId, Handler<AsyncResult<Void>> deleteHandler);

    void update(String htmId, HumanTask htm, Handler<AsyncResult<Void>> deleteHandler);

    void replace(String htmId, HumanTask htm, Handler<AsyncResult<Void>> replaceHandler);
}
