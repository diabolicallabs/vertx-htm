package com.diabolicallabs.htm.persist;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.SharedData;
import io.vertx.serviceproxy.ProxyHelper;

public class HumanTaskPersistVerticle extends AbstractVerticle {

    HumanTaskPersistService htmService ;

    @Override
    public void start() {

        EventBus eb = vertx.eventBus();
        SharedData sd = vertx.sharedData();
        Logger logger = LoggerFactory.getLogger(this.getClass().getName());

        htmService = new HumanTaskPersistMongoServiceImpl(vertx, config());

        ProxyHelper.registerService(HumanTaskPersistService.class, vertx, htmService, HumanTaskPersistService.DEFAULT_ADDRESS);

    }


}