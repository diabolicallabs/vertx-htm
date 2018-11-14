package com.diabolicallabs.htm;

import com.diabolicallabs.htm.persist.HumanTaskDefinitionPersistVerticle;
import com.diabolicallabs.htm.persist.HumanTaskPersistVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class HumanTaskManagerVerticle extends AbstractVerticle{

    @Override
    public void start() {

        //Passes the configuration from the command line to the ExchangeVertical
        DeploymentOptions htmOptions = new DeploymentOptions()
                .setConfig(config().getJsonObject("mongo"));

        vertx.deployVerticle(HumanTaskPersistVerticle.class.getName(), htmOptions);
        vertx.deployVerticle(HumanTaskDefinitionPersistVerticle.class.getName(), htmOptions);
        vertx.deployVerticle(HumanTaskManagerServiceVerticle.class.getName(), htmOptions);

    }
}
