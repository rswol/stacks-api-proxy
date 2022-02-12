package com.stacksonchain.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.stacksonchain.ext.KongApiClient;
import java.net.URI;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class JwtMakerTest {

  @Test
  public void createJwtTest() {
    var algorithm = Algorithm.HMAC256("5yIg3BnlHwj7tg2GXMskHGTphO7Ddu31");
    var jwt = JWT.create()
        .withClaim("iss", "tJf6CsOEPx2RPtXu3QXfxjngiYEUO5mk")
        .sign(algorithm);

    Assertions.assertThat(jwt)
        .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
            + ".eyJpc3MiOiJ0SmY2Q3NPRVB4MlJQdFh1M1FYZnhqbmdpWUVVTzVtayJ9"
            + ".rRFzzzlcAGg9VhVgmKXwBSg-oUXASWvpUwiUrWLVQBY");
  }

  @Test
  @SneakyThrows
  public void ensureUser() {
    var kong = new KongApiClient(new URI("http://localhost:8001"));
    var jwtMaker = new JwtMaker(kong);
    System.out.println(jwtMaker.ensureUser("xxx1"));
  }

  @Test
  @SneakyThrows
  public void getUserJwt() {
    var kong = new KongApiClient(new URI("http://localhost:8001"));
    var jwtMaker = new JwtMaker(kong);
    System.out.println(jwtMaker.userJwt("xxx2"));
  }

}