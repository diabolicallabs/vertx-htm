package com.diabolicallabs.htm;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.SharedData;
import io.vertx.serviceproxy.ProxyHelper;

public class HumanTaskManagerServiceVerticle extends AbstractVerticle {

    HumanTaskManagerService htmService ;

    @Override
    public void start() {

        EventBus eb = vertx.eventBus();
        SharedData sd = vertx.sharedData();
        Logger logger = LoggerFactory.getLogger(this.getClass().getName());

        htmService = new HumanTaskManagerServiceImpl(vertx, config());

        ProxyHelper.registerService(HumanTaskManagerService.class, vertx, htmService, HumanTaskManagerService.DEFAULT_ADDRESS);

    }
}
