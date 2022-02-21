package com.stacksonchain;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.hibernate.validator.constraints.*;
import javax.validation.constraints.*;

public class StacksApiProxyConfiguration extends Configuration {

  @NotNull
  public List<String> referenceApiNodes;

  @NotNull
  public List<String> endpoints;

  @NotNull
  public String kong;

  @NotNull
  public String proxyPath;

  @NotNull
  public String frontendHostname;

  public int driftTolerance = 1;
}
