package com.stacksonchain.module;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.stacksonchain.StacksApiProxyConfiguration;
import com.stacksonchain.core.JwtMaker;
import com.stacksonchain.core.StacksApiHealthCheck;
import com.stacksonchain.ext.KongApiClient;
import com.stacksonchain.stacksapi.StacksApiClient;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import lombok.SneakyThrows;

public class ServerModule implements Module {

  @Override
  public void configure(Binder binder) {
    binder.bind(StacksApiHealthCheck.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  Function<String, StacksApiClient> getApiClient() {
    return apiNode -> {
      var client = new StacksApiClient(apiNode);
      client.setTimeout(Duration.of(10, ChronoUnit.SECONDS));
      return client;
    };
  }

  @Provides
  @Singleton
  @SneakyThrows
  KongApiClient getKong(StacksApiProxyConfiguration configuration) {
    return new KongApiClient(new URI(configuration.kong));
  }

  @Provides
  @Singleton
  ScheduledExecutorService scheduledExecutorService() {
    return Executors.newScheduledThreadPool(4);
  }

  @Provides
  @Singleton
  JwtMaker jwtMaker(KongApiClient kong) {
    return new JwtMaker(kong);
  }
}
