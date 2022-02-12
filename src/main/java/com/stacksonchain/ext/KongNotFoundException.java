package com.stacksonchain.ext;

import com.fasterxml.jackson.databind.JsonNode;

public class KongNotFoundException extends KongException {

  public KongNotFoundException(JsonNode body) {
    super(404, body);
  }
}
