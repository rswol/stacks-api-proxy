package com.stacksonchain.ext;

import com.auth0.jwt.JWT;
import com.konghq.testcontainers.KongContainer;
import com.stacksonchain.core.JwtMaker;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public class KongApiClientTest {

  static KongContainer kong;

  @BeforeClass
  public static void setup() {
    /*
     docker run -d --name kong \
    --link kong-database:kong-database \
    -e "KONG_DATABASE=postgres" \
    -e "KONG_PG_HOST=kong-database" \
    -e "KONG_PG_PASSWORD=kong" \
     \
    -e "KONG_PROXY_ACCESS_LOG=/dev/stdout" \
    -e "KONG_ADMIN_ACCESS_LOG=/dev/stdout" \
    -e "KONG_PROXY_ERROR_LOG=/dev/stderr" \
    -e "KONG_ADMIN_ERROR_LOG=/dev/stderr" \
    -e "KONG_ADMIN_LISTEN=0.0.0.0:8001, 0.0.0.0:8444 ssl" \
    -p 8000:8000 \
    -p 8443:8443 \
    -p 8001:8001 \
    -p 8444:8444 \
    kong
     */
    //var db = pg.getEmbeddedPostgres();

    var network = Network.newNetwork();

    var db = new GenericContainer<>(DockerImageName.parse("postgres:9.6-alpine"))
        .withNetwork(network)
        .withNetworkAliases("kong-database")
        .withEnv("POSTGRES_DB", "kong")
        .withEnv("POSTGRES_USER", "kong")
        .withEnv("POSTGRES_PASSWORD", "kong")
        .withExposedPorts(5432);
    db.start();


    var bootstrap = new KongContainer(DockerImageName.parse("kong:2.6.0-alpine"))
        .withNetwork(network)
        .withEnv("KONG_DATABASE", "postgres")
        .withEnv("KONG_PG_HOST", "kong-database")
        .withEnv("KONG_PG_USER", "kong")
        .withEnv("KONG_PG_PASSWORD", "kong")
        .withCommand("kong migrations bootstrap");
    bootstrap.start();


    kong = new KongContainer(DockerImageName.parse("kong:2.6.0-alpine"))
        .withNetwork(network)
        .withEnv("KONG_DATABASE", "postgres")
        .withEnv("KONG_PG_HOST", "kong-database")
        //.withEnv("KONG_PG_HOST", "kong-database")
        .withEnv("KONG_PG_USER", "kong")
        .withEnv("KONG_PG_PASSWORD", "kong");

        //.withClasspathResourceMapping("kong.yml", "/opt/kong/kong.yaml", BindMode.READ_ONLY);
    kong.start();
  }

  @Test
  @SneakyThrows
  public void create() {
    var client = new KongApiClient(new URI(kong.getaAdminUrl()));
    var res = client.createUpstream("test7");
    Assertions.assertThat(res.getName()).isEqualTo("test7");
    var target = client.createTarget("test7", "target1");
    Assertions.assertThat(target.getTarget()).isEqualTo("target1:8000");
  }

  @Test
  @SneakyThrows
  public void createService() {
    var client = new KongApiClient(new URI(kong.getaAdminUrl()));
    var res = client.createService("s2", "host1", "/v1");
    Assertions.assertThat(res.getPath()).isEqualTo("/v1");
    Assertions.assertThat(res.getName()).isEqualTo("s2");
  }

  @Test
  @SneakyThrows
  public void createRoutes() {
    var client = new KongApiClient(new URI(kong.getaAdminUrl()));
    var res = client.createRoute("s2", "host1", "host2");
    Assertions.assertThat(res.getHosts())
        .containsExactly("host1", "host2");
  }

  @Test
  @SneakyThrows
  public void getUpstream() {
    var client = new KongApiClient(new URI(kong.getaAdminUrl()));
    var res = client.createUpstream("up1");
    Assertions.assertThat(res.getName()).isEqualTo("up1");
  }

  @Test
  @SneakyThrows
  public void getUser() {
    var client = new KongApiClient(new URI(kong.getaAdminUrl()));
    var user = client.createUser("user22");
    Assertions.assertThat(client.getUser("user22"))
        .isEqualTo(user);

    Assertions.assertThat(client.listUsers())
        .containsExactly(user);
  }


  @Test
  @SneakyThrows
  public void createJwtTest() {

    var client = new KongApiClient(new URI(kong.getaAdminUrl()));
    var jwtMaker = new JwtMaker(client);
    Assertions.assertThat(jwtMaker.ensureUser("xxx1"))
        .isNotNull();

    var newJwt = jwtMaker.userJwt("xxx2");

    var creds = client.listJwtCredentials("xxx2");
    Assertions.assertThat(creds)
            .hasSize(1);

    var key = creds.get(0).key;

    var decodedJWT = JWT.decode(newJwt);
    var payload = new String(Base64.getDecoder().decode(decodedJWT.getPayload()), StandardCharsets.UTF_8);
    Assertions.assertThat(payload.contains(key)).isTrue();
  }
}