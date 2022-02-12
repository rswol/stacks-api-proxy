package com.stacksonchain.ext;

import static org.junit.Assert.*;

import java.net.URI;
import lombok.SneakyThrows;
import org.junit.Test;

public class KongApiClientTest {

  @Test
  @SneakyThrows
  public void create() {
    var client = new KongApiClient(new URI("http://localhost:8001"));
    var res = client.createUpstream("test7");
    System.out.println("result = " + res);

    var target = client.createTarget("test7", "target1");
    System.out.println(target);
  }

  @Test
  @SneakyThrows
  public void createService() {
    var client = new KongApiClient(new URI("http://localhost:8001"));
    var res = client.createService("s2", "host1", "/v1");
    System.out.println("result = " + res);
  }

  @Test
  @SneakyThrows
  public void createRoutes() {
    var client = new KongApiClient(new URI("http://localhost:8001"));
    var res = client.createRoute("s2", "host1", "host2");
    System.out.println("result = " + res);
  }

  @Test
  @SneakyThrows
  public void listUpstreams() {
    var client = new KongApiClient(new URI("http://localhost:8001"));
    var res = client.upstreams();
    System.out.println("result = " + res);
  }

  @Test
  @SneakyThrows
  public void test1() {
    var client = new KongApiClient(new URI("http://localhost:8001"));
    var uri = client.buildPath("upstreams", "upstreamName", "targets");
    System.out.println(uri);
  }

  @Test
  @SneakyThrows
  public void listUsers() {
    var client = new KongApiClient(new URI("http://localhost:8001"));
    System.out.println(client.listUsers());
  }

  @Test
  @SneakyThrows
  public void getUser() {
    var client = new KongApiClient(new URI("http://localhost:8001"));
    System.out.println(client.getUser("user22"));
  }

}