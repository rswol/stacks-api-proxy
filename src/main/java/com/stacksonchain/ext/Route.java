package com.stacksonchain.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class Route {

    @JsonProperty("response_buffering")
    boolean responseBuffering;
    @JsonProperty("paths")
    String paths;
    @JsonProperty("methods")
    String methods;
    @JsonProperty("snis")
    String snis;
    @JsonProperty("https_redirect_status_code")
    int httpsRedirectStatusCode;
    @JsonProperty("preserve_host")
    boolean preserveHost;
    @JsonProperty("tags")
    String tags;
    @JsonProperty("strip_path")
    boolean stripPath;
    @JsonProperty("headers")
    String headers;
    @JsonProperty("id")
    String id;
    @JsonProperty("request_buffering")
    boolean requestBuffering;
    @JsonProperty("name")
    String name;
    @JsonProperty("created_at")
    long createdAt;
    @JsonProperty("path_handling")
    String pathHandling;
    @JsonProperty("updated_at")
    long updatedAt;
    @JsonProperty("regex_priority")
    int regexPriority;
    @JsonProperty("protocols")
    List<String> protocols;
    @JsonProperty("sources")
    String sources;
    @JsonProperty("service")
    Service service;
    @JsonProperty("destinations")
    List<String> destinations;
    @JsonProperty("hosts")
    List<String> hosts;
}
