package com.diabolicallabs.htm.persist;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.SharedData;
import io.vertx.serviceproxy.ProxyHelper;

public class HumanTaskDefinitionPersistVerticle extends AbstractVerticle{

    HumanTaskDefinitionPersistService htmService ;

    @Override
    public void start() {

        EventBus eb = vertx.eventBus();
        SharedData sd = vertx.sharedData();
        Logger logger = LoggerFactory.getLogger(this.getClass().getName());

        htmService = new HumanTaskDefinitionPersistMongoServiceImpl(vertx, config());

        ProxyHelper.registerService(HumanTaskDefinitionPersistService.class, vertx, htmService, HumanTaskDefinitionPersistService.DEFAULT_ADDRESS);

    }


}
