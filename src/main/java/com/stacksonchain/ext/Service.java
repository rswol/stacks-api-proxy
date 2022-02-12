package com.stacksonchain.ext;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Service {

    @JsonProperty("path")
    String path;
    @JsonProperty("tags")
    String tags;
    @JsonProperty("ca_certificates")
    String caCertificates;
    @JsonProperty("write_timeout")
    long writeTimeout;
    @JsonProperty("created_at")
    long createdAt;
    @JsonProperty("id")
    String id;
    @JsonProperty("host")
    String host;
    @JsonProperty("tls_verify_depth")
    String tlsVerifyDepth;
    @JsonProperty("protocol")
    String protocol;
    @JsonProperty("read_timeout")
    long readTimeout;
    @JsonProperty("retries")
    int retries;
    @JsonProperty("connect_timeout")
    long connectTimeout;
    @JsonProperty("name")
    String name;
    @JsonProperty("updated_at")
    long updatedAt;
    @JsonProperty("tls_verify")
    String tlsVerify;
    @JsonProperty("port")
    int port;
    @JsonProperty("enabled")
    String enabled;
    @JsonProperty("client_certificate")
    String clientCertificate;
}
