package com.stacksonchain.core;

import com.stacksonchain.StacksApiProxyConfiguration;
import com.stacksonchain.stacksapi.StacksApiClient;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class StacksApiHealthCheck {

  enum Status { HEALTHY, UNHEALTHY };

  final StacksApiProxyConfiguration configuration;
  final Function<String, StacksApiClient> apiClient;
  final ScheduledExecutorService scheduler;

  final Map<String, Status> state = new ConcurrentHashMap<>();

  long maxBlockHeight = 48000;

  final String mainStacksApiEndpoint = "https://stacks-node-api.mainnet.stacks.co";

  public void start() {
    for (var url: configuration.endpoints) {
      state.put(url, Status.UNHEALTHY);
    }

    scheduler.scheduleWithFixedDelay(this::poll, 10, 10, TimeUnit.SECONDS);
    scheduler.scheduleWithFixedDelay(this::setMaxBlockHeight, 2, 2, TimeUnit.MINUTES);
    log.info("Initial poll");
    setMaxBlockHeight();
    poll();
  }

  public Iterable<String> healthyBackends() {
    return state.entrySet().stream()
        .filter(e -> e.getValue().equals(Status.HEALTHY))
        .map(Entry::getKey)
        .collect(Collectors.toList());
  }

  void poll() {
    for (var url: state.keySet()) {
      try {
        var info = apiClient.apply(String.format("http://%s", url)).info();
        var status = info.getStacksTipHeight() >= maxBlockHeight ?
            Status.HEALTHY : Status.UNHEALTHY;
        log.info("Marking {} as {}, block {}", url, status, info.getStacksTipHeight());
        maxBlockHeight = Math.max(info.getStacksTipHeight(), maxBlockHeight);
        state.put(url, status);
      } catch (Exception ex) {
        log.info("Marking {} as UNHEALTHY, {}", url, ex.getMessage());
        state.put(url, Status.UNHEALTHY);
      }
    }
  }

  void setMaxBlockHeight() {
    try {
      var info = apiClient.apply(mainStacksApiEndpoint).info();
      maxBlockHeight = Math.max(info.getStacksTipHeight(), maxBlockHeight);
      log.info("Max block height: {}", maxBlockHeight);
    } catch (Exception ex) {
      log.warn("Failed to poll {} for block height {}", mainStacksApiEndpoint, ex.getMessage());
    }
  }
}
