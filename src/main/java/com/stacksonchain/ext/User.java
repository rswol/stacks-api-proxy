package com.stacksonchain.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {

  @JsonProperty("username")
  final String username;

  @JsonProperty("id")
  final String id;

  @JsonProperty("custom_id")
  final String customId;
}
