package com.stacksonchain.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.stacksonchain.ext.KongApiClient;
import com.stacksonchain.ext.KongNotFoundException;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class JwtMaker {

  @Inject
  final KongApiClient kong;

  public String ensureUser(String username) {
    try {
      return kong.getUser(username).getId();
    } catch (KongNotFoundException ex) {
      return kong.createUser(username).getId();
    }
  }

  public String userJwt(String username) {
    var userId = ensureUser(username);
    var credentials = kong.listJwtCredentials(userId);
    if (credentials.isEmpty()) {
      credentials.add(kong.createJwtCredential(username));
    }
    var credential = credentials.get(0);
    return createJwt(credential.getSecret(), credential.getKey());
  }

  String createJwt(String secret, String key) {
    var algorithm = Algorithm.HMAC256(secret);
    var jwt = JWT.create()
        .withClaim("iss", key)
        //.withExpiresAt(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
        .sign(algorithm);
    return jwt;
  }

}
