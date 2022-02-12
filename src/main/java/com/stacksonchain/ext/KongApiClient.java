package com.stacksonchain.ext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
@RequiredArgsConstructor
public class KongApiClient {

  final URI kongEndpoint;

  HttpClient httpClient = HttpClient.newHttpClient();

  public List<Upstream> upstreams() {
    return list(buildPath("upstreams"), new TypeReference<KongResponse<Upstream>>() {});
  }

  public List<Service> services() {
    return list(buildPath("services"), new TypeReference<KongResponse<Service>>() {});
  }

  public List<Target> targets(String upstream) {
    return list(buildPath("upstreams", upstream, "targets"), new TypeReference<KongResponse<Target>>() {});
  }

  public List<Route> routes(String service) {
    return list(buildPath("services", service, "routes"), new TypeReference<KongResponse<Route>>() {});
  }

  public Upstream createUpstream(String name) {
    return create(
        buildPath("upstreams"),
        Upstream.class,
        "name", name);
  }

  public Target createTarget(String upstreamName, String target) {
    return create(
        buildPath("upstreams", upstreamName, "targets"),
        Target.class,
        "target", target, "weight", "100");
  }

  public Service createService(String name, String upstream, String path) {
    return create(
        buildPath("services"),
        Service.class,
        "name", name, "host", upstream, "path", path);
  }

  public void deleteService(Service service) {
    var routes = routes(service.getName());
    for (var r: routes) {
      deleteRoute(service.getName(), r.getId());
    }
    delete(
        buildPath("services", service.getName()));
  }

  public void deleteRoute(String service, String route) {
    delete(buildPath("services", service, "routes", route));
  }

  public void deleteTarget(String upstream, String target) {
    delete(buildPath("upstreams", upstream, "targets", target));
  }

  public Route createRoute(String service, String... hosts) {
    String[] hostsParams = new String[hosts.length * 2];
    for (int i = 0; i < hosts.length; i++) {
      hostsParams[2*i] = "hosts[]";
      hostsParams[2*i+1] = hosts[i];
    }
    return create(
        buildPath("services", service, "routes"),
        Route.class,
        hostsParams);
  }

  public User createUser(String username) {
    return create(
        buildPath("consumers"),
        User.class,
        "username", username);
  }

  public List<User> listUsers() {
    return list(buildPath("consumers"), new TypeReference<KongResponse<User>>() {
    });
  }

  public User getUser(String username) {
    return get(buildPath("consumers", username), User.class);
  }

  public JwtCredential createJwtCredential(String username) {
    return create(
        buildPath("consumers", username, "jwt"),
        JwtCredential.class);
  }

  public List<JwtCredential> listJwtCredentials(String username) {
    return list(buildPath("consumers", username, "jwt"),
        new TypeReference<KongResponse<JwtCredential>>() {
        });
  }

  public Plugin getPlugin(String service, String id) {
    return get(buildPath("services", service, "plugins", id), Plugin.class);
  }

  // plugins
  public Plugin createPlugin(String service, String name) {
    return create(buildPath("services", service, "plugins"),
        Plugin.class,
        "name", name);
  }

  public List<Plugin> listPlugins(String service) {
    return list(buildPath("services", service, "plugins"),
        new TypeReference<KongResponse<Plugin>>() {
        });
  }

  public void deletePlugin(String service, String id) {
    delete(buildPath("services", service, "plugins", id));
  }

  @SneakyThrows
  <T> T create(URI uri, Class<T> createClass, String... params) {
    log.info("kong create {}", uri);
    var request =  post(uri, params);
    var response = httpClient.send(request, BodyHandlers.ofInputStream());
    if (response.statusCode() == 201) {
      return mapper.readValue(response.body(), createClass);
    }
    throw KongException.make(response);
  }

  @SneakyThrows
  void delete(URI uri) {
    log.info("kong delete {}", uri);
    var request = HttpRequest.newBuilder()
        .uri(uri)
        .DELETE()
        .build();
    var response = httpClient.send(request, BodyHandlers.ofInputStream());
    if (response.statusCode() < 200 || response.statusCode() > 299) {
      throw KongException.make(response);
    }
  }


  HttpRequest post(URI uri, String... keyValues) {
    var parameters = new ArrayList<Pair<String, String>>();
    for (int i = 0; i < keyValues.length / 2; i++) {
      parameters.add(Pair.of(keyValues[2*i], keyValues[2*i + 1]));
    }

    String form = parameters.stream()
        .map(pair -> pair.getKey() + "=" + URLEncoder.encode(pair.getValue(), StandardCharsets.UTF_8))
        .collect(Collectors.joining("&"));

    return HttpRequest.newBuilder()
        .uri(uri)
        .headers("Content-Type", "application/x-www-form-urlencoded")
        .POST(BodyPublishers.ofString(form)).build();
  }

  URI buildPath(String... parts) {
    var builder = UriBuilder.fromUri(kongEndpoint);
    for (var part: parts) {
      builder.path(part);
    }
    return builder.build();
  }

  @SneakyThrows
  <R> R get(URI uri, Class<R> resultClass) {
    var request = HttpRequest.newBuilder()
        .uri(uri)
        .build();
    var response = httpClient.send(request, BodyHandlers.ofInputStream());
    if (response.statusCode() == 200) {
      var result = mapper.readValue(response.body(), resultClass);
      return result;
    }
    throw KongException.make(response);
  }

  @SneakyThrows
  <R> List<R> list(URI uri, TypeReference<KongResponse<R>> resultClass) {
    var request = HttpRequest.newBuilder()
        .uri(uri)
        .build();
    var response = httpClient.send(request, BodyHandlers.ofInputStream());
    if (response.statusCode() == 200) {
      var kongRes = mapper.readValue(response.body(), resultClass);
      return kongRes.data;
    }
    throw KongException.make(response);
  }

  static ObjectMapper mapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

}
