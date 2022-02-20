package com.stacksonchain.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
}