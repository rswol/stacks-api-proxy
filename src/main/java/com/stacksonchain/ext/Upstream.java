package com.stacksonchain.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Upstream {

  String algorithm;

  @JsonProperty("slots")
  String slots;
  @JsonProperty("name")
  String name;
  @JsonProperty("host_header")
  String hostHeader;
  @JsonProperty("created_at")
  String createdAt;
  @JsonProperty("id")
  String id;
  @JsonProperty("hash_on")
  String hashOn;
  @JsonProperty("client_certificate")
  String clientCertificate;
}
