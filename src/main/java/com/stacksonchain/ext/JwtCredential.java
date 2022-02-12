package com.stacksonchain.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtCredential {

  @JsonProperty("algorithm")
  final String algorithm;

  @JsonProperty("key")
  final String key;

  @JsonProperty("id")
  final String id;

  @JsonProperty("secret")
  final String secret;
}
