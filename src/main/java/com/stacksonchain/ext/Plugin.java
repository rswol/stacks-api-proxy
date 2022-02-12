package com.stacksonchain.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Plugin {

  @JsonProperty("name")
  final String name;

  @JsonProperty("id")
  final String id;
}
