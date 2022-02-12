package com.stacksonchain.ext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class KongException extends RuntimeException {
  final int httpCode;

  public KongException(int httpCode, JsonNode body) {
    super(body.toString());
    this.httpCode = httpCode;
    this.body = body;
  }

  final JsonNode body;

  @SneakyThrows
  public static KongException make(HttpResponse<InputStream> response) {
    var message = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
    log.error("Kong returned {} {}", response.statusCode(),
        message);
    if (response.statusCode() == 404) {
      return new KongNotFoundException(mapper.readTree(message));
    } else {
      return new KongException(response.statusCode(), mapper.readTree(message));
    }
  }

  static ObjectMapper mapper = new ObjectMapper();
}
