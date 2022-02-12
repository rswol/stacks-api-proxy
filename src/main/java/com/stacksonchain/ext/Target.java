package com.stacksonchain.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Target {

  @Data
  class Upstream {
    String id;
  }

  Upstream upstream;

  @JsonProperty("weight")
  String weight;
  @JsonProperty("created_at")
  String createdAt;
  @JsonProperty("id")
  String id;
  @JsonProperty("tags")
  String tags;
  @JsonProperty("target")
  String target;

}
