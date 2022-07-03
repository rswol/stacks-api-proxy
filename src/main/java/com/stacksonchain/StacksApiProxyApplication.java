package com.stacksonchain;

import com.google.inject.Injector;
import com.stacksonchain.core.KongConfigurator;
import com.stacksonchain.core.StacksApiHealthCheck;
import com.stacksonchain.ext.KongApiClient;
import com.stacksonchain.module.ServerModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.injector.lookup.InjectorLookup;

@Slf4j
public class StacksApiProxyApplication extends Application<StacksApiProxyConfiguration> {

    public static void main(final String[] args) throws Exception {
        new StacksApiProxyApplication().run(args);
    }

    @Override
    public String getName() {
        return "stacks-api-proxy";
    }

    @Override
    public void initialize(final Bootstrap<StacksApiProxyConfiguration> bootstrap) {
        bootstrap.addBundle(GuiceBundle.builder()
            .modules(new ServerModule())
            .enableAutoConfig(getClass().getPackage().getName())
            .build());
    }

    @Override
    public void run(final StacksApiProxyConfiguration configuration,
                    final Environment environment) {
        Injector injector = InjectorLookup.getInjector(this).get();


        var kong = injector.getInstance(KongApiClient.class);

        kong.waitTillAlive();

        log.info("current upstreams: {}", kong.upstreams());

        var health = injector.getInstance(StacksApiHealthCheck.class);
        health.start();

        var configurator = injector.getInstance(KongConfigurator.class);
        configurator.start();
    }
}
