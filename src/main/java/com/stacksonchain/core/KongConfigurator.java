package com.stacksonchain.core;

import com.stacksonchain.StacksApiProxyConfiguration;
import com.stacksonchain.ext.KongApiClient;
import com.stacksonchain.ext.Plugin;
import com.stacksonchain.ext.Route;
import com.stacksonchain.ext.Service;
import com.stacksonchain.ext.Target;
import com.stacksonchain.ext.Upstream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KongConfigurator {

  static final String upstreamName = "stacks";
  static final String serviceName = "proxy";

  @Inject
  KongApiClient kong;

  @Inject
  StacksApiProxyConfiguration configuration;

  @Inject
  StacksApiHealthCheck apiHealthCheck;

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public void start() {
    scheduler.scheduleAtFixedRate(this::setup, 20, 20, TimeUnit.SECONDS);
    log.info("Initial setup");
    setup();
  }

  void setup() {
    ensureUpstream();
    ensureService();
    ensureBackends();
    ensureRoutes();
  }

  Upstream ensureUpstream() {
    var upstreams = kong.upstreams();
    for (var u: upstreams) {
      if (upstreamName.equals(u.getName())) {
        return u;
      }
    }
    return kong.createUpstream(upstreamName);
  }

  Service ensureService() {
    var services = kong.services();
    for (var s: services) {
      if (s.getName().equals(serviceName)) {
        if (!configuration.proxyPath.equals(s.getPath())) {
          kong.deleteService(s);
          break;
        }
        return s;
      }
    }
    return kong.createService(serviceName, upstreamName, configuration.proxyPath);
  }

  Set<Target> ensureBackends() {
    var healthy = apiHealthCheck.healthyBackends();
    log.info("Healthy backends: {}", healthy);
    var res = new HashSet<Target>();
    var targets = kong.targets(upstreamName);
    for (var backend: healthy) {
      if (targets.stream()
          .noneMatch(target -> backend.equals(target.getTarget()))) {
        log.info("Adding {} route", backend);
        var target = kong.createTarget(upstreamName, backend);
        res.add(target);
      }
    }
    /* remove stale targets */
    var allTargetNames = new HashSet<>(targets);
    healthy.forEach(endpoint -> {
      allTargetNames.removeIf(target -> endpoint.equals(target.getTarget()));
    });
    for (var target: allTargetNames) {
      log.info("Removing {} route", upstreamName);
      kong.deleteTarget(upstreamName, target.getId());
    }

    return res;
  }

  Route ensureRoutes() {
    var routes = kong.routes(serviceName);
    for (var r: routes) {
      if (r.getHosts().stream()
          .anyMatch(host -> host.equals(configuration.frontendHostname))) {
        return r;
      }
    }
    return kong.createRoute(serviceName, configuration.frontendHostname);
  }


  public boolean isAuth() {
    for (var plugin : kong.listPlugins(serviceName)) {
      if ("jwt".equals(plugin.getName())) {
        return true;
      }
    }
    return false;
  }

  public Plugin authOn() {
    for (var plugin : kong.listPlugins(serviceName)) {
      if ("jwt".equals(plugin.getName())) {
        return plugin;
      }
    }
    return kong.createPlugin(serviceName, "jwt");
  }

  public void authOff() {
    for (var plugin : kong.listPlugins(serviceName)) {
      if ("jwt".equals(plugin.getName())) {
        kong.deletePlugin(serviceName, plugin.getId());
      }
    }
  }
}
